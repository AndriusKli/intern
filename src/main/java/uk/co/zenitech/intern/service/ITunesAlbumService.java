package uk.co.zenitech.intern.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.co.zenitech.intern.client.ITunesFeignClient;
import uk.co.zenitech.intern.client.musicparams.Attribute;
import uk.co.zenitech.intern.client.musicparams.Entity;
import uk.co.zenitech.intern.entity.Album;
import uk.co.zenitech.intern.entity.Artist;
import uk.co.zenitech.intern.repository.AlbumRepository;
import uk.co.zenitech.intern.response.ITunesResponse;
import uk.co.zenitech.intern.serializer.ResponseParser;
import uk.co.zenitech.intern.serializer.WrapperType;

import java.util.List;

@Service
public class ITunesAlbumService implements AlbumService {

    private final ResponseParser responseParser;
    private final AlbumRepository albumRepository;
    private final ITunesFeignClient iTunesFeignClient;
    private static final Logger logger = LoggerFactory.getLogger(ITunesAlbumService.class);

    public ITunesAlbumService(ResponseParser responseParser, AlbumRepository albumRepository, ITunesFeignClient iTunesFeignClient) {
        this.responseParser = responseParser;
        this.albumRepository = albumRepository;
        this.iTunesFeignClient = iTunesFeignClient;
    }


    @Override
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
    public Album getAlbum(Long id) {
        return null;
    }
}
