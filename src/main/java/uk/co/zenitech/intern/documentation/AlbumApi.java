package uk.co.zenitech.intern.documentation;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import uk.co.zenitech.intern.entity.Album;

import java.util.List;

public interface AlbumApi {

    @ApiOperation(value = "Retrieves a list of up to 200 albums whose names contain the provided parameter", notes = "Case insensitive")
    ResponseEntity<List<Album>> getAlbumsByName(@ApiParam(example = "thunder") String searchTerm,
                                                @ApiParam(value = "Min 1 , Max 200", example = "5") Long limit);

    @ApiOperation(value = "Retrieves the specified album by id, along with all songs belonging it", notes = "The response will" +
            "also contain an array of songs that belong to this album.")
    ResponseEntity<Album> getAlbumById(@ApiParam(value = "Id of the album you want to fetch") Long albumId);
}
