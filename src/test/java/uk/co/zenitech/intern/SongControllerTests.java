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
import uk.co.zenitech.intern.controller.SongController;
import uk.co.zenitech.intern.entity.Song;
import uk.co.zenitech.intern.repository.SongRepository;
import uk.co.zenitech.intern.response.ITunesResponse;
import uk.co.zenitech.intern.serializer.ResponseParser;
import uk.co.zenitech.intern.service.ITunesSongService;

import java.io.IOException;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:memdb",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
//@Disabled
public class SongControllerTests {

    // Welcome to hell.

    @Mock
    ITunesFeignClient iTunesFeignClient;

    @Autowired
    ResponseParser responseParser;

    @Autowired
    SongRepository songRepository;

    ObjectMapper mapper = new ObjectMapper();
    List<JsonNode> jsonNodes = Arrays.asList(
            mapper.readTree(
                    "{\"wrapperType\":\"track\"," +
                            " \"kind\":\"song\"," +
                            " \"trackId\":118125081," +
                            " \"amgArtistId\":5," +
                            " \"artistName\":\"Pretenders\"," +
                            " \"trackName\":\"I'll Stand By You\"," +
                            " \"collectionName\":\"Last of the Independents\"}"),
            mapper.readTree(
                    "{\"wrapperType\":\"track\"," +
                            " \"kind\":\"song\"," +
                            " \"trackId\":323123," +
                            " \"amgArtistId\":55," +
                            " \"artistName\":\"Fake Band\"," +
                            " \"trackName\":\"Stand Fast\"," +
                            " \"collectionName\":\"Fake Album\"}")
    );

    private final ResponseEntity<ITunesResponse> songResponseEntity = new ResponseEntity<>(new ITunesResponse(2L, jsonNodes), HttpStatus.OK);
    private final ResponseEntity<ITunesResponse> emptyResponseEntity = new ResponseEntity<>(new ITunesResponse(0L, new ArrayList<>()), HttpStatus.OK);
    private SongController songController;

    public SongControllerTests() throws JsonProcessingException, IOException {
    }

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        ITunesSongService iTunesSongService = new ITunesSongService(responseParser, songRepository, iTunesFeignClient);
        this.songController = new SongController(iTunesSongService);
    }

// I know, some of these tests are redundant of meaningless, I'll delete those later; just trying out the syntax.

    @Test
    void whenGettingMultipleSongs_thenReturnsResultsAndPersists() {
        when(iTunesFeignClient.getResults(any(), any(), any(), any())).thenReturn(songResponseEntity);
        ResponseEntity<List<Song>> responseEntity = songController.getSongsByName("stand", 2L);

        assertThat(responseEntity.getStatusCode().is2xxSuccessful());
        assertThat(responseEntity.getBody().size()).isEqualTo(2);
        assertThat(responseEntity.getBody().get(0).getArtistName()).isEqualTo("Pretenders");

        assertThat(songRepository.findAll().size()).isEqualTo(2);
        assertThat(songRepository.findAll())
                .extracting("albumName")
                .contains("Last of the Independents", "Fake Album");
    }

    @Test
    void whenFetchingSong_thenReturnsResultAndPersists() {
        when(iTunesFeignClient.getById(anyLong())).thenReturn(songResponseEntity);
        ResponseEntity<Song> songResponse = songController.getSong(118125081L);

        assertThat(songResponse.getBody().getSongId()).isEqualTo(118125081);

        assertThat(songRepository.findById(118125081L))
                .contains(new Song(
                        118125081L,
                        "I'll Stand By You",
                        "Last of the Independents",
                        "Pretenders"));
    }

    @Test
    void givenSongExistsInDb_whenFetchingSong_thenReturnsSongFromDb() {
        songRepository.save(new Song(
                118125081L,
                "I'll Stand By You",
                "Last of the Independents",
                "Pretenders"));
        ResponseEntity<Song> songResponse = songController.getSong(118125081L);

        verify(iTunesFeignClient, times(0)).getById(anyLong());
        assertThat(songResponse.getBody().getSongId()).isEqualTo(118125081);
    }

    @Test
    void whenNoSongFound_thenThrowException() {
        when(iTunesFeignClient.getById(anyLong())).thenReturn(emptyResponseEntity);
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> songController.getSong(43434L));
    }

}

