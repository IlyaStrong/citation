package com.example.citations.repository;

import com.example.citations.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, PagingAndSortingRepository<User, Integer> {
    @Transactional
    @Modifying
    @Query("UPDATE User SET password = :password WHERE id = :id")
    void updateUser(@Param("password") String password, @Param("id") int id);
}
