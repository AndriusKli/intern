package uk.co.zenitech.intern.service.album;

import uk.co.zenitech.intern.entity.Album;
import uk.co.zenitech.intern.entity.Song;

import java.util.List;

public interface AlbumService {
    List<Album> getAlbums(String album, Long limit);
    Album getAlbum(Long id);
}
