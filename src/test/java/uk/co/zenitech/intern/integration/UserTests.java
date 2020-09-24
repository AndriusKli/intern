package uk.co.zenitech.intern.integration;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.co.zenitech.intern.entity.Playlist;
import uk.co.zenitech.intern.entity.User;
import uk.co.zenitech.intern.service.user.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource("/test.properties")
public class UserTests {

    @Autowired
    UserRepository userRepository;

    @LocalServerPort
    int port;

    private final User SAMPLE_USER = new User(1L, "Username");
    private final User SAMPLE_USER2 = new User(2L, "Andrius");
    private final Long SAMPLE_USER_ID = 1L;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }


    @Test
    void getsUsers() {
        userRepository.save(SAMPLE_USER);
        userRepository.save(SAMPLE_USER2);
        User[] users = RestAssured.when()
                .get("/api/users")
                .then()
                .assertThat().statusCode(200)
                .extract().as(User[].class);

        assertThat(users.length).isEqualTo(2);
        assertThat(users[1]).isEqualTo(SAMPLE_USER2);
    }

    @Test
    void getsUser() {
        userRepository.save(SAMPLE_USER);
        User user = RestAssured.when()
                .get("/api/users/{id}", SAMPLE_USER_ID)
                .then()
                .assertThat().statusCode(200)
                .extract().as(User.class);

        assertThat(user).isEqualTo(SAMPLE_USER);
    }

    @Test
    void createsUser() {
        RestAssured.given()
                .body(SAMPLE_USER)
                .when()
                .post("/api/users")
                .then()
                .assertThat().statusCode(201);

        assertThat(userRepository.findById(SAMPLE_USER_ID).get().getUserName()).isEqualTo("Username");
    }

    @Test
    void updatesUser() {
        userRepository.save(SAMPLE_USER);
        RestAssured.given()
                .body(new User(1L, "Updated name"))
                .when()
                .put("/api/users/{id}", SAMPLE_USER_ID)
                .then()
                .assertThat().statusCode(202);

        assertThat(userRepository.findById(SAMPLE_USER_ID).get().getUserName()).isEqualTo("Updated name");
    }

    @Test
    void deletesUser() {
        userRepository.save(SAMPLE_USER);
        RestAssured.when()
                .delete("/api/users/{id}", SAMPLE_USER_ID)
                .then()
                .assertThat().statusCode(204);

        assertThat(userRepository.findById(SAMPLE_USER_ID)).isEqualTo(Optional.empty());
    }
}
