package reptiles.service;

import java.io.IOException;

/**
 * @Auther: 大叔
 * @Time: 2019/3/14 15:55
 */
public interface MusicAcquireService {


    Object searchMusic(String searchMusicName, String searchSingerName) throws IOException;

    Object getMusicById(String musicId) throws IOException;
}
