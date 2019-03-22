package reptiles.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reptiles.service.MusicAcquireService;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Auther: 大叔
 * @Time: 2019/3/15 11:21
 */
@RestController
public class MusicAcquireController {

    @Resource
    MusicAcquireService musicAcquireService;

    @GetMapping("/search")
    public Object search(String musicName, String singerName) throws IOException {
        return musicAcquireService.searchMusic(musicName, singerName);
    }

    @GetMapping("/id")
    public Object musicById(String musicId) throws IOException {
        return musicAcquireService.getMusicById(musicId);
    }
}
