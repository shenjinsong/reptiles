package reptiles.pojo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: 大叔
 * @Time: 2019/3/25 10:42
 */
@Entity
@Table(name = "t_music")
@Data
public class MusicEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @Column(name = "music_id")
    private String musicId;

    @Column(name = "song_url")
    private String songUrl;

    @Column(name = "singer")
    private String singer;

    @Column(name = "music")
    private String music;

    @Column(name = "create_time")
    private Date createTime;

    @Version
    private Long version;
}
