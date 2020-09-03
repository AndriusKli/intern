package uk.co.zenitech.intern.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.zenitech.intern.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
