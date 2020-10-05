package uk.co.zenitech.intern.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.zenitech.intern.entity.User;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ITunesUserService implements UserService {

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
                .orElseThrow(() -> new NoSuchElementException("User with the id " + id + " was not found."));
    }

    @Override
    public User findUserByUsername(String username) {
        return findUsers().stream()
                .filter(user -> user.getUserName().toLowerCase().equals(username.toLowerCase().trim()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("User with the username " + username + " was not found."));
    }

    @Override
    @Transactional
    public void createUser(User user) {
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(Long id, User user) {
        User updatableUser = findUser(id);
        updatableUser.setUserId(user.getUserId());
        updatableUser.setUserName(user.getUserName());
        userRepository.save(updatableUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
