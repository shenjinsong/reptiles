package reptiles.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reptiles.config.ParamCheck;
import reptiles.dao.MusicDao;
import reptiles.service.MusicService;

import javax.annotation.Resource;

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

    @ParamCheck("music")
    @GetMapping("/music/find")
    public Object music(String music){
        return musicService.findByMusic(music);
    }

    @ParamCheck("str")
    @GetMapping("/find")
    public Object find(String str){
        return musicDao.getFirstBySinger(str);
    }

    @ParamCheck({"singer","music"})
    @GetMapping("/find/{singer}/{music}")
    public Object find(@PathVariable("singer") String singer, @PathVariable("music") String music){
        return musicDao.selectBySql(singer, music);
    }


}
