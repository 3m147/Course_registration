package com.coachlink.portfolio.repository;

import com.coachlink.portfolio.entity.PlayerHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerRepository extends JpaRepository<PlayerHistory, Long> {

    @EntityGraph(attributePaths = { "member" }, type = EntityGraph.EntityGraphType.FETCH)
    @Query("select p from PlayerHistory p where p.member.username=:playerName ")
    List<PlayerHistory> getPlayerHistoryList(@Param("playerName") String playerName);
}
