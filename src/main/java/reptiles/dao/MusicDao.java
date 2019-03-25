package reptiles.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import reptiles.pojo.MusicEntity;

/**
 * @Auther: 大叔
 * @Time: 2019/3/25 14:30
 */
public interface MusicDao extends JpaRepository<MusicEntity, Long> {
}
