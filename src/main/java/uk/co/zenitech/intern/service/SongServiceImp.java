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
    public List<Song> getSongsByName(String song) {
        if (song == null) {
            return responseParser.parseSongs();
        } else {
            return responseParser.parseSongs().stream()
                    .filter(jsonsong -> jsonsong.getArtistName().toLowerCase().contains(song.toLowerCase()))
                    .collect(Collectors.toList());
        }
    }
}
