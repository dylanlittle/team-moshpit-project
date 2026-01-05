package com.makers.moshpit.repository;

import com.makers.moshpit.model.Concert;
import com.makers.moshpit.model.ConcertGoer;
import com.makers.moshpit.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConcertGoerRepository extends JpaRepository<ConcertGoer, Long> {
    @Query("SELECT cg.user FROM ConcertGoer cg WHERE cg.concert.id = :concertId")
    List<User> findUsersByConcertId(@Param("concertId") Long concertId);
    boolean existsByUserAndConcert(User user, Concert concert);
    @Transactional
    @Modifying
    void deleteByUserAndConcert(User user, Concert concert);
}