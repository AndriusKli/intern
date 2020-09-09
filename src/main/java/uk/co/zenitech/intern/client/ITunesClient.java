package uk.co.zenitech.intern.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.co.zenitech.intern.errorhandler.ITunesErrorHandler;
import uk.co.zenitech.intern.response.ITunesResponse;

import java.util.Collections;

@Component
@Deprecated
public class ITunesClient {

    private final Long DEFAULT_LIMIT = 200L;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = LoggerFactory.getLogger(ITunesClient.class);

    @Autowired
    public ITunesClient(ITunesErrorHandler iTunesErrorHandler, MappingJackson2HttpMessageConverter converter) {
        restTemplate.setErrorHandler(iTunesErrorHandler);
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        restTemplate.setMessageConverters(Collections.singletonList(converter));
    }

    public ResponseEntity<ITunesResponse> fetchData(String searchTerm, String entity, String attribute, Long limit) {
        logger.info("Fetching data. Search term: {} Entity: {} Attribute: {}", searchTerm, entity, attribute);
        return restTemplate.getForEntity("${itunes.endpoint}/search?term={searchTerm}&entity={entity}&attribute={attribute}&limit={limit}",
                ITunesResponse.class,
                entity,
                searchTerm,
                attribute,
                limit != null ? limit : DEFAULT_LIMIT);
    }

    public ResponseEntity<ITunesResponse> fetchById(Long id) {
        logger.info("Fetching data by Id. Id: {}", id);
        return restTemplate.getForEntity("${itunes.endpoint}/lookup?id={id}",
                ITunesResponse.class,
                id);
    }
}
