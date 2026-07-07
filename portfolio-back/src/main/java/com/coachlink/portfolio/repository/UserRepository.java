package com.coachlink.portfolio.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coachlink.portfolio.entity.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Member, String>{

    @EntityGraph(attributePaths = {"roleSet", "sportName"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select m from Member m where m.username=:username")
    Optional<Member> findByUsername(@Param("username") String username);

    @EntityGraph(attributePaths = {"roleSet"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select m from Member m where m.fromSocial=:social and m.email=:email")
    Optional<Member> findByEmail(@Param("email") String email, @Param("social") boolean social);

}
