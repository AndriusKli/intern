package uk.co.zenitech.intern.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.bind.annotation.RestController;
import uk.co.zenitech.intern.client.ITunesFeignClient;
import uk.co.zenitech.intern.entity.Album;
import uk.co.zenitech.intern.entity.Playlist;
import uk.co.zenitech.intern.entity.Song;
import uk.co.zenitech.intern.entity.User;
import uk.co.zenitech.intern.service.playlist.PlaylistRepository;
import uk.co.zenitech.intern.service.song.SongRepository;
import uk.co.zenitech.intern.service.user.UserRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource("/test.properties")
public class PlaylistTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlaylistRepository playlistRepository;

    @LocalServerPort
    int port;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final User SAMPLE_USER = new User(1L, "Username");
    private final Long SAMPLE_USER_ID = 1L;
    private final Playlist EMPTY_PLAYLIST = new Playlist();

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    void getsPlaylists() {
        userRepository.save(SAMPLE_USER);
        User user = userRepository.findById(SAMPLE_USER_ID).get();
        user.setPlaylists(new ArrayList<>());
//        EMPTY_PLAYLIST.setUser(user);
        user.setPlaylists(Collections.singletonList(EMPTY_PLAYLIST));
        userRepository.save(user);

        Playlist[] playlists = RestAssured.when()
                .get("/api/users/{id}/playlists", SAMPLE_USER_ID)
                .then()
                .assertThat().statusCode(200)
                .extract().as(Playlist[].class);

        System.out.println(playlists[0].getUser());
        assertThat(playlists.length).isEqualTo(1);
        assertThat(playlistRepository.findAll().size()).isEqualTo(1);

    }

    @Test
    void getsPlaylist() {
        userRepository.save(SAMPLE_USER);
        User user = userRepository.findById(SAMPLE_USER_ID).get();
        Playlist emptyPlaylist = new Playlist();
        emptyPlaylist.setUser(user);
        user.setPlaylists(Collections.singletonList(emptyPlaylist));
        userRepository.save(user);

        Playlist playlist = RestAssured.when()
                .get("/api/users/{id}/playlists/{playlistId}", SAMPLE_USER_ID, 2L)
                .then()
                .log().all()
                .assertThat().statusCode(200)
                .extract().as(Playlist.class);

        //assertThat(playlist.getUser()).isNotNull();
        assertThat(playlist.getPlaylistId()).isEqualTo(2L);
        assertThat(playlistRepository.findById(2L)).isEmpty();
    }

    @Test
    void createsPlaylist() {
        userRepository.save(SAMPLE_USER);
        RestAssured.when()
                .post("/api/users/{id}/playlists", SAMPLE_USER_ID)
                .then()
                .assertThat().statusCode(201);

        assertThat(playlistRepository.findAll().size()).isEqualTo(1L);
        assertThat(playlistRepository.findAll().get(0).getUser().getUserId()).isEqualTo(SAMPLE_USER_ID);
    }
}
