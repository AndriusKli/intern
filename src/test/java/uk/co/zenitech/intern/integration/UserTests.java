package uk.co.zenitech.intern.integration;

import feign.Response;
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
import uk.co.zenitech.intern.client.AuthClient;
import uk.co.zenitech.intern.entity.User;
import uk.co.zenitech.intern.service.authentication.AuthService;
import uk.co.zenitech.intern.service.user.UserRepository;

import java.util.Optional;

import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource("/test.properties")
public class UserTests {

    @Autowired
    UserRepository userRepository;

    @MockBean
    AuthService authService;

    @LocalServerPort
    int port;

    private final User SAMPLE_USER = new User(1L, "Username", "user@email.com");
    private final User SAMPLE_USER2 = new User(2L, "Andrius", "andrius@email.com");
    private final Long SAMPLE_USER_ID = 1L;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void getsUsers() {
        userRepository.save(SAMPLE_USER);
        userRepository.save(SAMPLE_USER2);
        User[] users = when()
                .get("/api/users/all")
                .then()
                .assertThat().statusCode(200)
                .extract().as(User[].class);

        assertThat(users.length).isEqualTo(2);
        assertThat(users[1]).isEqualTo(SAMPLE_USER2);
    }

    @Test
    void getsUser() {
        userRepository.save(SAMPLE_USER);
        User user = when()
                .get("/api/users/{id}", SAMPLE_USER_ID)
                .then()
                .assertThat().statusCode(200)
                .extract().as(User.class);

        assertThat(user).isEqualTo(SAMPLE_USER);
    }

    @Test
    void createsUser() {
        Mockito.when(authService.retrieveUid("accessToken")).thenReturn(1L);
        RestAssured.given()
                .body(SAMPLE_USER)
                .when()
                .post("/api/users/accessToken")
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
    void testuid() {

    }

    @Test
    void deletesUser() {
        userRepository.save(SAMPLE_USER);
        when()
                .delete("/api/users/{id}", SAMPLE_USER_ID)
                .then()
                .assertThat().statusCode(204);

        assertThat(userRepository.findById(SAMPLE_USER_ID)).isEqualTo(Optional.empty());
    }
}
