package uk.co.zenitech.intern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import uk.co.zenitech.intern.client.ITunesFeignClient;
import uk.co.zenitech.intern.controller.ArtistController;
import uk.co.zenitech.intern.entity.Artist;
import uk.co.zenitech.intern.repository.ArtistRepository;
import uk.co.zenitech.intern.response.ITunesResponse;
import uk.co.zenitech.intern.serializer.ResponseParser;
import uk.co.zenitech.intern.service.ITunesArtistService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:memdb",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
//@Disabled
public class ArtistControllerTests {

    @Mock
    ITunesFeignClient iTunesFeignClient;

    @Autowired
    ResponseParser responseParser;

    @Autowired
    ArtistRepository artistRepository;

    ObjectMapper mapper = new ObjectMapper();
    List<JsonNode> jsonNodes = Arrays.asList(
            mapper.readTree(
                    "{\"wrapperType\":\"artist\"," +
                            " \"kind\":\"Artist\"," +
                            " \"artistName\":\"Santana\"," +
                            " \"amgArtistId\":13645," +
                            " \"otherProperty\":\"This should not cause an error\"," +
                            " \"artistId\":\"217174\"}"),
            mapper.readTree(
                    "{\"wrapperType\":\"artist\"," +
                            " \"kind\":\"Artist\"," +
                            " \"artistName\":\"Santa\"," +
                            " \"amgArtistId\":1," +
                            " \"artistId\":\"5\"}")
    );

    private final ResponseEntity<ITunesResponse> artistResponseEntity = new ResponseEntity<>(new ITunesResponse(2L, jsonNodes), HttpStatus.OK);
    private final ResponseEntity<ITunesResponse> emptyResponseEntity = new ResponseEntity<>(new ITunesResponse(0L, new ArrayList<>()), HttpStatus.OK);
    private ArtistController artistController;

    public ArtistControllerTests() throws JsonProcessingException, IOException {
    }

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        ITunesArtistService iTunesArtistService = new ITunesArtistService(responseParser, artistRepository, iTunesFeignClient);
        this.artistController = new ArtistController(iTunesArtistService);
    }

    @Test
    void whenGettingMultipleArtists_thenReturnsResultsAndPersists() {
        when(iTunesFeignClient.getResults(any(), any(), any(), any())).thenReturn(artistResponseEntity);
        ResponseEntity<List<Artist>> responseEntity = artistController.getAllArtist("san", 2L);

        assertThat(responseEntity.getBody().size()).isEqualTo(2);
        assertThat(responseEntity.getBody().get(0).getArtistName()).isEqualTo("Santana");

        assertThat(artistRepository.findAll().size()).isEqualTo(2);
        assertThat(artistRepository.findAll())
                .extracting("artistId", "amgArtistId", "artistName")
                .contains(
                        tuple(217174L, 13645L, "Santana"),
                        tuple(5L, 1L, "Santa")
                );
    }

    @Test
    void whenFetchingArtist_thenReturnsResultAndPersists() {
        when(iTunesFeignClient.getById(anyLong())).thenReturn(artistResponseEntity);
        ResponseEntity<Artist> songResponse = artistController.getArtist(217174L);

        assertThat(songResponse.getBody().getArtistId()).isEqualTo(217174);

        assertThat(artistRepository.findById(217174L))
                .contains(new Artist(
                        217174L,
                        13645L,
                        "Santana"
                ));
    }

    @Test
    void givenArtistExistsInDb_whenFetchingArtist_thenReturnsArtistFromDb() {
        artistRepository.save(new Artist(
                217174L,
                13645L,
                "Santana"));
        ResponseEntity<Artist> artistResponse = artistController.getArtist(217174L);

        verify(iTunesFeignClient, times(0)).getById(anyLong());
        assertThat(artistResponse.getBody().getArtistId()).isEqualTo(217174L);
    }

    @Test
    void whenNoArtistFound_thenThrowException() {
        when(iTunesFeignClient.getById(anyLong())).thenReturn(emptyResponseEntity);
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> artistController.getArtist(43434L));
    }

}
