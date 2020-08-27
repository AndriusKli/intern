package uk.co.zenitech.intern.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.zenitech.intern.entity.Song;
import uk.co.zenitech.intern.service.SongService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "api/songs")
@Api("api/songs")
public class SongController {

    private SongService songService;

    @Autowired
    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping
    public ResponseEntity<List<Song>> getSongsByName(@RequestParam String searchTerm) {
        return ResponseEntity.ok(songService.getSongs(searchTerm));
    }

    @GetMapping(value = "/{songId}")
    public ResponseEntity<Song> getSong(@PathVariable Long songId ) {
        try {
            return ResponseEntity.ok(songService.getSong(songId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createSong(@RequestBody Song song) {

    }

    @PutMapping(value = "/{songId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSong(@RequestBody Song song, @PathVariable Long songId) {

    }

    @DeleteMapping(value = "/{songId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSong(@PathVariable Long songId) {

    }
}
