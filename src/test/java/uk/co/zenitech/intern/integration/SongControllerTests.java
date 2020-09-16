package uk.co.zenitech.intern.integration;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.co.zenitech.intern.client.ITunesFeignClient;
import uk.co.zenitech.intern.controller.SongController;
import uk.co.zenitech.intern.entity.Song;
import uk.co.zenitech.intern.repository.SongRepository;
import uk.co.zenitech.intern.response.ITunesResponse;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource("/test.properties")
public class SongControllerTests {

    @MockBean
    ITunesFeignClient iTunesFeignClient;

    @Autowired
    SongController songController;

    @Autowired
    SongRepository songRepository;

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

    public SongControllerTests() throws IOException {
    }

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void whenGettingMultipleSongs_thenReturnsResultsAndPersists() {
        when(iTunesFeignClient.getResults(any(), any(), any(), any())).thenReturn(songResponseEntity);
        ResponseEntity<List<Song>> responseEntity = songController.getSongsByName("stand", 2L);

        assertThat(responseEntity.getStatusCode().is2xxSuccessful());
        assertThat(responseEntity.getBody().size()).isEqualTo(2);
        assertThat(responseEntity.getBody().get(0).getArtistName()).isEqualTo("Pretenders");

        assertThat(songRepository.findAll().size()).isEqualTo(2);
        assertThat(songRepository.findAll()).contains(SONG_FAKE, SONG_PRETENDERS);
    }

    @Test
    void whenFetchingSong_thenReturnsResultAndPersists() {
        when(iTunesFeignClient.getById(anyLong())).thenReturn(songResponseEntity);
        ResponseEntity<Song> songResponse = songController.getSong(ID_PRETENDERS);

        assertThat(songResponse.getBody().getSongId()).isEqualTo(ID_PRETENDERS);

        assertThat(songRepository.findById(ID_PRETENDERS))
                .contains(SONG_PRETENDERS);
    }

    @Test
    void givenSongExistsInDb_whenFetchingSong_thenReturnsSongFromDb() {
        songRepository.save(SONG_PRETENDERS);
        ResponseEntity<Song> songResponse = songController.getSong(ID_PRETENDERS);

        verify(iTunesFeignClient, times(0)).getById(anyLong());
        assertThat(songResponse.getBody().getSongId()).isEqualTo(ID_PRETENDERS);
    }

    @Test
    void whenNoSongFound_thenThrowException() {
        when(iTunesFeignClient.getById(anyLong())).thenReturn(emptyResponseEntity);
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> songController.getSong(43434L));
    }

}

