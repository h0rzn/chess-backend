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
        LobbyEntity lobbyEntity = new LobbyEntity(lobby.getPlayerUUID());
        lobbyRepository.save(lobbyEntity);
        return lobbyEntity;
    }

    public LobbyEntity deleteLobby(LobbyEntity lobby) {
        lobbyRepository.delete(lobby);
        return lobby;
    }

    public LobbyEntity deleteLobby(Integer id) {
        lobbyRepository.deleteById(id);
        return null;
    }

    public Optional<LobbyEntity> getLobbyByID(Integer id) {
        return lobbyRepository.findById(id);

    }
}
