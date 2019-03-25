package reptiles.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reptiles.service.MusicAcquireService;

import javax.annotation.Resource;

/**
 * @Auther: 大叔
 * @Time: 2019/3/15 11:21
 */
@RestController
public class MusicAcquireController {

    @Resource
    MusicAcquireService musicAcquireService;

    @GetMapping("/search")
    public Object search(String musicName, String singerName) {
        return musicAcquireService.searchMusic(musicName, singerName);
    }

    @GetMapping("/id")
    public Object musicById(String musicId) {
        return musicAcquireService.getMusicByMusicId(musicId);
    }

    @GetMapping("/rankList")
    public void reptiles(String name, String bangId) {
        musicAcquireService.List4Rank(name, bangId);
    }

    @GetMapping("/choiceness")
    public void choiceness(String pid){
        musicAcquireService.getListByPid(pid);
    }
}
