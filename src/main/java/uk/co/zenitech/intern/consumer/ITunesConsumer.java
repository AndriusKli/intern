package uk.co.zenitech.intern.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.co.zenitech.intern.errorhandler.ITunesErrorHandler;

@Component
public class ITunesConsumer {

    RestTemplate restTemplate = new RestTemplate();
    Logger logger = LoggerFactory.getLogger(ITunesConsumer.class);

    @Autowired
    public ITunesConsumer(ITunesErrorHandler iTunesErrorHandler) {
        restTemplate.setErrorHandler(iTunesErrorHandler);
    }

    public ResponseEntity<String> fetchData(String entity, String searchTerm, String attribute) {
        logger.info("Fetching data. Entity: {} Search term: {} Attribute: {}", entity, searchTerm, attribute);
        return restTemplate.getForEntity("https://itunes.apple.com/search?entity={entity}&term={searchTerm}&attribute={attribute}&limit=200",
                String.class,
                entity,
                searchTerm,
                attribute);
    }

    public ResponseEntity<String> fetchById(Long id) {
        logger.info("Fetching data by Id. Id: {}", id);
        return restTemplate.getForEntity("https://itunes.apple.com/lookup?id={id}",
                String.class,
                id);
    }
}
