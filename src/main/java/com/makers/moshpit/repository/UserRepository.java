package com.makers.moshpit.repository;

import com.makers.moshpit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findUserByEmail(String email);
    public Optional<User> findByName(String name);
    List<User> findByUsernameContainingIgnoreCase(String username);
    Optional<User> findByAuth0Sub(String auth0Sub);

}
