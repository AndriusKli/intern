package uk.co.zenitech.intern.documentation;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import uk.co.zenitech.intern.entity.Playlist;

import java.net.URISyntaxException;
import java.util.List;

public interface PlaylistApi {

    @ApiOperation(value = "Returns a list of playlists belonging to the specified user.")
    ResponseEntity<List<Playlist>> getPlaylists(@ApiParam(value = "User whose playlists you want to retrieve") Long userId);

    @ApiOperation(value = "Get a specific playlist", notes = "Gets a specific playlist belonging to a user.")
    ResponseEntity<Playlist> getPlaylist(@ApiParam(value = "User whose playlist you want to retrieve") Long userId,
                                         @ApiParam(value = "Playlist which you want to retrieve") Long playlistId);

    @ApiOperation(value = "Create a playlist", notes = "Creates a playlist with the given name for the specified user")
    ResponseEntity<Playlist> createPlaylist(@ApiParam(value = "User for whom you want to create a new playlist") Long userId,
                                            @ApiParam(value = "The name you want to give to the playlist." +
                                                    " Note that all other additional properties other than \"playlistName\" will be ignored.") Playlist playlist) throws URISyntaxException;

    @ApiOperation(value = "Update a playlist's name", notes = "Updates a specified playlist's name")
    ResponseEntity<Playlist> updatePlaylist(@ApiParam(value = "User for whom you want to update the playlist") Long userId,
                                            @ApiParam(value = "The playlist you are updating and the new name you want to give to the playlist." +
                                                    " Note that all other additional properties other than \"playlistId\" and \"playlistName\" will be ignored." +
                                                    "Will throw a 404 code and return and error message if specified playlist does not exist.") Playlist playlist);

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
