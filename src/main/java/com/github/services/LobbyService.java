package com.github.services;

import com.github.entity.LobbyEntity;
import com.github.repository.LobbyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing lobbies
 */
@Service
public class LobbyService {

    private LobbyRepository lobbyRepository;

    /**
     * Constructor, params get autowired (injected) by Spring
     */
    @Autowired
    public LobbyService(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    /**
     * Creates a new lobby and saves it to the database
     */
    public LobbyEntity saveLobby(LobbyEntity lobby) {
        LobbyEntity lobbyEntity = new LobbyEntity(lobby.getPlayerUUID());
        lobbyRepository.save(lobbyEntity);
        return lobbyEntity;
    }

    /**
     * Deletes a lobby from the database
     */
    public LobbyEntity deleteLobby(LobbyEntity lobby) {
        lobbyRepository.delete(lobby);
        return lobby;
    }

    /**
     * Deletes a lobby from the database
     */
    public LobbyEntity deleteLobby(Integer id) {
        lobbyRepository.deleteById(id);
        return null;
    }

    /**
     * Gets a lobby from the database
     */
    public Optional<LobbyEntity> getLobbyByID(Integer id) {
        return lobbyRepository.findById(id);

    }
}
