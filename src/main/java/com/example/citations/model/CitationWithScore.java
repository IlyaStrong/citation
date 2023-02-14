package com.example.citations.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CitationWithScore {
    private Citation citation;
    private int score;
    private List<Integer> graph;
}
