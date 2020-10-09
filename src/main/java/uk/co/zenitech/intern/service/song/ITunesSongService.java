package uk.co.zenitech.intern.service.song;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.co.zenitech.intern.client.ITunesFeignClient;
import uk.co.zenitech.intern.client.musicparams.Attribute;
import uk.co.zenitech.intern.client.musicparams.Entity;
import uk.co.zenitech.intern.entity.Song;
import uk.co.zenitech.intern.errorhandling.exceptions.EntityNotInDbException;
import uk.co.zenitech.intern.response.ITunesResponse;
import uk.co.zenitech.intern.serializer.ResponseParser;
import uk.co.zenitech.intern.serializer.WrapperType;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ITunesSongService implements SongService {

    private final ResponseParser responseParser;
    private final SongRepository songRepository;
    private final ITunesFeignClient iTunesFeignClient;
    private static final Logger logger = LoggerFactory.getLogger(SongService.class);


    @Autowired
    public ITunesSongService(ResponseParser responseParser, SongRepository songRepository, ITunesFeignClient iTunesFeignClient) {
        this.responseParser = responseParser;
        this.songRepository = songRepository;
        this.iTunesFeignClient = iTunesFeignClient;
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
        songRepository.flush();
        return songs;
    }

    private ResponseEntity<ITunesResponse> fetchSongData(String songName, Long limit) {
        return iTunesFeignClient.getResults(
                songName,
                Entity.MUSIC_TRACK.getValue(),
                Attribute.SONG_TERM.getValue(),
                limit);
    }

    @Override
    @Transactional
    public Song getSong(Long id) {
        return songRepository.findById(id).orElseGet(() -> fetchSong(id));
    }

    private Song fetchSong(Long id) {
        List<Song> songs = responseParser.parse(
                Song.class,
                WrapperType.TRACK.getWrapper(),
                iTunesFeignClient.getById(id)
        );
        if (songs.isEmpty()) {
            logger.warn("No songs found with the requested id: {}", id);
            throw new EntityNotInDbException("song", id);
        } else {
            Song song = songs.get(0);
            songRepository.saveAndFlush(song);
            return song;
        }
    }

    @Override
    @Transactional
    public void createSong(Song song) {
        songRepository.saveAndFlush(song);
    }

    @Override
    @Transactional
    public void deleteSong(Long id) {
        songRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateSong(Long id, Song song) {
        Song updatableSong = songRepository.findById(id).orElseThrow(() -> new EntityNotInDbException("song", id));
        updatableSong.setAlbumName(song.getAlbumName());
        updatableSong.setArtistName(song.getArtistName());
        updatableSong.setSongId(song.getSongId());
        updatableSong.setSongName(song.getSongName());
        updatableSong.setAlbumCover(song.getAlbumCover());
        songRepository.save(updatableSong);
    }
}
