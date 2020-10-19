package uk.co.zenitech.intern.integration;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.co.zenitech.intern.entity.Playlist;
import uk.co.zenitech.intern.entity.Song;
import uk.co.zenitech.intern.entity.User;
import uk.co.zenitech.intern.service.authentication.AuthService;
import uk.co.zenitech.intern.service.playlist.PlaylistRepository;
import uk.co.zenitech.intern.service.song.SongRepository;
import uk.co.zenitech.intern.service.user.UserRepository;

import javax.transaction.Transactional;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource("/test.properties")
public class PlaylistTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    SongRepository songRepository;

    @Autowired
    PlaylistRepository playlistRepository;

    @MockBean
    AuthService authService;

    @LocalServerPort
    int port;

    private final User SAMPLE_USER = new User(1L, "Username", "user@email.com");
    private final Long SAMPLE_USER_ID = 1L;
    private final Song SAMPLE_SONG = new Song(2L, "name", "album", "artist");

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        MockitoAnnotations.initMocks(this);
    }

    // TODO tidy up tests
    @Test
    void getsPlaylists() {
        userRepository.save(SAMPLE_USER);
        User user = userRepository.findById(SAMPLE_USER_ID).get();
        Playlist playlist = new Playlist();
        playlist.setUser(user);
        user.setPlaylists(Collections.singletonList(playlist));
        userRepository.saveAndFlush(user);

        Mockito.when(authService.retrieveUid("token")).thenReturn(1L);

        Playlist[] playlists = RestAssured.when()
                .get("/api/users/{accessToken}/playlists", "token")
                .then()
                .assertThat().statusCode(200)
                .extract().as(Playlist[].class);

        assertThat(playlists.length).isEqualTo(1);
        assertThat(playlists[0].getSongs()).isEmpty();
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

        Mockito.when(authService.retrieveUid("token")).thenReturn(1L);

        Playlist playlist = RestAssured.when()
                .get("/api/users/{accessToken}/playlists/{playlistId}", "token", 1L)
                .then()
                .log().all()
                .assertThat().statusCode(200)
                .extract().as(Playlist.class);

        assertThat(playlist).isNotNull();
        assertThat(playlist.getSongs()).isEmpty();
        assertThat(playlistRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    void createsPlaylist() {
        userRepository.save(SAMPLE_USER);
        Mockito.when(authService.retrieveUid("token")).thenReturn(1L);
        RestAssured.given()
                .body(SAMPLE_USER)
                .post("/api/users/{accessToken}/playlists", "token")
                .then()
                .assertThat().statusCode(200);

        assertThat(playlistRepository.findAll().size()).isEqualTo(1L);
        assertThat(playlistRepository.findAll().get(0).getUser().getUid()).isEqualTo(SAMPLE_USER_ID);
    }

    @Test
    void addsSong() {
        userRepository.save(SAMPLE_USER);
        songRepository.save(SAMPLE_SONG);
        Mockito.when(authService.retrieveUid("token")).thenReturn(1L);
        RestAssured.given().body(SAMPLE_USER).post("/api/users/{accessToken}/playlists", "token");

        RestAssured.given()
                .param("songId", 2)
                .when()
                .post("/api/users/{accessToken}/playlists/{playlistId}", "token", 1L)
                .then()
                .assertThat().statusCode(202);
    }
}
