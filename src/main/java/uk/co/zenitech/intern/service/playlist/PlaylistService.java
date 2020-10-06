package uk.co.zenitech.intern.service.playlist;

import uk.co.zenitech.intern.entity.Playlist;

import java.util.List;

public interface PlaylistService {
    List<Playlist> getPlaylists(Long userId);
    Playlist getPlaylist(Long userId, Long playlistId);
    Playlist createPlaylist(Long userId);
    void addSong(Long userId, Long playlistId, Long songId);
    void removeSong(Long userId, Long playlistId, Long songId);
}
