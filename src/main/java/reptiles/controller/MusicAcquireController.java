package reptiles.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reptiles.service.KuwoMusicAcquireService;

import javax.annotation.Resource;

/**
 * @Auther: 大叔
 * @Time: 2019/3/15 11:21
 */
@RestController
public class MusicAcquireController {

    @Resource
    KuwoMusicAcquireService musicAcquireService;

    @GetMapping("/music")
    public Object searchMusic(String musicName, String singerName) {
        return musicAcquireService.searchMusic(musicName, singerName);
    }

    @GetMapping("/id")
    public Object musicById(String musicId) {
        return musicAcquireService.getMusicByMusicId(musicId);
    }

    @GetMapping("/rankList")
    public void List4Rank(String name, String bangId) {
        musicAcquireService.List4Rank(name, bangId);
    }

    @GetMapping("/selection")
    public void selection(String pid){
        musicAcquireService.getListByPid(pid);
    }

    @GetMapping("/singer/{name}")
    public void searchSinger(@PathVariable("name") String name){
        musicAcquireService.searchSinger(name);
    }
}
