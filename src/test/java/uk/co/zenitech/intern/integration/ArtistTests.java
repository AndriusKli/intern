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
import uk.co.zenitech.intern.response.ITunesResponse;
import uk.co.zenitech.intern.service.artist.ArtistRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ArtistTests {

    @MockBean
    ITunesFeignClient iTunesFeignClient;

    @Autowired
    ArtistRepository artistRepository;

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    private final ObjectMapper mapper = new ObjectMapper();
    private final JsonNode responseNode = mapper.readTree(new File("src/test/resources/artistResponse.json"));
    private final Long resultCount = responseNode.get("resultCount").asLong();
    private final List<JsonNode> results = StreamSupport
            .stream(responseNode.get("results").spliterator(), false)
            .collect(Collectors.toList());
    private final ITunesResponse artistResponseEntity = new ITunesResponse(resultCount, results);
    private final ITunesResponse emptyResponseEntity = new ITunesResponse(0L, new ArrayList<>());

    private final Artist ARTIST_SANTANA = new Artist(217174L, 13645L, "Santana");
    private final Artist ARTIST_SANTA = new Artist(5L, 1L, "Santa");
    private final Long SANTANA_ID = 217174L;

    public ArtistTests() throws IOException {
    }

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void whenGettingMultipleArtists_thenReturnsResultsAndPersists() {
        when(iTunesFeignClient.getResults("san", Entity.MUSIC_ARTIST.getValue(), Attribute.ARTIST_TERM.getValue(), 2L))
                .thenReturn(artistResponseEntity);
        Artist[] artists = RestAssured
                .given()
                .param("searchTerm", "san")
                .param("limit", 2L)
                .when()
                .get("/api/artists/")
                .then()
                .assertThat().statusCode(200)
                .extract().as(Artist[].class);

        assertThat(artists.length).isEqualTo(2);
        assertThat(artists[0]).isEqualTo(ARTIST_SANTANA);

        assertThat(artistRepository.findAll().size()).isEqualTo(2);
        assertThat(artistRepository.findAll()).contains(ARTIST_SANTA, ARTIST_SANTANA);
    }

    @Test
    void whenFetchingArtist_thenReturnsResultAndPersists() {
        when(iTunesFeignClient.getById(SANTANA_ID)).thenReturn(artistResponseEntity);
        Artist artists = RestAssured.when()
                .get("/api/artists/{id}", SANTANA_ID)
                .then()
                .assertThat().statusCode(200)
                .extract().as(Artist.class);

        assertThat(artists).isEqualTo(ARTIST_SANTANA);
        assertThat(artistRepository.findById(SANTANA_ID))
                .contains(ARTIST_SANTANA);
    }

    @Test
    void givenArtistExistsInDb_whenFetchingArtist_thenReturnsArtistFromDb() {
        artistRepository.save(ARTIST_SANTANA);
        Artist artist = RestAssured.when()
                .get("/api/artists/{id}", SANTANA_ID)
                .then()
                .assertThat().statusCode(200)
                .extract().as(Artist.class);

        verify(iTunesFeignClient, times(0)).getById(anyLong());
        assertThat(artist).isEqualTo(ARTIST_SANTANA);
    }

    @Test
    void whenNoArtistFound_thenReturn404() {
        when(iTunesFeignClient.getById(anyLong())).thenReturn(emptyResponseEntity);
        RestAssured.when()
                .get("/api/artists/43434")
                .then()
                .assertThat().statusCode(404);
    }

    @Test
    void createsArtist() {
        RestAssured.given()
                .body(ARTIST_SANTANA)
                .when()
                .post("/api/artists/")
                .then()
                .assertThat().statusCode(201);

        assertThat(artistRepository.findById(SANTANA_ID)).isEqualTo(Optional.of(ARTIST_SANTANA));
    }

    @Test
    void updatesArtist() {
        artistRepository.save(ARTIST_SANTANA);
        Artist updatedArtist = new Artist(217174L,13645L, "New name");
        RestAssured.given()
                .body(updatedArtist)
                .when()
                .put("/api/artists/{id}", SANTANA_ID)
                .then()
                .assertThat().statusCode(202);

        assertThat(artistRepository.findById(SANTANA_ID).get().getArtistName()).isEqualTo("New name");
    }

    @Test
    void removesArtist() {
        artistRepository.save(ARTIST_SANTANA);
        RestAssured.when()
                .delete("/api/artists/{id}", SANTANA_ID)
                .then()
                .assertThat().statusCode(204);

        assertThat(artistRepository.findById(SANTANA_ID)).isEqualTo(Optional.empty());
    }
}
