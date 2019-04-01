package reptiles;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reptiles.dao.MusicDao;
import reptiles.pojo.MusicEntity;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReptilesApplicationTests {


	@Resource
	private MusicDao musicDao;

	@Test
	public void contextLoads() {
		MusicEntity musicEntity = musicDao.queryById(2008L);
		System.out.println(musicEntity);
	}

}
