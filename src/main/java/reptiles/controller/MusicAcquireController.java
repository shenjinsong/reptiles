package reptiles.controller;

import org.springframework.web.bind.annotation.*;
import reptiles.paramcheck.annotation.ParamCheck;
import reptiles.pojo.MusicEntity;
import reptiles.service.KuwoMusicAcquireService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Auther: 大叔
 * @Time: 2019/3/15 11:21
 */
@RestController
public class MusicAcquireController {

    @Resource
    KuwoMusicAcquireService musicAcquireService;

    @ParamCheck("test")
    @PostMapping("/music")
    public Object searchMusic(@RequestBody MusicEntity musicEntity) {
        return musicAcquireService.searchMusic(musicEntity.getSinger(), musicEntity.getMusic());
    }

    @GetMapping("/id")
    public Object musicById(String musicId) {
        return musicAcquireService.getMusicByMusicId(musicId);
    }

    @ParamCheck({"name | bangId | test"})
    @GetMapping("/rankList")
    public void List4Rank(List<String> name) {
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
