package uk.co.zenitech.intern.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.zenitech.intern.documentation.ArtistApi;
import uk.co.zenitech.intern.entity.Artist;
import uk.co.zenitech.intern.service.artist.ArtistService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping(value = "api/artists")
@Api("api/artists")
public class ArtistController implements ArtistApi {

    private final ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    public ResponseEntity<List<Artist>> getArtistsByName(@RequestParam String searchTerm,
                                                         @RequestParam(required = false) Long limit) {
        return ResponseEntity.ok(artistService.getArtists(searchTerm, limit));
    }

    @GetMapping(value = "/{artistId}")
    public ResponseEntity<Artist> getArtist(@PathVariable Long artistId) {
        return ResponseEntity.ok(artistService.getArtist(artistId));
    }

    @PostMapping
    public ResponseEntity<Void> createArtist(@RequestBody Artist artist) throws URISyntaxException {
        artistService.createArtist(artist);
        Long id = artist.getArtistId();
        return ResponseEntity.created(new URI(id.toString())).build();
    }

    @PutMapping(value = "/{artistId}")
    public ResponseEntity<Void> updateArtist(@RequestBody Artist artist, @PathVariable Long artistId) {
        artistService.updateArtist(artistId, artist);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping(value = "/{artistId}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Long artistId) {
        artistService.deleteArtist(artistId);
        return ResponseEntity.noContent().build();
    }
}
