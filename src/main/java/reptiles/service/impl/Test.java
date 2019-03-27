package reptiles.service.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;


/**
 * @Auther: 大叔
 * @Time: 2019/3/14 15:57
 */
public class Test {


    public static void main(String[] args) throws IOException {

        String searchHtml = "http://sou.kuwo.cn/ws/NSearch?type=all&catalog=yueku2016&key={1}";
        String getXML = "http://player.kuwo.cn/webmusic/st/getNewMuiseByRid?rid=MUSIC_";

        String music = "下雪的季节";

        ResponseEntity<String> entity = new RestTemplate().getForEntity(searchHtml, String.class, music);
        String body = entity.getBody();
        Document document = Jsoup.parse(body);

        Elements div = document.select("div [ class = m_list clearfix ]");
        Elements li = div.select("li");

//        Element element = li.get(0); // 取第一条
        for (Element element : li) { // 下载所有名称相同的music

            Elements input = element.getElementsByTag("input");

            String singerName = element.getElementsByClass("s_name").select("a").attr("title");
            String musicName = element.getElementsByClass("m_name").select("a").attr("title");

            musicName = musicName + " - " + singerName; // 名称会有乱码
            String musicId = input.attr("value");
            ResponseEntity<String> responseXML = new RestTemplate().getForEntity(getXML + musicId, String.class);

            String xmlBody = responseXML.getBody();
            Assert.notNull(xmlBody, "xmlBody is null");
            Document xml = Jsoup.parse(xmlBody);
            Elements mp3dl = xml.getElementsByTag("mp3dl");
            Elements mp3path = xml.getElementsByTag("mp3path");

            String mp3dlStr = mp3dl.text();
            String mp3pathStr = mp3path.text();

            String filePath = "http://" + mp3dlStr + "/resource/" + mp3pathStr;

            // 使用NIO下载网络文件
            ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(filePath).openStream());
            FileOutputStream outputStream = new FileOutputStream("C:/Users/Administrator/Desktop/" + musicName + ".mp3");
            FileChannel fileChannel = outputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);


        }
    }
}
