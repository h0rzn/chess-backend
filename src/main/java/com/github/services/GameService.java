package com.github.services;

import com.github.Chess;
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
import com.github.model.debug.ResponseModelRecord;
import com.github.repository.HistoryRepository;
import com.github.repository.RedisGameRepository;
import com.github.utils.ChessClock;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class GameService {
    @Getter
    public Game game;

    private final RedisGameRepository redisGameRepository;
    private final HistoryService historyService;

    @Autowired
    public GameService(RedisGameRepository redisGameRepository, HistoryService historyService) {
        this.redisGameRepository = redisGameRepository;
        this.historyService = historyService;
    }

    public GameEntity createGame(GameModel gameModel) throws Exception {
        Game game = new Game();
        game.loadFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

        Random random = new SecureRandom();
        UUID whitePlayerId;
        UUID blackPlayerId;
        if (random.nextBoolean()) {
            whitePlayerId = gameModel.getPlayer1();
            blackPlayerId = gameModel.getPlayer2();
        } else {
            whitePlayerId = gameModel.getPlayer2();
            blackPlayerId = gameModel.getPlayer1();
        }

        ChessClock chessClock = new ChessClock(300000);
        chessClock.startClock(0);
        GameEntity gameEntity = redisGameRepository.save(new GameEntity(game,
                gameModel.getLobbyId().toString(),
                gameModel.getLobbyId(),
                gameModel.getPlayer1(),
                gameModel.getPlayer2(),
                whitePlayerId,
                blackPlayerId,
                chessClock));

        historyService.createHistory(gameEntity.getId());
        return gameEntity;
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

    public ResponseModelRecord makeGameMove(GameMoveModel moveModel) throws GameNotFoundException {
        GameEntity gameEntity = getGameEntity(moveModel.getGameId());
        Game game1 = gameEntity.getGame();

        String playerID = moveModel.getPlayerId();
        int playerColor = Objects.equals(playerID, gameEntity.getWhitePlayerId().toString()) ? 0 : 1;
        System.out.println("PlayerID: " + playerID);
        System.out.println("PlayerID gameentity: " + gameEntity.getWhitePlayerId());
        System.out.println("Player has color: " + playerColor);

        System.out.println("Active Color: " + game1.getActiveColor());
        MoveAction moveAction = new MoveAction(moveModel.getMove(), moveModel.getPromoteTo());

        if(playerColor != game1.getActiveColor()){
            MoveInfo result = game1.execute(moveAction);
            result.setLegal(false);
            return new ResponseModelRecord(result, 0, 0);
        }

        MoveInfo result = game1.execute(moveAction);
        if(result.isLegal()){
            historyService.addMoveToHistory(gameEntity.getId(), result);
            gameEntity.getChessClock().setActivePlayer(gameEntity.getChessClock().getActivePlayer() == 0 ? 1 : 0);
        }
        redisGameRepository.save(gameEntity);
        return new ResponseModelRecord(result, gameEntity.getChessClock().getWhiteTimeLeft(), gameEntity.getChessClock().getBlackTimeLeft());
    }

}
