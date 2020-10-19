package uk.co.zenitech.intern.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.zenitech.intern.documentation.PlaylistApi;
import uk.co.zenitech.intern.entity.Playlist;
import uk.co.zenitech.intern.service.playlist.PlaylistService;

import java.util.List;

@RestController
@RequestMapping(value = "api/users/{accessToken}/playlists")
@Api("api/users/{accessToken}/playlists")
public class PlaylistController implements PlaylistApi {

    private final PlaylistService playlistService;

    @Autowired
    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping
    public ResponseEntity<List<Playlist>> getPlaylists(@PathVariable String accessToken) {
        return ResponseEntity.ok(playlistService.getPlaylists(accessToken));
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<Playlist> getPlaylist(@PathVariable String accessToken,
                                                @PathVariable Long playlistId) {
        return ResponseEntity.ok(playlistService.getPlaylist(accessToken, playlistId));
    }

    @PostMapping
    public ResponseEntity<Playlist> createPlaylist(@PathVariable String accessToken,
                                                   @RequestBody Playlist playlist) {
        return ResponseEntity.ok(playlistService.createPlaylist(accessToken, playlist));
    }

    @PutMapping
    public ResponseEntity<Playlist> updatePlaylist(@PathVariable String accessToken,
                                                   @RequestBody Playlist playlist) {
        return ResponseEntity.ok(playlistService.updatePlaylist(accessToken, playlist));
    }

    // TODO Maybe pass the song via body instead of params.
    @PostMapping("/{playlistId}")
    public ResponseEntity<Void> addSongToPlaylist(@PathVariable String accessToken,
                                                  @PathVariable Long playlistId,
                                                  @RequestParam Long songId) {
        playlistService.addSong(accessToken, playlistId, songId);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{playlistId}")
    public ResponseEntity<Void> removeSongFromPlaylist(@PathVariable String accessToken,
                                                       @PathVariable Long playlistId,
                                                       @RequestParam Long songId) {
        playlistService.removeSong(accessToken, playlistId, songId);
        return ResponseEntity.noContent().build();
    }
}
