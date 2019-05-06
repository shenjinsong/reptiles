package reptiles.pojo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    private Date createTime;

    @Transient //此注解表示不保存到db
    @Temporal(TemporalType.DATE)
    private Calendar updateDate;

    @Version
    private Long version;



    @Override
    public String toString() {
        return "MusicEntity{" +
                "Id=" + Id +
                ", musicId='" + musicId + '\'' +
                ", songUrl='" + songUrl + '\'' +
                ", singer='" + singer + '\'' +
                ", music='" + music + '\'' +
                ", createTime=" + createTime +
                ", version=" + version +
                '}';
    }
}
