package com.example.citations.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "votes",
        uniqueConstraints = {
                @UniqueConstraint(name = "SingleVote", columnNames = {"citation_id", "user_id"})
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int score;

    @ManyToOne
    @JoinColumn(name = "citation_id")
    private Citation citation;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Instant createdAt;
}
