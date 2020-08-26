package uk.co.zenitech.intern.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;
import uk.co.zenitech.intern.entity.Artist;
import uk.co.zenitech.intern.entity.Song;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ResponseParser {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T> List<T> parse(Class<T> clazz, String wrapperType) {
        List<T> list = new ArrayList<>();
        Optional<JsonNode> results = getResults();
        results.ifPresent(node -> node.forEach(entry -> {
                    if (entry.get("wrapperType").toString().equals(wrapperType)) {
                        try {
                            T object = objectMapper.treeToValue(entry, clazz);
                            list.add(object);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ));
        return list;
    }

    private Optional<JsonNode> getResults() {
        try {
            return Optional.ofNullable(objectMapper.readTree(new File("src/main/resources/response.json")).get("results"));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
