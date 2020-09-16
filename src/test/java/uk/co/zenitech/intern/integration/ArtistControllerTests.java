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
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.co.zenitech.intern.client.ITunesFeignClient;
import uk.co.zenitech.intern.client.musicparams.Attribute;
import uk.co.zenitech.intern.client.musicparams.Entity;
import uk.co.zenitech.intern.controller.ArtistController;
import uk.co.zenitech.intern.entity.Artist;
import uk.co.zenitech.intern.repository.ArtistRepository;
import uk.co.zenitech.intern.response.ITunesResponse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ArtistControllerTests {

    @MockBean
    ITunesFeignClient iTunesFeignClient;

    @Autowired
    ArtistController artistController;

    @Autowired
    ArtistRepository artistRepository;

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Autowired
    TestRestTemplate testRestTemplate;

    private final ObjectMapper mapper = new ObjectMapper();
    private final JsonNode responseNode = mapper.readTree(new File("src/test/resources/artistResponse.json"));
    private final Long resultCount = responseNode.get("resultCount").asLong();
    private final List<JsonNode> results = StreamSupport
            .stream(responseNode.get("results").spliterator(), false)
            .collect(Collectors.toList());
    private final ResponseEntity<ITunesResponse> artistResponseEntity = new ResponseEntity<>(new ITunesResponse(resultCount, results), HttpStatus.OK);
    private final ResponseEntity<ITunesResponse> emptyResponseEntity = new ResponseEntity<>(new ITunesResponse(0L, new ArrayList<>()), HttpStatus.OK);

    private final Artist ARTIST_SANTANA = new Artist(217174L, 13645L, "Santana");
    private final Artist ARTIST_SANTA = new Artist(5L, 1L, "Santa");
    private final Long SANTANA_ID = 217174L;

    public ArtistControllerTests() throws IOException {
    }

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void whenGettingMultipleArtists_thenReturnsResultsAndPersists() {
        when(iTunesFeignClient.getResults("san", Entity.MUSIC_ARTIST.getValue(), Attribute.ARTIST_TERM.getValue(), 2L))
                .thenReturn(artistResponseEntity);
        ResponseEntity<List<Artist>> responseEntity = artistController.getArtistByName("san", 2L);

        assertThat(responseEntity.getBody().size()).isEqualTo(2);
        assertThat(responseEntity.getBody().get(0).getArtistName()).isEqualTo("Santana");

        assertThat(artistRepository.findAll().size()).isEqualTo(2);
        assertThat(artistRepository.findAll()).contains(ARTIST_SANTA, ARTIST_SANTANA);
    }

    @Test
    void whenFetchingArtist_thenReturnsResultAndPersists() {
        when(iTunesFeignClient.getById(SANTANA_ID)).thenReturn(artistResponseEntity);
        ResponseEntity<Artist> artistResponse = artistController.getArtist(SANTANA_ID);

        assertThat(artistResponse.getBody().getArtistId())
                .isEqualTo(SANTANA_ID);

        assertThat(artistRepository.findById(SANTANA_ID))
                .contains(ARTIST_SANTANA);
    }

    @Test
    void givenArtistExistsInDb_whenFetchingArtist_thenReturnsArtistFromDb() {
        artistRepository.save(ARTIST_SANTANA);
        ResponseEntity<Artist> artistResponse = artistController.getArtist(SANTANA_ID);

        verify(iTunesFeignClient, times(0)).getById(anyLong());
        assertThat(artistResponse.getBody().getArtistId())
                .isEqualTo(SANTANA_ID);
    }

    @Test
    void whenNoArtistFound_thenThrowException() {
        when(iTunesFeignClient.getById(anyLong())).thenReturn(emptyResponseEntity);
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> artistController.getArtist(43434L));
    }
}
