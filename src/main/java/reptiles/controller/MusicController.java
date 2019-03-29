package reptiles.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reptiles.dao.MusicDao;
import reptiles.service.impl.MusicService;

import javax.annotation.Resource;
import javax.annotation.Resources;

/**
 * @Auther: 大叔
 * @Time: 2019/3/27 15:52
 */
@RestController
public class MusicController {

    @Resource
    private MusicService musicService;

    @Resource
    private MusicDao musicDao;

    @GetMapping("/singer")
    public Object singer(String singer){
        return musicService.findOneBySinger(singer);
    }

    @GetMapping("/music/find")
    public Object music(String music){
        return musicService.findByMusic(music);
    }

    @GetMapping("/find")
    public Object find(String str){
        return musicDao.getFirstBySinger(str);
    }

    @GetMapping("/find/{singer}/{music}")
    public Object find(@PathVariable("singer") String singer, @PathVariable("music") String music){
        return musicDao.selectBySql(singer, music);
    }
}
