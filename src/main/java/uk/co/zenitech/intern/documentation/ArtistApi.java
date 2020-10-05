package uk.co.zenitech.intern.documentation;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import uk.co.zenitech.intern.entity.Artist;

import java.util.List;

public interface ArtistApi {

    @ApiOperation(value = "Returns a list of up to 200 artists whose names contain the provided parameter", notes="Note that" +
            "the endpoint will also return artists whose REAL names include the provided parameter." +
            "For example, if you search for \"Michael\", the response will also contain Lil Wayne " +
            "(due to his full name being Dwayne Michael Carter Jr.)")
    ResponseEntity<List<Artist>> getArtistsByName(@ApiParam(value = "Term to search artists by.", example = "michael") String artist,
                                                  @ApiParam(value = "Amount of results to return. Min 1, max 200 (default 200).", example = "15") Long limit);
}
