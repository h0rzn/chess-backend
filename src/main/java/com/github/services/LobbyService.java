package com.github.services;

import com.github.entity.LobbyEntity;
import com.github.repository.LobbyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class LobbyService {

    private LobbyRepository lobbyRepository;

    @Autowired
    public LobbyService(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    public LobbyEntity saveLobby(LobbyEntity lobby) {
        lobbyRepository.save(lobby);
        return lobby;
    }

    public Optional<LobbyEntity> getLobbyByID(Integer id) {
        return lobbyRepository.findById(id);

    }
}
