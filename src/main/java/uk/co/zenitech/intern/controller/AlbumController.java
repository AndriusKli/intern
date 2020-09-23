package uk.co.zenitech.intern.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.zenitech.intern.entity.Album;
import uk.co.zenitech.intern.service.AlbumService;

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

    // TODO
    // https://itunes.apple.com/search?term=life&entity=album&attribute=albumTerm
// {"wrapperType":"collection", "collectionType":"Album"

}
