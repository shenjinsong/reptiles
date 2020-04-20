package reptiles.service.impl;

import com.alibaba.fastjson.JSON;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import reptiles.dao.MusicDao;
import reptiles.pojo.MusicEntity;
import reptiles.service.KuwoMusicAcquireService;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @Auther: 大叔
 * @Time: 2019/3/15 10:44
 */
@Service
public class KuwoMusicAcquireServiceImpl implements KuwoMusicAcquireService {

    private static String searchMusic, getXML, list4Pid, list4Rank, searchSinger, pageRequest;
    private static Long pageSize;

    static {
        pageSize = 20L;
        searchMusic = "http://sou.kuwo.cn/ws/NSearch?type=all&catalog=yueku2016&key={1}";
        getXML = "http://player.kuwo.cn/webmusic/st/getNewMuiseByRid?rid=MUSIC_{1}";
        list4Pid = "http://www.kuwo.cn/playlist/content?pid={1}";
        list4Rank = "http://www.kuwo.cn/bang/content?name={1}&bangId={2}";
        searchSinger = "http://www.kuwo.cn/artist/content?name={1}";
        pageRequest = "http://www.kuwo.cn/artist/contentMusicsAjax?artistId={artistId}&pn={pageNum}&rn={pageSize}";
    }

    @Resource
    private MusicDao musicDao;

    @Resource
    private RestTemplate restTemplate;

    @Override
    public void searchSinger(String singer) {
        ResponseEntity<String> entity = restTemplate.getForEntity(searchSinger, String.class, singer);
        String body = entity.getBody();
        if (body == null) return;
        Document document = Jsoup.parse(body);

        String artistId = document.getElementsByAttribute("data-artistid").attr("data-artistid");  // 歌手Id
        Elements listMusic = document.getElementsByClass("listMusic");
        String pageSize = listMusic.attr("data-rn"); // 每页数量
        String pageTotal = listMusic.attr("data-page"); // 总页数

        long total = Long.valueOf(pageSize) * Long.valueOf(pageTotal);

        int pageNum = 1;
        Map<String, Object> map = new HashMap<>();
        map.put("artistId", artistId);
        map.put("pageSize", this.pageSize);

        while ((pageNum - 1) * this.pageSize < total) {
            map.put("pageNum", pageNum++ - 1);
            this.save2DB(this.selectResult(restTemplate.getForEntity(pageRequest, String.class, map).getBody()));
        }
    }

    @Override
    public Object getMusicByMusicId(String musicId) {

        ResponseEntity<String> responseXML = restTemplate.getForEntity(getXML, String.class, musicId);
        if (responseXML.getBody() == null || responseXML.getBody().length() == 0) {
            return "XML中的body为空";
        }

        Document xml = Jsoup.parse(responseXML.getBody());

        String mp3dlStr = xml.getElementsByTag("mp3dl").text();
        String mp3pathStr = xml.getElementsByTag("mp3path").text();

        String name = xml.getElementsByTag("name").text();
        String singer = xml.getElementsByTag("singer").text();

        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(singer)) {
            return "musicId" + musicId + "信息不全！";
        }
        String musicName = name + " - " + singer;
        String filePath = "http://" + mp3dlStr + "/resource/" + mp3pathStr;

        MusicEntity musicEntity = new MusicEntity();
        musicEntity.setCreateTime(new Date());
        musicEntity.setSinger(singer);
        musicEntity.setMusic(name);
        musicEntity.setSongUrl(filePath);
        musicEntity.setMusicId(musicId);

        MusicEntity music = musicDao.save(musicEntity);
        System.out.println("获取:\t" + JSON.toJSONString(music));

        try {
            this.outputMusic(filePath, musicName); // 通过NIO下载到本地
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "获取:\t" + musicName;

    }

    @Override
    public Object searchMusic(String searchMusicName, String searchSingerName) {


        ResponseEntity<String> entity = restTemplate.getForEntity(searchMusic, String.class, searchMusicName);
        String body = entity.getBody();
        if (body == null || body.length() == 0) {
            return "未查询到" + searchMusicName;
        }

        Document document = Jsoup.parse(body);

        Elements div = document.select("div [ class = m_list clearfix ]");
        Elements li = div.select("li");
        if (li.size() == 0) {
            return "div 中无 li 标签";
        }
        Element element = li.get(0);
        Elements input = element.getElementsByTag("input");
        String musicId = input.attr("value");

        String singerName = element.getElementsByClass("s_name").select("a").attr("title");
        String musicName = element.getElementsByClass("m_name").select("a").attr("title");

        if (!searchMusicName.equals(musicName)) {
            return musicName + "\n查询到的音乐不符合要求，终止!";
        }
        musicName = musicName + " - " + singerName;

        if (searchSingerName != null && searchSingerName.length() != 0 && !searchSingerName.equals(singerName)) {
            return musicName + "\n 查询到的音乐不符合要求，exit!";
        }

        return getMusicByMusicId(musicId);
    }

    private void outputMusic(String filePath, String musicName) throws IOException {

        // 使用NIO下载网络文件
        ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(filePath).openStream());
        FileOutputStream outputStream = new FileOutputStream("C:/Users/Administrator/Desktop/" + musicName + ".mp3");
        FileChannel fileChannel = outputStream.getChannel();
        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

        // 使用普通IO流下载文件
//        URL url = new URL(filePath);
//
//        URLConnection conn = url.openConnection();
//        InputStream inputStream = conn.getInputStream();
//        FileOutputStream outputStream = new FileOutputStream("C:/Users/Administrator/Desktop/Musics/" + musicName + ".mp3");
//
//        byte[] buffer = new byte[2048];
//        int byteread;
//        while ((byteread = inputStream.read(buffer)) != -1) {
//            outputStream.write(buffer, 0, byteread);
//        }
//
//        outputStream.flush();
//        outputStream.close();
//        inputStream.close();
    }

    @Override
    public Object getListByPid(String pid) {

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(list4Pid, String.class, pid);
        this.save2DB(this.selectResult(responseEntity.getBody()));
        return null;
    }

    private Elements selectResult(String body) {

        Assert.notNull(body, "body is null !");
        Document html = Jsoup.parse(body);
        Elements elements = html.getElementsByClass("listMusic");
        Element element = elements.get(0);
        return element.getElementsByAttribute("data-music");

    }


    private void save2DB(Elements elements) {
        synchronized (UUID.randomUUID()){
            elements.stream().map(music -> music.attr("data-music")).map(JSON::parseObject).map(jsonObject -> Pattern.compile("MUSIC_").matcher(jsonObject.getString("id")).replaceAll("")).forEachOrdered(this::getMusicByMusicId);
        }
    }

    @Override
    public void List4Rank(String name, String bangId) {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(list4Rank, String.class, name, bangId);
        this.save2DB(this.selectResult(responseEntity.getBody()));
    }
}
