package com.coachlink.portfolio.service;

import com.coachlink.portfolio.dto.PlayerHistoryDTO;
import com.coachlink.portfolio.entity.PlayerHistory;
import java.util.List;

public interface PlayerService {
    List<PlayerHistoryDTO> getPlayerHistoryList(String playerName);

    default PlayerHistoryDTO entityToDTO(PlayerHistory playerHistory) {
        return PlayerHistoryDTO.builder()
                .content(playerHistory.getContent())
                .startDate(playerHistory.getStartDate())
                .endDate(playerHistory.getEndDate())
                .build();
    }
}
