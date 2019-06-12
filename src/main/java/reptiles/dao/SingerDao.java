package reptiles.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import reptiles.pojo.SingerEntity;

import javax.transaction.Transactional;

/**
 * @Auther: 大叔
 * @Time: 2019/6/10 15:00
 */
@Transactional
public interface SingerDao extends JpaRepository<SingerEntity, Long>, JpaSpecificationExecutor<SingerEntity> {
}
