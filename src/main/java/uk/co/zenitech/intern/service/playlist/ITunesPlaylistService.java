package uk.co.zenitech.intern.service.playlist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.zenitech.intern.entity.Playlist;
import uk.co.zenitech.intern.entity.Song;
import uk.co.zenitech.intern.entity.User;
import uk.co.zenitech.intern.service.song.SongService;
import uk.co.zenitech.intern.service.user.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;


@Service
public class ITunesPlaylistService implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final SongService songService;
    private static final Logger logger = LoggerFactory.getLogger(ITunesPlaylistService.class);

    @Autowired
    public ITunesPlaylistService(PlaylistRepository playlistRepository, UserRepository userRepository, SongService songService) {
        this.playlistRepository = playlistRepository;
        this.userRepository = userRepository;
        this.songService = songService;
    }

    @Override
    public List<Playlist> getPlaylists(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Playlist with the id " + userId + " was not found."));
        return user.getPlaylists();
    }

    @Override
    public Playlist getPlaylist(Long userId, Long playlistId) {
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
        return user.getPlaylists()
                .stream()
                .filter(playlist -> playlist.getPlaylistId().equals(playlistId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("User with the id " + userId + " was not found."));
    }

    @Override
    public void createPlaylist(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
        Playlist playlist = new Playlist();
        user.getPlaylists().add(playlist);
        playlist.setUser(user);
        userRepository.saveAndFlush(user);
    }

    @Override
    public void addSong(Long userId, Long playlistId, Long songId) {
        Playlist playlist = getPlaylist(userId, playlistId);
        Song song = songService.getSong(songId);
        if (!playlist.getSongs().contains(song)) {
            logger.info("Adding song {} to user's (ID: {}) playlist (ID: {})", song, userId, playlistId);
            playlist.getSongs().add(song);
            playlistRepository.save(playlist);
        } else {
            logger.info("Song already exist in playlist (ID: {}): {}. Not adding duplicate.", playlistId, song);
        }
    }

    @Override
    public void removeSong(Long userId, Long playlistId, Long songId) {
        Playlist playlist = getPlaylist(userId, playlistId);
        Song song = songService.getSong(songId);
        logger.info("Removing song {} from user's (ID: {}) playlist (ID: {})", song, userId, playlistId);
        playlist.getSongs().remove(song);
        playlistRepository.save(playlist);
    }
}
