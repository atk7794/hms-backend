package com.example.hms.repository;

import com.example.hms.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // İleride user ile patient birlikte çekmek istersen, @EntityGraph ile fetch yapabilirsin:
    @EntityGraph(attributePaths = {"patient", "doctor"})
    Optional<User> findByEmail(String email);

}
