package com.coachlink.portfolio.repository;

import com.coachlink.portfolio.entity.SportName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SportNameRepository extends JpaRepository<SportName, Long> {

    @Query("select s from SportName s where s.subName=:subName")
    Optional<SportName> findBySubName(@Param("subName") String subName);
}
