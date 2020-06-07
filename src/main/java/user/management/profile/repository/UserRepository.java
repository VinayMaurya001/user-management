package user.management.profile.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import user.management.profile.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
