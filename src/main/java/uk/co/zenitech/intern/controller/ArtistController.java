package uk.co.zenitech.intern.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.zenitech.intern.entity.Artist;
import uk.co.zenitech.intern.service.ArtistService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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
    public ResponseEntity<List<Artist>> getArtistByName(@RequestParam String artist,
                                                        @RequestParam(required = false) Long limit) {
        return ResponseEntity.ok(artistService.getArtists(artist, limit));
    }

    @GetMapping(value = "/{artistId}")
    public ResponseEntity<Artist> getArtist(@PathVariable Long artistId) {
        return ResponseEntity.ok(artistService.getArtist(artistId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createArtist(@RequestBody Artist artist) throws URISyntaxException {
        artistService.createArtist(artist);
        Long id = artist.getArtistId();
        return ResponseEntity.created(new URI("/{id}")).build();
    }

    @PutMapping(value = "/{artistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> updateArtist(@RequestBody Artist artist, @PathVariable Long artistId) {
        artistService.updateArtist(artistId, artist);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping(value = "/{artistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> deleteArtist(@PathVariable Long artistId) {
        artistService.deleteArtist(artistId);
        return ResponseEntity.noContent().build();
    }
}
