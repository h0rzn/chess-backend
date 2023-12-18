package com.github.services;

import com.github.engine.Game;
import com.github.exceptions.GameNotFoundException;
import com.github.entity.GameEntity;
import com.github.model.GameModel;
import com.github.repository.RedisGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GameService {
    public Game gameStorageDebug;

    private final RedisGameRepository redisGameRepository;

    @Autowired
    public GameService(RedisGameRepository redisGameRepository) {
        this.redisGameRepository = redisGameRepository;
    }

    public GameEntity createGame(GameModel gameModel){
        Game game = new Game();
        return redisGameRepository.save(new GameEntity(game, gameModel.getLobbyId().toString(), gameModel.getLobbyId(), gameModel.getPlayer1(), gameModel.getPlayer2()));
    }

    public Game getGame(String UUID) throws GameNotFoundException {
        if(UUID.isEmpty()){
            return null;
        }
        return Optional.of(redisGameRepository.findById(UUID).get()).map(GameEntity::getGame).orElseThrow(GameNotFoundException::new);
    }

    public Game createDebugGame(){
        Game game = new Game();
        gameStorageDebug = game;
        return game;
    }

    public Game createDebugGame(String fenString){
        Game game = new Game();
        gameStorageDebug = game;
        return game;
    }

    public Game getDebugGame(){
        return gameStorageDebug;
    }

    public void makeMove(){
        gameStorageDebug.makeMove();
    }
}
