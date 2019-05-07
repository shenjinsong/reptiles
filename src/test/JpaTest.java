import com.github.wenhao.jpa.Specifications;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import reptiles.ReptilesApplication;
import reptiles.dao.MusicDao;
import reptiles.pojo.MusicEntity;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @Auther: 大叔
 * @Time: 2019/5/5 16:43
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReptilesApplication.class)
public class JpaTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Resource
    private MusicDao musicDao;

    /**
     * 新增
     */
    @Test
    public void test() {

        MusicEntity music = new MusicEntity();
        music.setSinger("本兮");
        music.setMusic("创作者");
        music.setSongUrl("http://www.baidu.com/pic/oosln?sk89_gKJ=");
        music.setMusicId("1541");

        MusicEntity musicEntity = musicDao.save(music);
    }

    /**
     * 更新（@Version自动+1）
     */
    @Test
    public void test1() {

//        Optional<MusicEntity> optional = musicDao.findById(611L);
//        MusicEntity entity = optional.get();

        // getOne() 是懒加载
        MusicEntity entity = musicDao.getOne(2183L);
//        MusicEntity entity = musicDao.queryById(2183L);
        System.out.println(entity.toString());

        entity.setCreateTime(null);
        MusicEntity muisc = musicDao.save(entity);
        System.out.println(muisc.toString());

    }

    /**
     * 查询
     */
    @Test
    public void test2() {

        /**
         *  自定义sql查询
         */
        List<MusicEntity> musicList = musicDao.queryMusicbyMusicName("创作者");

        MusicEntity musicEntity = musicDao.queryById(454L);


        /**
         * 自定义接口查询
         */
        List<MusicEntity> musics = musicDao.queryByMusic("创作者");

        List<MusicEntity> lists = musicDao.findByCreateTimeGreaterThanEqualAndCreateTimeLessThan(new Date(), new Date());

        List<MusicEntity> list = musicDao.findByCreateTimeBetweenOrderByCreateTimeDesc(new Date(), new Date());

        /**
         * 使用jpa接口查询
         */
        Optional<MusicEntity> optional = musicDao.findById(545L);

        long count = musicDao.count();

        // findAll 方法
        MusicEntity mEx = new MusicEntity();
        mEx.setMusic("下雪的季节");
        Example<MusicEntity> example = Example.of(mEx);
        List<MusicEntity> all = musicDao.findAll(example);

        // Page 分页
        Page<MusicEntity> musicLists = musicDao.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createTime")));

        // Specification from <jpa-spec>
        Specification<MusicEntity> specification = new Specifications<MusicEntity>()
                .eq("singer", "本兮")
                .eq("music", "情花")
                .build();
        List<MusicEntity> list1 = musicDao.findAll(specification);

    }


    /**
     * 使用
     * JpaSpecificationExecutor
     * 动态条件查询
     */
    @Test
    public void test3() {

        MusicEntity musicEntity = new MusicEntity();
        musicEntity.setSinger("本兮");
        musicEntity.setCreateTime(new Date());

        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<?> page = musicDao.findAll((Specification<MusicEntity>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = Arrays.asList(
                    criteriaBuilder.lessThan(root.get("createTime").as(Date.class), musicEntity.getCreateTime()),
                    criteriaBuilder.equal(root.get("singer").as(String.class), musicEntity.getSinger())
            );

            Predicate[] predicate = new Predicate[predicates.size()];
            return criteriaBuilder.and(predicates.toArray(predicate));

        }, pageRequest);

    }

    /**
     * EntityManager 使用
     *      set 方法更新
     *
     */
    @Test
    @Transactional
    public void test4(){

        MusicEntity music = musicDao.getOne(611L);
        System.out.println(music.toString());

        entityManager.persist(music);
        music.setCreateTime(new Date());

    }


    @Test
    @Transactional
    public void test5(){

        MusicEntity music = musicDao.queryById(611L);
        entityManager.persist(music);
        music.setCreateTime(new Date());
        boolean contains = entityManager.contains(music);


    }

}
