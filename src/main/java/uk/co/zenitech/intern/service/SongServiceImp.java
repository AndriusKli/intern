package uk.co.zenitech.intern.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.zenitech.intern.consumer.ITunesConsumer;
import uk.co.zenitech.intern.entity.Song;
import uk.co.zenitech.intern.repository.SongRepository;
import uk.co.zenitech.intern.serializer.ResponseParser;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class SongServiceImp implements SongService {

    private ResponseParser responseParser;
    private ITunesConsumer iTunesConsumer;
    private SongRepository songRepository;
    private Logger logger = LoggerFactory.getLogger(SongService.class);


    @Autowired
    public SongServiceImp(ResponseParser responseParser, ITunesConsumer iTunesConsumer, SongRepository songRepository) {
        this.responseParser = responseParser;
        this.iTunesConsumer = iTunesConsumer;
        this.songRepository = songRepository;
    }

    @Override
    @Transactional
    public List<Song> getSongs(String songName) {
        logger.info("Retrieving songs containing \"{}\" in their name", songName);
        List<Song> songs = responseParser.parse(
                Song.class,
                "\"track\"",
                iTunesConsumer.fetchData("musicTrack", songName, "songTerm"));
        songRepository.saveAll(songs);
        return songs;
    }

    @Override
    @Transactional
    public Song getSong(Long id) {
        Optional<Song> repoSong = songRepository.findById(id);
        if (repoSong.isEmpty()) {
            List<Song> songs = responseParser.parse(
                    Song.class,
                    "\"track\"",
                    iTunesConsumer.fetchById(id)
            );
            if (songs.isEmpty()) {
                logger.warn("No songs found with the requested id: {}", id);
                throw new NoSuchElementException("No songs with the requested id found");
            } else {
                Song song = songs.get(0);
                songRepository.save(song);
                return song;
            }
        } else {
            return repoSong.get();
        }
    }
}
