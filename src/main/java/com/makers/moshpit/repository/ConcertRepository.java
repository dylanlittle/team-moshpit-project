package com.makers.moshpit.repository;

import com.makers.moshpit.model.Concert;
import com.makers.moshpit.model.Post;
import com.makers.moshpit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ConcertRepository extends JpaRepository<Concert, Long> {
    // find all concerts for an artist where the date is in the future, order by date (soonest first)
    List<Concert> findAllByArtistIdAndConcertDateAfterOrderByConcertDateAsc(Long artistId, LocalDate dateToday);
    List<Concert> findAllByArtistIdAndConcertDateBeforeOrderByConcertDateDesc(Long artistId, LocalDate dateToday);
    List<Concert> findByConcertDateAfterOrderByConcertDateAsc(LocalDate dateToday);

    @Query("SELECT c FROM Concert c JOIN Follow f ON c.artist = f.artist WHERE f.user = :user")
    List<Concert> findConcertsByFollowedArtists(@Param("user") User user);
}
