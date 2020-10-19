package uk.co.zenitech.intern.service.playlist;

import uk.co.zenitech.intern.entity.Playlist;

import java.util.List;

public interface PlaylistService {
    List<Playlist> getPlaylists(String accessToken);
    Playlist getPlaylist(String accessToken, Long playlistId);
    Playlist createPlaylist(String accessToken, Playlist playlist);
    Playlist updatePlaylist(String accessToken, Playlist playlist);
    void addSong(String accessToken, Long playlistId, Long songId);
    void removeSong(String accessToken, Long playlistId, Long songId);
}
