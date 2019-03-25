package reptiles;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReptilesApplicationTests {


	@Resource
	RestTemplate restTemplate;

	@Test
	public void contextLoads() {


		String url = "http://www.kuwo.cn/bang/content?name=酷我热歌榜&bangId=16";
		ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
		String body = forEntity.getBody();
		Document html = Jsoup.parse(body);

		Elements listMusic = html.getElementsByClass("listMusic");

	}

}
