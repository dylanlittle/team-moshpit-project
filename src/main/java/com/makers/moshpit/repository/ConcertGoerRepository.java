package com.makers.moshpit.repository;

import com.makers.moshpit.model.ConcertGoer;
import com.makers.moshpit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConcertGoerRepository extends JpaRepository<ConcertGoer, Long> {
    List<User> findUserByConcertId(Long concertId);
}