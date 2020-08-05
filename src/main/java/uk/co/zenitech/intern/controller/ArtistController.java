package uk.co.zenitech.intern.controller;

import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.co.zenitech.intern.entity.Artist;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "api/artist")
@Api("api/artist")
public class ArtistController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Artist> getAllArtist() {
        return Arrays.asList(new Artist(0L, 0L, "string"));
    }

    @GetMapping(value = "/{artistId}")
    @ResponseStatus(HttpStatus.OK)
    public Artist getArtist(@PathVariable Long artistId ) {
        return new Artist(0L, 0L, "string");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createArtist(@RequestBody Artist artist) {

    }

    @PutMapping(value = "/{artistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateArtist(@RequestBody Artist artist, @PathVariable Long artistId) {

    }

    @DeleteMapping(value = "/{artistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteArtist(@PathVariable Long artistId) {

    }
}
