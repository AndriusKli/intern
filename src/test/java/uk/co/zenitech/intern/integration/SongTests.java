package uk.co.zenitech.intern.integration;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.co.zenitech.intern.client.ITunesFeignClient;
import uk.co.zenitech.intern.client.musicparams.Attribute;
import uk.co.zenitech.intern.client.musicparams.Entity;
import uk.co.zenitech.intern.entity.Artist;
import uk.co.zenitech.intern.entity.Song;
import uk.co.zenitech.intern.repository.SongRepository;
import uk.co.zenitech.intern.response.ITunesResponse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource("/test.properties")
public class SongTests {

    @MockBean
    ITunesFeignClient iTunesFeignClient;

    @Autowired
    SongRepository songRepository;

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    private final ObjectMapper mapper = new ObjectMapper();
    private final JsonNode responseNode = mapper.readTree(new File("src/test/resources/songResponse.json"));
    private final Long resultCount = responseNode.get("resultCount").asLong();
    // This seems way too complicated of a solution for something to simple. Can't I just get an array
    // of JsonNodes after getting the 'results' node, since the whole node is already an array of objects?
    private final List<JsonNode> results = StreamSupport
            .stream(responseNode.get("results").spliterator(), false)
            .collect(Collectors.toList());
    private final ResponseEntity<ITunesResponse> songResponseEntity = new ResponseEntity<>(new ITunesResponse(resultCount, results), HttpStatus.OK);
    private final ResponseEntity<ITunesResponse> emptyResponseEntity = new ResponseEntity<>(new ITunesResponse(0L, new ArrayList<>()), HttpStatus.OK);

    private final Song SONG_PRETENDERS = new Song(118125081L, "I'll Stand By You", "Last of the Independents", "Pretenders");
    private final Song SONG_FAKE = new Song(323123L, "Stand Fast", "Fake Album", "Fake Band");
    private final Long ID_PRETENDERS = 118125081L;

    public SongTests() throws IOException {
    }

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void whenGettingMultipleSongs_thenReturnsResultsAndPersists() {
        when(iTunesFeignClient.getResults("stand", Entity.MUSIC_TRACK.getValue(), Attribute.SONG_TERM.getValue(), 2L))
                .thenReturn(songResponseEntity);
        Song[] songs = RestAssured
                .given()
                .param("searchTerm", "stand")
                .param("limit", 2L)
                .when()
                .get("/api/songs/")
                .then()
                .assertThat().statusCode(200)
                .extract().as(Song[].class);

        assertThat(songs.length).isEqualTo(2);
        assertThat(songs[0]).isEqualTo(SONG_PRETENDERS);

        assertThat(songRepository.findAll().size()).isEqualTo(2);
        assertThat(songRepository.findAll()).contains(SONG_FAKE, SONG_PRETENDERS);
    }

    @Test
    void whenFetchingSong_thenReturnsResultAndPersists() {
        when(iTunesFeignClient.getById(anyLong())).thenReturn(songResponseEntity);
        Song song = RestAssured.when()
                .get("/api/songs/{id}", ID_PRETENDERS)
                .then()
                .assertThat()
                .statusCode(200)
                .extract().as(Song.class);

        assertThat(song).isEqualTo(SONG_PRETENDERS);
        assertThat(songRepository.findById(ID_PRETENDERS))
                .contains(SONG_PRETENDERS);
    }

    @Test
    void givenSongExistsInDb_whenFetchingSong_thenReturnsSongFromDb() {
        songRepository.save(SONG_PRETENDERS);
        Song song = RestAssured.when()
                .get("/api/songs/{id}", ID_PRETENDERS)
                .then()
                .assertThat()
                .statusCode(200)
                .extract().as(Song.class);

        verify(iTunesFeignClient, never()).getById(anyLong());
        assertThat(song.getSongId()).isEqualTo(ID_PRETENDERS);
    }

    @Test
    void createsSong() {
        RestAssured.given()
                .body(SONG_PRETENDERS)
                .when()
                .post("/api/songs/")
                .then()
                .assertThat().statusCode(201);

        assertThat(songRepository.findById(ID_PRETENDERS)).isEqualTo(Optional.of(SONG_PRETENDERS));
    }

    @Test
    void updatesSong() {
        songRepository.save(SONG_PRETENDERS);
        Song updatedSong = new Song(
                118125081L,
                "Renamed song",
                "Renamed album",
                "Renamed artist");

        RestAssured.given()
                .body(updatedSong)
                .when()
                .put("/api/songs/{id}", ID_PRETENDERS)
                .then()
                .assertThat().statusCode(202);

        assertThat(songRepository.findById(ID_PRETENDERS).get()).isEqualTo(updatedSong);
    }

    @Test
    void deleteArtist() {
        songRepository.save(SONG_PRETENDERS);
        RestAssured.when()
                .delete("/api/songs/{id}", ID_PRETENDERS)
                .then()
                .assertThat().statusCode(204);

        assertThat(songRepository.findById(ID_PRETENDERS)).isEqualTo(Optional.empty());
    }

    @Test
    void whenNoSongFound_thenThrowException() {
        when(iTunesFeignClient.getById(anyLong())).thenReturn(emptyResponseEntity);
        RestAssured.when()
                .get("/api/songs/43434")
                .then()
                .assertThat()
                .statusCode(404);
    }
}

