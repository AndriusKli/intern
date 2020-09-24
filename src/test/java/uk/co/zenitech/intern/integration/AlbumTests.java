package uk.co.zenitech.intern.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import uk.co.zenitech.intern.entity.Album;
import uk.co.zenitech.intern.service.album.AlbumRepository;
import uk.co.zenitech.intern.response.ITunesResponse;
import uk.co.zenitech.intern.service.song.SongRepository;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AlbumTests {

    @MockBean
    ITunesFeignClient iTunesFeignClient;

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    SongRepository songRepository;

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    ObjectMapper mapper = new ObjectMapper();
    private final JsonNode responseNode = mapper.readTree(new File("src/test/resources/albumResponse.json"));
    private final Long resultCount = responseNode.get("resultCount").asLong();
    private final List<JsonNode> results = StreamSupport
            .stream(responseNode.get("results").spliterator(), false)
            .collect(Collectors.toList());
    private final ResponseEntity<ITunesResponse> albumResponseEntity = new ResponseEntity<>(new ITunesResponse(resultCount, results), HttpStatus.OK);
    private final ResponseEntity<ITunesResponse> emptyResponseEntity = new ResponseEntity<>(new ITunesResponse(0L, new ArrayList<>()), HttpStatus.OK);

    private final Long ALBUM_ID = 574049507L;
    private final Album ALBUM = new Album(574049507L, 5040714L, "The Razors Edge", "https://is4-ssl.mzstatic.com/image/thumb/Music/v4/81/17/f9/8117f9c5-ba41-97e8-1de5-cf75b0d4cc5f/source/100x100bb.jpg", 13L, "Hard Rock", null);

    public AlbumTests() throws IOException {
    }

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getsAlbums_andPersists() {
        when(iTunesFeignClient.getResults("The Razors Edge", Entity.ALBUM.getValue(), Attribute.ALBUM_TERM.getValue(), 1L))
                .thenReturn(albumResponseEntity);
        Album[] albums = RestAssured.given()
                .param("searchTerm", "The Razors Edge")
                .param("limit", 1)
                .when()
                .get("/api/albums")
                .then()
                .assertThat().statusCode(200)
                .extract().as(Album[].class);

        assertThat(albums[0]).isEqualTo(ALBUM);
        assertThat(albumRepository.findById(ALBUM_ID).get().getAlbumId()).isEqualTo(ALBUM_ID);
    }

    @Test
    @Transactional
    void whenAlbumIsNotInDb_thenRetrievesAlbumAndSongs_andThenPersists() {
        when(iTunesFeignClient.getById(ALBUM_ID)).thenReturn(albumResponseEntity);
        when(iTunesFeignClient.getAlbumSongs(ALBUM_ID)).thenReturn(albumResponseEntity);
        RestAssured.when()
                .get("api/albums/{id}", ALBUM_ID)
                .then()
                .assertThat().statusCode(200);

        assertThat(albumRepository.findAll().size()).isEqualTo(1L);
        assertThat(songRepository.findAll().size()).isEqualTo(12L);

        assertThat(albumRepository.findById(ALBUM_ID).get().getSongs().size()).isEqualTo(12L);
        assertThat(songRepository.findAll().get(0).getAlbum()).isEqualTo(albumRepository.findById(ALBUM_ID).get());
    }

    @Test
    void whenAlbumInDbButHasNoSongs_thenRetrievesSongs_andPersists() {
        albumRepository.save(ALBUM);
        when(iTunesFeignClient.getAlbumSongs(ALBUM_ID)).thenReturn(albumResponseEntity);
        RestAssured.when()
                .get("api/albums/{id}", ALBUM_ID)
                .then()
                .assertThat().statusCode(200);

        verify(iTunesFeignClient, never()).getById(anyLong());
        assertThat(albumRepository.findAll().size()).isEqualTo(1L);
        assertThat(songRepository.findAll().size()).isEqualTo(12L);

        RestAssured.when()
                .get("api/albums/{id}", ALBUM_ID)
                .then()
                .assertThat().statusCode(200);
    }

    @Test
    void whenAlbumWithSongsInDb_thenRetrieveFromDb() {
        when(iTunesFeignClient.getById(ALBUM_ID)).thenReturn(albumResponseEntity);
        when(iTunesFeignClient.getAlbumSongs(ALBUM_ID)).thenReturn(albumResponseEntity);

        RestAssured.when()
                .get("api/albums/{id}", ALBUM_ID)
                .then()
                .assertThat().statusCode(200);

        RestAssured.when()
                .get("api/albums/{id}", ALBUM_ID)
                .then()
                .assertThat().statusCode(200);

        verify(iTunesFeignClient, times(1)).getById(ALBUM_ID);
        verify(iTunesFeignClient, times(1)).getAlbumSongs(ALBUM_ID);
    }
}
