package uk.co.zenitech.intern.service.user;

import uk.co.zenitech.intern.entity.User;

import java.util.List;

public interface UserService {
    List<User> findUsers();
    User findUser(Long id);
    User findUserByUsername(String username);
    User createUser(String accessToken, User user);
    void updateUser(Long id, User user);
    void deleteUser(Long id);
}
