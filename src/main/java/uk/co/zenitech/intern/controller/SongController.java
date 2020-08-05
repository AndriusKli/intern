package uk.co.zenitech.intern.controller;

import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.co.zenitech.intern.entity.Song;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "api/songs")
@Api("api/songs")
public class SongController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Song> getAllSongs() {
        return Arrays.asList(new Song(0L, "string", "string", "string"));
    }

    @GetMapping(value = "/{songId}")
    @ResponseStatus(HttpStatus.OK)
    public Song getSong(@PathVariable Long songId ) {
        return new Song(0L, "string", "string", "string");
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
