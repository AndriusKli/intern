package uk.co.zenitech.intern.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.models.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ResponseParser {
    private final ObjectMapper objectMapper = new ObjectMapper();
    Logger logger = LoggerFactory.getLogger(ResponseParser.class);

    public <T> List<T> parse(Class<T> clazz, String wrapperType, ResponseEntity<String> responseEntity) {
        List<T> list = new ArrayList<>();
        getResults(responseEntity).ifPresent(node -> node.forEach(entry -> {
                    if (entry.get("wrapperType").toString().equals(wrapperType)) {
                        try {
                            T object = objectMapper.treeToValue(entry, clazz);
                            list.add(object);
                        } catch (JsonProcessingException e) {
                            logger.error("Error parsing json: {}. ", e.getMessage());
                        }
                    }
                }
        ));
        return list;
    }

    private Optional<JsonNode> getResults(ResponseEntity<String> responseEntity) {
        try {
            return Optional.ofNullable(objectMapper.readTree(responseEntity.getBody()).get("results"));
        } catch (JsonProcessingException e) {
            logger.error("Error parsing json: {}. ", e.getMessage());
            return Optional.empty();
        }
    }
}
