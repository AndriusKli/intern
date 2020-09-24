package uk.co.zenitech.intern.controller;

import io.swagger.annotations.Api;
import io.swagger.models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.zenitech.intern.entity.Playlist;
import uk.co.zenitech.intern.entity.Song;
import uk.co.zenitech.intern.service.playlist.PlaylistService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping(value = "api/users/{userId}/playlists")
@Api("api/users/{userId}/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    @Autowired
    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping
    ResponseEntity<List<Playlist>> getPlaylists(@PathVariable Long userId) {
        return ResponseEntity.ok(playlistService.getPlaylists(userId));
    }

    @GetMapping("/{playlistId}")
    ResponseEntity<Playlist> getPlaylist(@PathVariable Long userId,
                                         @PathVariable Long playlistId) {
        return ResponseEntity.ok(playlistService.getPlaylist(userId, playlistId));
    }

    @PostMapping
    ResponseEntity<Void> createPlaylist(@PathVariable Long userId) throws URISyntaxException {
        playlistService.createPlaylist(userId);
        return ResponseEntity.created(new URI(userId.toString())).build();
    }

    @PutMapping("/{playlistId}")
    ResponseEntity<Void> addSongToPlaylist(@PathVariable Long userId,
                                           @PathVariable Long playlistId,
                                           @RequestBody Song song) {
        return null;
    }
}