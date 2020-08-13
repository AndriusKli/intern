package uk.co.zenitech.intern.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.zenitech.intern.entity.Artist;
import uk.co.zenitech.intern.serializer.ResponseParser;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtistServiceImp implements ArtistService {

    private ResponseParser responseParser;

    @Autowired
    public ArtistServiceImp(ResponseParser responseParser) {
        this.responseParser = responseParser;
    }

    @Override
    public List<Artist> getArtistByName(String artist) {
        if (artist == null) {
            return responseParser.parseArtists();
        } else {
            return responseParser.parseArtists().stream()
                    .filter(jsonartist -> jsonartist.getArtistName().toLowerCase().contains(artist.toLowerCase()))
                    .collect(Collectors.toList());
        }
    }
}
