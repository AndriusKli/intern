package uk.co.zenitech.intern.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.co.zenitech.intern.entity.Song;
import uk.co.zenitech.intern.service.SongService;

import java.util.Arrays;
import java.util.List;

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
    @ResponseStatus(HttpStatus.OK)
    public List<Song> getAllSongs(@RequestParam(name = "song", required = false) String song) {
        return songService.getSongsByName(song);
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
