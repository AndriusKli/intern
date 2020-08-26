package uk.co.zenitech.intern.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.zenitech.intern.entity.Song;
import uk.co.zenitech.intern.serializer.ResponseParser;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongServiceImp implements SongService {

    private ResponseParser responseParser;

    @Autowired
    public SongServiceImp(ResponseParser responseParser) {
        this.responseParser = responseParser;
    }

    @Override
    public List<Song> getSongs(String songName) {
        if (songName == null) {
            return responseParser.parse(Song.class, "track");
        } else {
            return responseParser.parse(Song.class, "track").stream()
                    .filter(jsonsong -> jsonsong.getArtistName()
                            .toLowerCase()
                            .trim()
                            .contains(songName.toLowerCase().trim()))
                    .collect(Collectors.toList());
        }
    }
}
