package uk.co.zenitech.intern.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.co.zenitech.intern.entity.Artist;
import uk.co.zenitech.intern.service.ArtistService;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "api/artist")
@Api("api/artist")
public class ArtistController {

    private ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Artist> getAllArtist(@RequestParam (name = "artist", required = false) String artist) {
        return artistService.getArtistByName(artist);
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
