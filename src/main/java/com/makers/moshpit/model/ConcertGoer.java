package com.makers.moshpit.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "concert_goers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "concert_id"}))
public class ConcertGoer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "concert_id")
    private Concert concert;

    public ConcertGoer(User user, Concert concert) {
        this.user = user;
        this.concert = concert;
    }
}
