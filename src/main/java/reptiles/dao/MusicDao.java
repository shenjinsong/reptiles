package reptiles.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import reptiles.pojo.MusicEntity;

import java.util.List;

/**
 * @Auther: 大叔
 * @Time: 2019/3/25 14:30
 */
public interface MusicDao extends JpaRepository<MusicEntity, Long> {

    List<MusicEntity> queryMusicEntitiesByMusic(String music);

    List<MusicEntity> queryByMusic(String music);

    MusicEntity getTopBySinger(String singer);

    MusicEntity getFirstBySinger(String singer);

    @Query(value = "select `id`,music_id,song_url,singer,music,create_time from  t_music where  singer = ?1 and music = ?2 limit 1", nativeQuery = true)
    MusicEntity selectBySql(String singer,  String music);

//    @Query(value = "select id,music_id,song_url,singer,music,create_time from  t_music where  singer = :singer and music = :music ", nativeQuery = true)
//    MusicEntity selectBySql(@Param("singer") String singer, @Param("music") String music);

}
