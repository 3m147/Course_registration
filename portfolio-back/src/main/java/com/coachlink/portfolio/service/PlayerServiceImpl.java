package com.coachlink.portfolio.service;

import com.coachlink.portfolio.dto.PlayerHistoryDTO;
import com.coachlink.portfolio.entity.PlayerHistory;
import com.coachlink.portfolio.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepository playerRepository;

    @Override
    public List<PlayerHistoryDTO> getPlayerHistoryList(String playerName) {
        List<PlayerHistory> result = playerRepository.getPlayerHistoryList(playerName);

        List<PlayerHistoryDTO> dtoList = result.stream()
                .map(this::entityToDTO)
                .toList();

        return dtoList;
    }
}
