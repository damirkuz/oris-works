package ru.kuzdikenov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.kuzdikenov.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query(value = "select u from User u where u.username = :username")
    Optional<User> getByUsername(String username);

    @Query(value = "SELECT * FROM users WHERE users.username = ?1", nativeQuery = true)
    Optional<User> getByUsernameNative(String username);

    void deleteByUsername(String username);


    Optional<User> findByVerificationCode(String verificationCode);
}
