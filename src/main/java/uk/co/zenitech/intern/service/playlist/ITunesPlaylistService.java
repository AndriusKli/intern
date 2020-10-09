package uk.co.zenitech.intern.service.playlist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.zenitech.intern.entity.Playlist;
import uk.co.zenitech.intern.entity.Song;
import uk.co.zenitech.intern.entity.User;
import uk.co.zenitech.intern.errorhandling.exceptions.DuplicateEntryException;
import uk.co.zenitech.intern.errorhandling.exceptions.EntityNotInDbException;
import uk.co.zenitech.intern.service.song.SongService;
import uk.co.zenitech.intern.service.user.UserRepository;
import uk.co.zenitech.intern.service.user.UserService;

import java.util.List;


@Service
public class ITunesPlaylistService implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final SongService songService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(ITunesPlaylistService.class);

    @Autowired
    public ITunesPlaylistService(PlaylistRepository playlistRepository, UserRepository userRepository, SongService songService, UserService userService) {
        this.playlistRepository = playlistRepository;
        this.userRepository = userRepository;
        this.songService = songService;
        this.userService = userService;
    }

    @Override
    public List<Playlist> getPlaylists(Long userId) {
        User user = userService.findUser(userId);
        return user.getPlaylists();
    }

    @Override
    public Playlist getPlaylist(Long userId, Long playlistId) {
        User user = userService.findUser(userId);
        return user.getPlaylists()
                .stream()
                .filter(playlist -> playlist.getPlaylistId().equals(playlistId))
                .findFirst()
                .orElseThrow(() -> new EntityNotInDbException("playlist"));
    }

    @Override
    public Playlist createPlaylist(Long userId, Playlist playlist) {
        User user = userService.findUser(userId);
        Playlist playlistToCreate = new Playlist(playlist.getPlaylistName());
        user.getPlaylists().add(playlistToCreate);
        playlistToCreate.setUser(user);
        return playlistRepository.save(playlistToCreate);
    }

    @Override
    public Playlist updatePlaylist(Playlist playlist) {
        Playlist playlistToUpdate = playlistRepository
                .findById(playlist.getPlaylistId()).orElseThrow(() -> new EntityNotInDbException("playlist"));
        playlistToUpdate.setPlaylistName(playlist.getPlaylistName());
        return playlistRepository.save(playlistToUpdate);
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
            throw new DuplicateEntryException("Song already exists in the playlist.");
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
