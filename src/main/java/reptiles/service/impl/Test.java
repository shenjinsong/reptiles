package reptiles.service.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;


/**
 * @Auther: 大叔
 * @Time: 2019/3/14 15:57
 */
public class Test {


    public static void main(String[] args) {

        String searchHtml = "http://sou.kuwo.cn/ws/NSearch?type=all&catalog=yueku2016&key={1}";
        String getXML = "http://player.kuwo.cn/webmusic/st/getNewMuiseByRid?rid=MUSIC_";

        ResponseEntity<String> entity = new RestTemplate().getForEntity(searchHtml, String.class, "下雪的季节");
        String body = entity.getBody();
        Document document = Jsoup.parse(body);

        Elements div = document.select("div [ class = m_list clearfix ]");
        Elements li = div.select("li");

        for (Element element : li) {

            Elements input = element.getElementsByTag("input");

            String singerName = element.getElementsByClass("s_name").select("a").attr("title");
            String musicName = element.getElementsByClass("m_name").select("a").attr("title");

            musicName = musicName + " - " + singerName;
            String musicId = input.attr("value");
            ResponseEntity<String> responseXML = new RestTemplate().getForEntity(getXML + musicId, String.class);

            Document xml = Jsoup.parse(responseXML.getBody());
            Elements mp3dl = xml.getElementsByTag("mp3dl");
            Elements mp3path = xml.getElementsByTag("mp3path");

            String mp3dlStr = mp3dl.text();
            String mp3pathStr = mp3path.text();

            String filePath = "http://" + mp3dlStr + "/resource/" + mp3pathStr;

            File file = new File(filePath);

            String musicPath = file.getPath();


        }


    }
}
