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
import uk.co.zenitech.intern.errorhandling.exceptions.ForbiddenException;
import uk.co.zenitech.intern.service.authentication.AuthService;
import uk.co.zenitech.intern.service.song.SongService;
import uk.co.zenitech.intern.service.user.UserRepository;
import uk.co.zenitech.intern.service.user.UserService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ITunesPlaylistService implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final SongService songService;
    private final UserService userService;
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(ITunesPlaylistService.class);

    @Autowired
    public ITunesPlaylistService(PlaylistRepository playlistRepository, UserRepository userRepository, SongService songService, UserService userService, AuthService authService) {
        this.playlistRepository = playlistRepository;
        this.songService = songService;
        this.userService = userService;
        this.authService = authService;
    }

    @Override
    public List<Playlist> getPlaylists(String accessToken) {
        Long uid = authService.retrieveUid(accessToken);
        return playlistRepository.findAll().stream()
                .filter(playlist -> playlist.getUser().getUid().equals(uid))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Playlist getPlaylist(String accessToken, Long playlistId) {
        Long uid = authService.retrieveUid(accessToken);
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new EntityNotInDbException("playlist"));
        if (playlist.getUser().getUid().equals(uid)) {
            return playlist;
        } else {
            throw new ForbiddenException("Unauthorized to perform requested action on playlist.");
        }
    }

    @Override
    public Playlist createPlaylist(String accessToken, Playlist playlist) {
        Long uid = authService.retrieveUid(accessToken);
        User user = userService.findUser(uid);
        Playlist playlistToCreate = new Playlist(playlist.getPlaylistName());
        user.getPlaylists().add(playlistToCreate);
        playlistToCreate.setUser(user);
        return playlistRepository.save(playlistToCreate);
    }

    @Override
    public Playlist updatePlaylist(String accessToken, Playlist playlist) {
        Long uid = authService.retrieveUid(accessToken);
        Playlist playlistToUpdate = playlistRepository
                .findById(playlist.getPlaylistId()).orElseThrow(() -> new EntityNotInDbException("playlist"));
        if (playlistToUpdate.getUser().getUid().equals(uid)) {
            playlistToUpdate.setPlaylistName(playlist.getPlaylistName());
            return playlistRepository.save(playlistToUpdate);
        } else {
            throw new ForbiddenException("Unauthorized to perform requested action on playlist.");
        }
    }

    @Override
    public void addSong(String accessToken, Long playlistId, Long songId) {
        Playlist playlist = getPlaylist(accessToken, playlistId);
        Song song = songService.getSong(songId);
        if (!playlist.getSongs().contains(song)) {
            logger.info("Adding song {} to user's (ID: {}) playlist (ID: {})", song, accessToken, playlistId);
            playlist.getSongs().add(song);
            playlistRepository.save(playlist);
        } else {
            logger.info("Song already exist in playlist (ID: {}): {}. Not adding duplicate.", playlistId, song);
            throw new DuplicateEntryException("Song already exists in the playlist.");
        }
    }

    @Override
    public void removeSong(String accessToken, Long playlistId, Long songId) {
        Playlist playlist = getPlaylist(accessToken, playlistId);
        Song song = songService.getSong(songId);
        logger.info("Removing song {} from user's (ID: {}) playlist (ID: {})", song, accessToken, playlistId);
        playlist.getSongs().remove(song);
        playlistRepository.save(playlist);
    }
}
