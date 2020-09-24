package uk.co.zenitech.intern.service.playlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.zenitech.intern.entity.Playlist;
import uk.co.zenitech.intern.entity.Song;
import uk.co.zenitech.intern.entity.User;
import uk.co.zenitech.intern.service.user.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ITunesPlaylistService implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;

    @Autowired
    public ITunesPlaylistService(PlaylistRepository playlistRepository, UserRepository userRepository) {
        this.playlistRepository = playlistRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Playlist> getPlaylists(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
        return user.getPlaylists();
    }

    @Override
    public Playlist getPlaylist(Long userId, Long playlistId) {
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
        return user.getPlaylists()
                .stream()
                .filter(playlist -> playlist.getPlaylistId().equals(playlistId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public void createPlaylist(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
        Playlist playlist = new Playlist();
        user.getPlaylists().add(playlist);
        playlist.setUser(user);
        userRepository.save(user);
    }

    @Override
    public void addSong(Long playlistId, Song song) {

    }

    @Override
    public void removeSong(Long playlistId, Song song) {

    }
}
