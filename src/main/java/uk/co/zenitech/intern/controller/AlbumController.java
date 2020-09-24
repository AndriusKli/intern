package uk.co.zenitech.intern.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.zenitech.intern.entity.Album;
import uk.co.zenitech.intern.service.album.AlbumService;

import java.util.List;

@RestController
@RequestMapping(value = "api/albums")
@Api("api/albums")
public class AlbumController {

    private final AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public ResponseEntity<List<Album>> getAlbumsByName(@RequestParam String searchTerm,
                                                       @RequestParam(required = false) Long limit) {
        return ResponseEntity.ok(albumService.getAlbums(searchTerm, limit));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Album> getAlbumById(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.getAlbum(id));
    }

    // TODO
    // https://itunes.apple.com/search?term=life&entity=album&attribute=albumTerm
// {"wrapperType":"collection", "collectionType":"Album"

}
