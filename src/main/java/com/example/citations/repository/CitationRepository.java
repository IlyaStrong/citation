package com.example.citations.repository;

import com.example.citations.model.Citation;
import com.example.citations.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CitationRepository extends JpaRepository<Citation, Integer>, PagingAndSortingRepository<Citation, Integer> {
    @Transactional
    @Modifying
    @Query("UPDATE Citation SET content = :content, author = :author WHERE id = :id")
    void updateCitation(@Param("content") String content, @Param("author") User author, @Param("id") int id);
}
