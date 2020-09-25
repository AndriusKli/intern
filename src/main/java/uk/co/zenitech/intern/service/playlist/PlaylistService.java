package uk.co.zenitech.intern.service.playlist;

import uk.co.zenitech.intern.entity.Playlist;
import uk.co.zenitech.intern.entity.Song;

import java.util.List;

public interface PlaylistService {
    List<Playlist> getPlaylists(Long userId);
    Playlist getPlaylist(Long userId, Long playlistId);
    void createPlaylist(Long userId);
    void addSong(Long userId, Long playlistId, Song song);
    void removeSong(Long userId, Long playlistId, Song song);
}
