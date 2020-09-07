package uk.co.zenitech.intern.errorhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

@Component
public class ITunesErrorHandler implements ResponseErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(ITunesErrorHandler.class);

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        if (clientHttpResponse.getStatusCode().isError()) {
            logger.debug("Status code: {}", clientHttpResponse.getStatusCode());
            logger.debug("Response: {}", clientHttpResponse.getStatusText());
        }
        return false;
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        logger.error("Error: {}", clientHttpResponse.getStatusText());
        // TODO talk about this part later.
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        logger.error("Error occurred while processing url: {}. Method: {}. Status text: {}", url, method, response.getStatusText());
    }
}
