package uk.co.zenitech.intern.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.co.zenitech.intern.errorhandling.exceptions.ParsingException;
import uk.co.zenitech.intern.response.ITunesResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResponseParser {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(ResponseParser.class);

    public <T> List<T> parse(Class<T> clazz, String wrapperType, ResponseEntity<ITunesResponse> responseEntity) {
        return getResults(responseEntity)
                .stream()
                .filter(entry -> entry.get("wrapperType").asText().equals(wrapperType))
                .map(entry -> getClazz(entry, clazz) )
                .collect(Collectors.toList());
    }

    private <T> T getClazz(JsonNode entry, Class<T> clazz) {
        try {
            return objectMapper.treeToValue(entry, clazz);
        } catch (JsonProcessingException e) {
            logger.info("Error processing jsonNode: {} Cause: {}" , entry, e.getMessage());
            throw new ParsingException("Error parsing jsonNode");
        }
    }

    private List<JsonNode> getResults(ResponseEntity<ITunesResponse> responseEntity) {
        return responseEntity.getBody() != null ? responseEntity.getBody().getResults() : new ArrayList<>();
    }
}
