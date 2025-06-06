package com.example.Timetables.TimetableApp.repository;

import com.example.Timetables.TimetableApp.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository that finds users from their email or username
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByVerificationCode(String verificationCode);
    Optional<User> findById(long id);
}
