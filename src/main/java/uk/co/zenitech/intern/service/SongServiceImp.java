package uk.co.zenitech.intern.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.co.zenitech.intern.client.ITunedFeignClient;
import uk.co.zenitech.intern.client.musicparams.Attribute;
import uk.co.zenitech.intern.client.musicparams.Entity;
import uk.co.zenitech.intern.entity.Song;
import uk.co.zenitech.intern.repository.SongRepository;
import uk.co.zenitech.intern.response.ITunesResponse;
import uk.co.zenitech.intern.serializer.ResponseParser;
import uk.co.zenitech.intern.serializer.WrapperType;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SongServiceImp implements SongService {

    private final ResponseParser responseParser;
    private final SongRepository songRepository;
    private final ITunedFeignClient iTunedFeignClient;
    private static final Logger logger = LoggerFactory.getLogger(SongService.class);


    @Autowired
    public SongServiceImp(ResponseParser responseParser, SongRepository songRepository, ITunedFeignClient iTunedFeignClient) {
        this.responseParser = responseParser;
        this.songRepository = songRepository;
        this.iTunedFeignClient = iTunedFeignClient;
    }

    @Override
    @Transactional
    public List<Song> getSongs(String songName, Long limit) {
        logger.info("Retrieving songs containing \"{}\" in their name", songName);
        List<Song> songs = responseParser.parse(
                Song.class,
                WrapperType.TRACK.getWrapper(),
                fetchSongData(songName, limit)
        );
        songRepository.saveAll(songs);
        return songs;
    }

    private ResponseEntity<ITunesResponse> fetchSongData(String songName, Long limit) {
        return iTunedFeignClient.getResults(
                songName,
                Entity.MUSIC_TRACK.getValue(),
                Attribute.SONG_TERM.getValue(),
                limit);
    }

    @Override
    @Transactional
    public Song getSong(Long id) {
        return songRepository.findById(id).orElse(fetchSong(id));
    }

    private Song fetchSong(Long id) {
        List<Song> songs = responseParser.parse(
                Song.class,
                WrapperType.TRACK.getWrapper(),
                iTunedFeignClient.getById(id)
        );
        if (songs.isEmpty()) {
            logger.warn("No songs found with the requested id: {}", id);
            throw new NoSuchElementException("No songs with the requested id found");
        } else {
            Song song = songs.get(0);
            songRepository.save(song);
            return song;
        }
    }
}
