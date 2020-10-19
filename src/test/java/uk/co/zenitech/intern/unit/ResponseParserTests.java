package uk.co.zenitech.intern.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.zenitech.intern.entity.Artist;
import uk.co.zenitech.intern.entity.Song;
import uk.co.zenitech.intern.response.ITunesResponse;
import uk.co.zenitech.intern.serializer.ResponseParser;
import uk.co.zenitech.intern.serializer.WrapperType;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseParserTests {
    ResponseParser responseParser = new ResponseParser();
    ObjectMapper mapper = new ObjectMapper();

    ITunesResponse artistResponse = new ITunesResponse(1L, Collections.singletonList(
            mapper.readTree("{\"wrapperType\":\"artist\", \"artistType\":\"Artist\", \"artistName\":\"Manson\", \"artistLinkUrl\":\"https://music.apple.com/us/artist/manson/1401282208?uo=4\", \"artistId\":1401282208, \"primaryGenreName\":\"Hip-Hop/Rap\", \"primaryGenreId\":18}")
    ));

    ITunesResponse songResponse = new ITunesResponse(1L, Collections.singletonList(
            mapper.readTree("{\"wrapperType\":\"track\", \"kind\":\"song\", \"artistId\":2663644, \"collectionId\":118125504, \"trackId\":118125081, \"amgArtistId\":5, \"artistName\":\"Pretenders\", \"trackName\":\"I'll Stand By You\", \"collectionName\":\"Last of the Independents\"}")
    ));

    public ResponseParserTests() throws JsonProcessingException {
    }

    @Test
    void parsesArtistResponseCorrectly() {
        List<Artist> artists = responseParser.parse(Artist.class, WrapperType.ARTIST.getWrapper(), artistResponse);
        assertEquals(1, artists.size());
        Artist expectedArtist = new Artist(1401282208L, null, "Manson");
        assertEquals(expectedArtist, artists.get(0));
    }

    @Test
    void parsesSongResponseCorrectly() {
        List<Song> songs = responseParser.parse(Song.class, WrapperType.TRACK.getWrapper(), songResponse);
        assertEquals(1, songs.size());
        Song expectedSong = new Song(118125081L, "I'll Stand By You", "Last of the Independents", "Pretenders");
        assertEquals(expectedSong, songs.get(0));
    }
}
