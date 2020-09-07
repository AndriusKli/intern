package uk.co.zenitech.intern.serializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.co.zenitech.intern.response.ITunesResponse;

import java.util.ArrayList;
import java.util.List;

@Component
public class ResponseParser {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(ResponseParser.class);

    public <T> List<T> parse(Class<T> clazz, String wrapperType, ResponseEntity<ITunesResponse> responseEntity) {
        List<T> list = new ArrayList<>();
        getResults(responseEntity).forEach(entry -> {
                    if (entry.get("wrapperType").asText().equals(wrapperType)) {
                        try {
                            T object = objectMapper.treeToValue(entry, clazz);
                            list.add(object);
                        } catch (JsonProcessingException e) {
                            logger.error("Error parsing json: {}. ", e.getMessage());
                            throw new RuntimeException("Failed to parse provided response entity");
                        }
                    }
                }
        );
        return list;
    }

    private List<JsonNode> getResults(ResponseEntity<ITunesResponse> responseEntity) {
        return responseEntity.getBody() != null ? responseEntity.getBody().getResults() : new ArrayList<>();
    }

// I'm really not sure about extracting the if try catch block into a different method. I'd just end up with another
// method that takes three parameters, which is equally as messy looking.
//    private <T> T processNode(JsonNode node) {
//        return null;
//    }
}
