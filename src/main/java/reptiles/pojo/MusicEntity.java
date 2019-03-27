package reptiles.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Auther: 大叔
 * @Time: 2019/3/25 10:42
 */
@Entity
@Table(name = "t_music")
@Data
public class MusicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    private String musicId;

    private String songUrl;

    private String singer;

    private String music;

    private Date createTime;
}
