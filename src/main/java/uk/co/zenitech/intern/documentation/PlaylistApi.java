package uk.co.zenitech.intern.documentation;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import uk.co.zenitech.intern.entity.Playlist;

import java.util.List;

public interface PlaylistApi {

    @ApiOperation(value = "Returns a list of playlists belonging to the specified user.")
    ResponseEntity<List<Playlist>> getPlaylists(@ApiParam(value = "User whose playlists you want to retrieve") Long userId);

    @ApiOperation(value = "Get a specific playlist", notes = "Gets a specific playlist belonging to a user.")
    ResponseEntity<Playlist> getPlaylist(@ApiParam(value = "User whose playlist you want to retrieve") Long userId,
                                         @ApiParam(value = "Playlist which you want to retrieve") Long playlistId);
//
//    @ApiOperation(value = "Create a playlist", notes = "Creates a playlist for the specified user")
//    ResponseEntity<Void> createPlaylist(@ApiParam(value = "User for whom you want to create a new playlist") Long userId) throws URISyntaxException;

    @ApiOperation(value = "Adds a song to a playlist", notes = "The song being added must be in the database," +
            "otherwise it will fail. Will not add duplicates.")
    ResponseEntity<Void> addSongToPlaylist(@ApiParam(value = "The id of the user to whom the playlist belongs to") Long userId,
                                           @ApiParam(value = "The playlist the song is being added to") Long playlistId,
                                           @ApiParam(value = "The id of song song being added") Long songId);

    @ApiOperation(value = "Removes song from a playlist", notes = "Will still return a success response status if the" +
            "song being deleted does not exist in the playlist.")
    ResponseEntity<Void> removeSongFromPlaylist(@ApiParam(value = "The id of the user to whom the playlist belongs to") Long userId,
                                                @ApiParam(value = "The playlist the song is being removed from") Long playlistId,
                                                @ApiParam(value = "The id of song song being removed") Long songId);

}
