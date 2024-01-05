package com.github.services;

import com.github.engine.Game;
import com.github.engine.MoveAction;
import com.github.engine.models.MoveInfo;
import com.github.engine.move.Move;
import com.github.entity.LobbyEntity;
import com.github.exceptions.GameNotFoundException;
import com.github.entity.GameEntity;
import com.github.model.GameModel;
import com.github.repository.RedisGameRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GameService {
    @Getter
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

    public Optional<GameEntity> getGameOptional(String id) {
        return redisGameRepository.findById(id);

    }

    public Game getGame(String id) throws GameNotFoundException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        return Optional.ofNullable(redisGameRepository.findById(id))
                .map(gameEntity -> gameEntity.get().getGame())
                .orElseThrow(GameNotFoundException::new);
    }



    public Game createDebugGame(){
        Game game = new Game();
        gameStorageDebug = game;
        return game;
    }

    public Game createDebugGame(String fenString) throws Exception {
        Game game = new Game(fenString);
        System.out.println("[GAMESERVICE] created new debug game with fen: "+fenString);

        gameStorageDebug = game;
        return gameStorageDebug;
    }

    public Game loadPosition(String fenString) throws Exception {
        if(gameStorageDebug != null){
            gameStorageDebug.load(fenString);
            return gameStorageDebug;
        }
        return null;
    }

    public Game getDebugGame(){
        return gameStorageDebug;
    }

    public MoveInfo makeMove(Move move){
        MoveAction moveAction = new MoveAction(move);
        return gameStorageDebug.execute(moveAction);
    }

    public MoveInfo makeMove(String move, Integer promoteTo){
        MoveAction moveAction = new MoveAction(move, promoteTo);
        return gameStorageDebug.execute(moveAction);
    }
}
