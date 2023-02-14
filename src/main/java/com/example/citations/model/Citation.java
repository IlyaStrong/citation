package com.example.citations.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "citations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Citation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String content;

    @ManyToOne
    private User author;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "citation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes;

    private Instant updatedAt;
}
