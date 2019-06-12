package reptiles.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import reptiles.pojo.MusicEntity;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * @Auther: 大叔
 * @Time: 2019/3/25 14:30
 */
@Transactional
public interface MusicDao extends JpaRepository<MusicEntity, Long>, JpaSpecificationExecutor<MusicEntity> {

    List<MusicEntity> queryByMusic(String music);

    @Lock(LockModeType.READ)
    List<MusicEntity> queryByMusic(String music, Sort sort);

    MusicEntity getFirstBySinger(String singer);

    Stream<MusicEntity> queryBySinger(String singer);

    @Query(value = "select * from  t_music where  singer = ?1 and music = ?2 limit 1", nativeQuery = true)
    MusicEntity selectBySql(String singer, String music);

//    @Query(value = "select id,music_id,song_url,singer,music,create_time from  t_music where  singer = :singer and music = :music ", nativeQuery = true)
//    MusicEntity selectBySql(@Param("singer") String singer, @Param("music") String music);

    @Query(value = "select * from t_music where id = :id", nativeQuery = true)
    MusicEntity queryById(@Param("id") Long id);

    @Query("from MusicEntity where music = :music")
    List<MusicEntity> queryMusicbyMusicName(String music);

    List<MusicEntity> findByCreateTimeGreaterThanEqualAndCreateTimeLessThan(Date date1, Date date2);

    List<MusicEntity> findByCreateTimeBetweenOrderByCreateTimeDesc(Date date1, Date date2);


}
