package uk.co.zenitech.intern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.zenitech.intern.entity.Artist;
import uk.co.zenitech.intern.entity.Song;
import uk.co.zenitech.intern.response.ITunesResponse;
import uk.co.zenitech.intern.serializer.ResponseParser;
import uk.co.zenitech.intern.serializer.WrapperType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseParserTest {
    ResponseParser responseParser = new ResponseParser();
    ObjectMapper mapper = new ObjectMapper();


    ITunesResponse artistResponse = new ITunesResponse(1L, Collections.singletonList(
            mapper.readTree("{\"wrapperType\":\"artist\", \"artistType\":\"Artist\", \"artistName\":\"Manson\", \"artistLinkUrl\":\"https://music.apple.com/us/artist/manson/1401282208?uo=4\", \"artistId\":1401282208, \"primaryGenreName\":\"Hip-Hop/Rap\", \"primaryGenreId\":18}")
    ));
    ResponseEntity<ITunesResponse> artistResponseEntity = new ResponseEntity<>(artistResponse, HttpStatus.OK);

    ITunesResponse songResponse = new ITunesResponse(1L, Collections.singletonList(
            mapper.readTree("{\"wrapperType\":\"track\", \"kind\":\"song\", \"artistId\":2663644, \"collectionId\":118125504, \"trackId\":118125081, \"amgArtistId\":5, \"artistName\":\"Pretenders\", \"trackName\":\"I'll Stand By You\", \"collectionName\":\"Last of the Independents\"}")
    ));
    ResponseEntity<ITunesResponse> songResponseEntity = new ResponseEntity<>(songResponse, HttpStatus.OK);

    public ResponseParserTest() throws JsonProcessingException {
    }

    @Test
    void parsesArtistResponseCorrectly() {
        List<Artist> artists = responseParser.parse(Artist.class, WrapperType.ARTIST.getWrapper(), artistResponseEntity);
        assertEquals(1, artists.size());
        Artist expectedArtist = new Artist(1401282208L, null, "Manson");
        assertEquals(expectedArtist, artists.get(0));
    }

    @Test
    void parsesSongResponseCorrectly() {
        List<Song> songs = responseParser.parse(Song.class, WrapperType.TRACK.getWrapper(), songResponseEntity);
        assertEquals(1, songs.size());
        Song expectedSong = new Song(118125081L, "I'll Stand By You", "Last of the Independents","Pretenders" );
        assertEquals(expectedSong, songs.get(0));
    }

}
