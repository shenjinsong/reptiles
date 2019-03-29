package reptiles.service.impl;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import reptiles.dao.MusicDao;
import reptiles.pojo.MusicEntity;

import javax.annotation.Resource;

/**
 * @Auther: 大叔
 * @Time: 2019/3/27 15:45
 */
@Service
public class MusicServiceImpl implements MusicService {

    @Resource
    MusicDao musicDao;

    @Override
    public Object findOneBySinger(String singer) {

        MusicEntity musicEntity = new MusicEntity();
        musicEntity.setSinger(singer);
        Example<MusicEntity> example = Example.of(musicEntity);
        PageRequest pageRequest = PageRequest.of(0, 1, Sort.Direction.DESC, "createTime");

        return musicDao.findAll(example, pageRequest);
    }

    @Override
    public Object findByMusic(String music) {
        return musicDao.queryByMusic(music);
    }

}
