package uk.co.zenitech.intern.documentation;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import uk.co.zenitech.intern.entity.Song;

import java.util.List;

public interface SongApi {

    @ApiOperation(value = "Returns a list of up to 200 songs whose names or album names contain the provided parameter",
            notes = "Case insensitive. Note that results will also contain songs which are in albums that match the search" +
                    "parameter.")
    ResponseEntity<List<Song>> getSongsByName(@ApiParam(value = "Term to search songs by", example = "thunderstruck") String searchTerm,
                                                     @ApiParam(value = "Amount of results to return. Min 1, max 200 (default 200).", example = "15") Long limit);
}
