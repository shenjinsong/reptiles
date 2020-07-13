package reptiles.controller;

import org.springframework.web.bind.annotation.*;
import reptiles.config.ParamCheck;
import reptiles.pojo.MusicEntity;
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

    @ParamCheck("singer - 1")
    @PostMapping("/music")
    public Object searchMusic(@RequestBody MusicEntity musicEntity) {
        return musicAcquireService.searchMusic(musicEntity.getSinger(), musicEntity.getMusic());
    }

    @GetMapping("/id")
    public Object musicById(String musicId) {
        return musicAcquireService.getMusicByMusicId(musicId);
    }

    @ParamCheck({"name | bangId", "name - 8"})
    @GetMapping("/rankList")
    public void List4Rank(String name, String bangId) {
        //musicAcquireService.List4Rank(name, bangId);
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
