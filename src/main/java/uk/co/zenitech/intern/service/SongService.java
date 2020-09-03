package uk.co.zenitech.intern.service;

import uk.co.zenitech.intern.entity.Song;

import java.util.List;

public interface SongService {
    List<Song> getSongs(String song, Long limit);
    Song getSong(Long id);
}
