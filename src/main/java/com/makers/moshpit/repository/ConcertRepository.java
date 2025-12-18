package com.makers.moshpit.repository;

import com.makers.moshpit.model.Concert;
import com.makers.moshpit.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ConcertRepository extends JpaRepository<Concert, Long> {
    // find all concerts for an artist where the date is in the future, order by date (soonest first)
    List<Concert> findAllByArtistIdAndConcertDateAfterOrderByConcertDateAsc(Long artistId, LocalDate dateToday);
}
