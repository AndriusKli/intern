package uk.co.zenitech.intern.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.zenitech.intern.documentation.AlbumApi;
import uk.co.zenitech.intern.entity.Album;
import uk.co.zenitech.intern.service.album.AlbumService;

import java.util.List;

@RestController
@RequestMapping(value = "api/albums")
@Api("api/albums")
public class AlbumController implements AlbumApi {

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

    @GetMapping(value = "/{albumId}")
    public ResponseEntity<Album> getAlbumById(@PathVariable Long albumId) {
        return ResponseEntity.ok(albumService.getAlbum(albumId));
    }
}
