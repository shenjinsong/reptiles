package reptiles.service;

import java.io.IOException;

/**
 * 酷我音乐抓取
 *
 * @Auther: 大叔
 * @Time: 2019/3/14 15:55
 */
public interface KuwoMusicAcquireService {


    /**
     * 搜索音乐名称
     * @param searchMusicName
     * @param searchSingerName
     * @return
     */
    Object searchMusic(String searchMusicName, String searchSingerName);

    /**
     * 根据musicId获取可访问路径
     * @param musicId
     * @return
     */
    Object getMusicByMusicId(String musicId);

    /**
     * 根据排行榜名称，排行榜ID获取获取音乐列表
     * @param name
     * @param bangId
     */
    void List4Rank(String name,String bangId);

    /**
     * 根据PID获取精选歌单列表
     * @param pid
     * @return
     */
    Object getListByPid(String pid);
}
