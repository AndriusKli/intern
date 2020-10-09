package uk.co.zenitech.intern.service.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.zenitech.intern.entity.User;
import uk.co.zenitech.intern.errorhandling.exceptions.DuplicateEntryException;
import uk.co.zenitech.intern.errorhandling.exceptions.EntityNotInDbException;
import uk.co.zenitech.intern.service.song.SongService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ITunesUserService implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(ITunesUserService.class);
    private final UserRepository userRepository;

    @Autowired
    public ITunesUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotInDbException("user", id));
    }

    @Override
    public User findUserByUsername(String username) {
        return findUsers().stream()
                .filter(user -> user.getUserName().toLowerCase().equals(username.toLowerCase().trim()))
                .findFirst()
                .orElseThrow(() -> new EntityNotInDbException("user"));
    }

    @Override
    @Transactional
    public User createUser(User user) {
        Optional<User> existingUser = userRepository.findAll().stream()
                .filter(dbUser -> dbUser.getEmail().equals(user.getEmail()) || dbUser.getUserName().equals(user.getUserName()))
                .findFirst();
        if (existingUser.isEmpty()) {
            logger.info("Creating new user: {}", user);
            return userRepository.save(user);
        } else {
            logger.info("User with the provided email or username already exists: {}", user);
            throw new DuplicateEntryException("User with the provided email or username already exists.");
        }
    }

    @Override
    @Transactional
    public void updateUser(Long id, User user) {
        User updatableUser = findUser(id);
        updatableUser.setUserName(user.getUserName());
        updatableUser.setEmail(user.getEmail());
        userRepository.save(updatableUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
