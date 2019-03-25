package reptiles.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import reptiles.dao.MusicDao;
import reptiles.pojo.MusicEntity;
import reptiles.service.MusicAcquireService;

import javax.annotation.Resource;
import javax.jws.WebResult;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @Auther: 大叔
 * @Time: 2019/3/15 10:44
 */
@Service
public class MusicAcquireServiceImpl implements MusicAcquireService {

    private static String searchHtml;
    private static String getXML;
    private static String list4Pid;
    private static String list4Rank;

    static {
        searchHtml = "http://sou.kuwo.cn/ws/NSearch?type=all&catalog=yueku2016&key={1}";
        getXML = "http://player.kuwo.cn/webmusic/st/getNewMuiseByRid?rid=MUSIC_{1}";
        list4Pid = "http://www.kuwo.cn/playlist/content?pid={1}";
        list4Rank = "http://www.kuwo.cn/bang/content?name={1}&bangId={2}";
    }

    @Resource
    MusicDao musicDao;

    @Resource
    RestTemplate restTemplate;

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

//        this.outputMusic(filePath, musicName); // 通过NIO下载到本地

        return "获取:\t" + musicName;

    }

    @Override
    public Object searchMusic(String searchMusicName, String searchSingerName) {


        ResponseEntity<String> entity = restTemplate.getForEntity(searchHtml, String.class, searchMusicName);
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
        FileOutputStream outputStream = new FileOutputStream("C:/Users/Administrator/Desktop/Musics/" + musicName + ".mp3");
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

        ResponseEntity<String> response = restTemplate.getForEntity(list4Pid, String.class, pid);

        return null;
    }

    @Override
    public void List4Rank(String name, String bangId) {

//        String url = "http://www.kuwo.cn/bang/content?name=" + name + "&bangId=" + bangId;
        ResponseEntity<String> response = restTemplate.getForEntity(list4Rank, String.class, name, bangId);
        String body = response.getBody();

        assert body != null;
        Document html = Jsoup.parse(body);

        Elements elements = html.getElementsByClass("listMusic");
        Element element = elements.get(0);

        Elements dataMusics = element.getElementsByAttribute("data-music");
        for (Element music : dataMusics) {

            String jsonStr = music.attr("data-music");
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            String musicId = Pattern.compile("MUSIC_").matcher(jsonObject.getString("id")).replaceAll("");

            this.getMusicByMusicId(musicId);

        }

    }
}
