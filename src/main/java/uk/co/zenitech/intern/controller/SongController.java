package uk.co.zenitech.intern.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.zenitech.intern.entity.Song;
import uk.co.zenitech.intern.service.SongService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping(value = "api/songs")
@Api("api/songs")
public class SongController {

    private final SongService songService;

    @Autowired
    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping
    public ResponseEntity<List<Song>> getSongsByName(@RequestParam String searchTerm,
                                                     @RequestParam(required = false) Long limit) {
        return ResponseEntity.ok(songService.getSongs(searchTerm, limit));
    }

    @GetMapping(value = "/{songId}")
    public ResponseEntity<Song> getSong(@PathVariable Long songId) {
        return ResponseEntity.ok(songService.getSong(songId));

    }

    @PostMapping
    public ResponseEntity<Object> createSong(@RequestBody Song song) throws URISyntaxException {
        songService.createSong(song);
        Long id = song.getSongId();
        return ResponseEntity.created(new URI(id.toString())).build();
    }

    @PutMapping(value = "/{songId}")
    public ResponseEntity<Object> updateSong(@RequestBody Song song, @PathVariable Long songId) {
        songService.updateSong(songId, song);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping(value = "/{songId}")
    public ResponseEntity<Object> deleteSong(@PathVariable Long songId) {
        songService.deleteSong(songId);
        return ResponseEntity.noContent().build();
    }
}
