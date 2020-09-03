package uk.co.zenitech.intern.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.zenitech.intern.entity.Artist;
import uk.co.zenitech.intern.service.ArtistService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "api/artists")
@Api("api/artists")
public class ArtistController {

    private ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    public ResponseEntity<List<Artist>> getAllArtist(@RequestParam String artist,
                                                     @RequestParam(required = false) Long limit) {
        return ResponseEntity.ok(artistService.getArtists(artist, limit));
    }

    @GetMapping(value = "/{artistId}")
    public ResponseEntity<Artist> getArtist(@PathVariable Long artistId ) {
        try {
            return ResponseEntity.ok(artistService.getArtist(artistId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
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
