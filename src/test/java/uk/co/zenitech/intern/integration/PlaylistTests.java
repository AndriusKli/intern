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
import java.util.Arrays;
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

    private final User SAMPLE_USER = new User(1L, "Username");
    private final Long SAMPLE_USER_ID = 1L;
    private final Song SAMPLE_SONG = new Song(2L, "name", "album", "artist");

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    void getsPlaylists() {
        userRepository.save(SAMPLE_USER);
        User user = userRepository.findById(SAMPLE_USER_ID).get();
        Playlist playlist = new Playlist();
        playlist.setUser(user);
        user.setPlaylists(Collections.singletonList(playlist));
        userRepository.saveAndFlush(user);

        Playlist[] playlists = RestAssured.when()
                .get("/api/users/{id}/playlists", SAMPLE_USER_ID)
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

        Playlist playlist = RestAssured.when()
                .get("/api/users/{id}/playlists/{playlistId}", SAMPLE_USER_ID, 2L)
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
        RestAssured.when()
                .post("/api/users/{id}/playlists", SAMPLE_USER_ID)
                .then()
                .assertThat().statusCode(201);

        assertThat(playlistRepository.findAll().size()).isEqualTo(1L);
        assertThat(playlistRepository.findAll().get(0).getUser().getUserId()).isEqualTo(SAMPLE_USER_ID);
    }

    @Test
    @Transactional
    void addsSong() {
        RestAssured.given().body(SAMPLE_USER).post("/api/users");
        RestAssured.post("/api/users/{id}/playlists", SAMPLE_USER_ID);
        RestAssured.given().body(SAMPLE_SONG).post("/api/songs");

        RestAssured.given()
                .body(SAMPLE_SONG)
                .when()
                .put("/api/users/{userId}/playlists/{playlistId}", SAMPLE_USER_ID, 2)
                .then()
                .assertThat().statusCode(202);


        assertThat(playlistRepository.findById(2L).get().getSongs().size()).isEqualTo(1L);
    }

//    @Test
//    void addsSong() {
//        User user = new User();
//        Song song = SAMPLE_SONG;
//        Playlist playlist = new Playlist();
//
//        playlist.setUser(user);
//        user.setPlaylists(Collections.singletonList(playlist));
//
//        song.setPlaylists(Collections.singletonList(playlist));
//        playlist.setSongs(Collections.singletonList(song));
//
//        userRepository.save(user);
//
//        RestAssured.given()
//                .body(SAMPLE_SONG)
//                .when()
//                .put("/api/users/{userId}/playlists/{playlistId}", SAMPLE_USER_ID, 2)
//                .then()
//                .assertThat().statusCode(202);
//
//        assertThat(playlistRepository.findById(2L).get().getSongs().size()).isEqualTo(1L);
//    }
}
