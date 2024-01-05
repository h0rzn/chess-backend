package com.github.services;

import com.github.engine.Game;
import com.github.engine.MoveAction;
import com.github.engine.models.MoveInfo;
import com.github.engine.move.Move;
import com.github.entity.LobbyEntity;
import com.github.exceptions.GameNotFoundException;
import com.github.entity.GameEntity;
import com.github.model.GameModel;
import com.github.model.debug.GameMoveModel;
import com.github.model.debug.MoveDebugModel;
import com.github.repository.RedisGameRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameService {
    @Getter
    public Game game;

    private final RedisGameRepository redisGameRepository;

    @Autowired
    public GameService(RedisGameRepository redisGameRepository) {
        this.redisGameRepository = redisGameRepository;
    }

    public GameEntity createGame(GameModel gameModel) throws Exception {
        Game game = new Game();
        game.loadFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        return redisGameRepository.save(new GameEntity(game, gameModel.getLobbyId().toString(), gameModel.getLobbyId(), gameModel.getPlayer1(), gameModel.getPlayer2()));
    }

    public Optional<GameEntity> getGameOptional(String id) {
        return redisGameRepository.findById(id);

    }

    public GameEntity getGameEntity(String id){
        return redisGameRepository.findById(id).get();
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
        this.game = game;
        return game;
    }

    public Game createDebugGame(String fenString) throws Exception {
        Game game = new Game(fenString);
        System.out.println("[GAMESERVICE] created new debug game with fen: "+fenString);

        this.game = game;
        return this.game;
    }

    public Game loadPosition(String fenString) throws Exception {
        if(game != null){
            game.load(fenString);
            return game;
        }
        return null;
    }

    public Game getDebugGame(){
        return game;
    }

    public MoveInfo makeMove(MoveDebugModel moveModel) {
        MoveAction moveAction = new MoveAction(moveModel.getMove(), moveModel.getPromoteTo());
        return game.execute(moveAction);
    }

    public MoveInfo makeGameMove(GameMoveModel moveModel) throws GameNotFoundException {
        GameEntity gameEntity = getGameEntity(moveModel.getGameId());
        Game game1 = gameEntity.getGame();
        System.out.println("Active Color: " + game1.getActiveColor());
        MoveAction moveAction = new MoveAction(moveModel.getMove(), moveModel.getPromoteTo());
        MoveInfo result = game1.execute(moveAction);
        redisGameRepository.save(gameEntity);
        return result;
    }

}
