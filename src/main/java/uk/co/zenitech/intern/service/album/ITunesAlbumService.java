package uk.co.zenitech.intern.service.album;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.co.zenitech.intern.client.ITunesFeignClient;
import uk.co.zenitech.intern.client.musicparams.Attribute;
import uk.co.zenitech.intern.client.musicparams.Entity;
import uk.co.zenitech.intern.entity.Album;
import uk.co.zenitech.intern.entity.Song;
import uk.co.zenitech.intern.response.ITunesResponse;
import uk.co.zenitech.intern.serializer.ResponseParser;
import uk.co.zenitech.intern.serializer.WrapperType;
import uk.co.zenitech.intern.service.song.SongRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ITunesAlbumService implements AlbumService {

    private final ResponseParser responseParser;
    private final AlbumRepository albumRepository;
    private final ITunesFeignClient iTunesFeignClient;
    private final SongRepository songRepository;
    private static final Logger logger = LoggerFactory.getLogger(ITunesAlbumService.class);

    @Autowired
    public ITunesAlbumService(ResponseParser responseParser, AlbumRepository albumRepository, ITunesFeignClient iTunesFeignClient, SongRepository songRepository) {
        this.responseParser = responseParser;
        this.albumRepository = albumRepository;
        this.iTunesFeignClient = iTunesFeignClient;
        this.songRepository = songRepository;
    }


    @Override
    @Transactional
    public List<Album> getAlbums(String album, Long limit) {
        List<Album> albums = responseParser.parse(
                Album.class,
                WrapperType.COLLECTION.getWrapper(),
                fetchAlbumData(album, limit)
        );
        albumRepository.saveAll(albums);
        return albums;
    }

    private ResponseEntity<ITunesResponse> fetchAlbumData(String album, Long limit) {
        return iTunesFeignClient.getResults(
                album,
                Entity.ALBUM.getValue(),
                Attribute.ALBUM_TERM.getValue(),
                limit);
    }

    @Override
    @Transactional
    public Album getAlbum(Long id) {
        logger.info("Attempting to find an album with id {} in the database", id);
        Album album = albumRepository.findById(id).orElseGet(() -> fetchAlbum(id));
        if (album.getSongs() == null || album.getSongs().isEmpty()) {
            logger.info("Album ID {} does not have any songs assigned to it, fetching songs from ITunes", id);
            fetchAlbumSongs(id);
        }
        return album;
    }

    private Album fetchAlbum(Long id) {
        logger.info("Album not found, fetching album with id {} from ITunes", id);
        List<Album> albums = responseParser.parse(
                Album.class,
                WrapperType.COLLECTION.getWrapper(),
                iTunesFeignClient.getById(id)
        );
        if (albums.isEmpty()) {
            logger.warn("No album with the requested id found in ITunes: {}", id);
            throw new NoSuchElementException("No album with the requested id found");
        } else {
            Album album = albums.get(0);
            albumRepository.save(album);
            return album;
        }
    }

    private void fetchAlbumSongs(Long albumId) {
        List<Song> songs = responseParser.parse(
                Song.class,
                WrapperType.TRACK.getWrapper(),
                iTunesFeignClient.getAlbumSongs(albumId)
        );
        Album album = albumRepository.findById(albumId).orElseThrow(NoSuchElementException::new);
        songs.forEach(song -> song.setAlbum(album));
        album.setSongs(songs);
        albumRepository.save(album);
    }
}