package uk.co.zenitech.intern.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import uk.co.zenitech.intern.entity.Artist;
import uk.co.zenitech.intern.entity.Song;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ResponseParser {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Artist> parseArtists() {
        List<Artist> artists = new ArrayList<>();
        getResults().forEach(entry -> {
            if (entry.get("wrapperType").toString().equals("\"artist\"")) {
                try {
                    Artist artist = objectMapper.treeToValue(entry, Artist.class);
                    artists.add(artist);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });
        return artists;
    }

    public List<Song> parseSongs() {
        List<Song> songs = new ArrayList<>();
        getResults().forEach(entry -> {
            if (entry.get("wrapperType").toString().equals("\"track\"")) {
                try {
                    Song song = objectMapper.treeToValue(entry, Song.class);
                    songs.add(song);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });
        return songs;
    }


    private JsonNode getResults() {
        try {
            return objectMapper.readValue(new File("src/main/resources/response.json"), ObjectNode.class).get("results");
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }
}
