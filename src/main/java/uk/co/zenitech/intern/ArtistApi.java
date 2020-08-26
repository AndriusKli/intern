package uk.co.zenitech.intern;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import uk.co.zenitech.intern.entity.Artist;

import java.util.List;

public interface ArtistApi {

    @ApiOperation(value = "blah", notes="blah")
    List<Artist> getAllArtist(@ApiParam(value = "blah", example = "john") String artist);

    // TODO
    // This is just a placeholder
}
