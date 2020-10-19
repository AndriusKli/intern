package uk.co.zenitech.intern.service.authentication;

import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.co.zenitech.intern.client.AuthClient;
import uk.co.zenitech.intern.errorhandling.exceptions.ForbiddenException;
import uk.co.zenitech.intern.serializer.ResponseParser;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final AuthClient authClient;
    private final ResponseParser responseParser;

    public AuthServiceImpl(AuthClient authClient, ResponseParser responseParser) {
        this.authClient = authClient;
        this.responseParser = responseParser;
    }

    @Override
    public Long retrieveUid(String accessToken) {
        logger.info("Attempting to retrieve user information");
        Response userInfo = authClient.getUserInfo(accessToken);
        if (userInfo.status() == HttpStatus.OK.value()) {
            logger.info("User information retrieved successfully. Extracting subjectId.");
            return responseParser.extractSubjectId(userInfo);
        } else if (userInfo.status() == HttpStatus.FORBIDDEN.value()) {
            logger.info("Access token is either expired or invalid: {}", accessToken);
            throw new ForbiddenException("Invalid or expired access token");
        } else {
            logger.info("Something went wrong while attempting to retrieve user info.");
            throw new RuntimeException("Something went wrong while attempting to retrieve user info.");
        }
    }
}
