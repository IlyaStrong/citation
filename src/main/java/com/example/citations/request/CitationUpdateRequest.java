package com.example.citations.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CitationUpdateRequest {
    private int id;
    private String content;
    private int authorId;
}
