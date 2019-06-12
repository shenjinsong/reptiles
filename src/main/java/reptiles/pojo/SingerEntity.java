package reptiles.pojo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Auther: 大叔
 * @Time: 2019/6/10 14:36
 */
@Entity
@Table(name = "t_singer")
@Data
public class SingerEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "singer_id")
    private Long singerId;

    @Column(name = "name")
    private String name;

    @Column(name = "image")
    private String image;

    @Column(name = "information")
    private String information;

}
