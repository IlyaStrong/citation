package com.example.citations.repository;

import com.example.citations.model.Citation;
import com.example.citations.model.User;
import com.example.citations.model.Vote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Integer>, PagingAndSortingRepository<Vote, Integer> {

    @Query("SELECT c, SUM(v.score) as s FROM Citation c, Vote v WHERE v.citation = c GROUP BY c ORDER BY s DESC ")
    List<Citation> getTop(Pageable pageable);

    @Query("SELECT v FROM Vote v WHERE v.citation = :citation ORDER BY v.createdAt ASC")
    List<Vote> getGraph(@Param("citation") Citation citation);
}
