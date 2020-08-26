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
    public List<Artist> getArtists(String artistName) {
        if (artistName == null) {
            return responseParser.parse(Artist.class, "track");
        } else {
            return responseParser.parse(Artist.class, "track").stream()
                    .filter(jsonartist -> jsonartist.getArtistName()
                            .toLowerCase()
                            .trim()
                            .contains(artistName.toLowerCase().trim()))
                    .collect(Collectors.toList());
        }
    }
}
