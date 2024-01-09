package com.github.services;

import com.github.engine.Game;
import com.github.engine.MoveAction;
import com.github.engine.models.MoveInfo;
import com.github.exceptions.GameNotFoundException;
import com.github.entity.GameEntity;
import com.github.model.GameModel;
import com.github.model.GameMoveModel;
import com.github.model.debug.DebugMoveModel;
import com.github.model.ResponseModelRecord;
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

/**
 * Service for managing games
 */
@Service
public class GameService {

    @Getter
    public Game gameDebug;

    private final RedisGameRepository redisGameRepository;
    private final HistoryService historyService;

    /**
     * Constructor, params get autowired (injected) by Spring
     * @param redisGameRepository
     * @param historyService
     */
    @Autowired
    public GameService(RedisGameRepository redisGameRepository, HistoryService historyService) {
        this.redisGameRepository = redisGameRepository;
        this.historyService = historyService;
    }

    /**
     * Creates a new game and saves it to the database
     * @param gameModel
     * @return
     * @throws Exception
     */
    public GameEntity createGame(GameModel gameModel) throws Exception {
        Game game = new Game();
        Random random = new SecureRandom();

        //Loads standard board configuration
        game.loadFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

        UUID whitePlayerId;
        UUID blackPlayerId;

        //Randomly assigns white and black player
        if (random.nextBoolean()) {
            whitePlayerId = gameModel.getPlayer1();
            blackPlayerId = gameModel.getPlayer2();
        } else {
            whitePlayerId = gameModel.getPlayer2();
            blackPlayerId = gameModel.getPlayer1();
        }

        //Creates a new chess clock with 5 minutes for each player
        ChessClock chessClock = new ChessClock(300000);
        chessClock.startClock(0);

        //Saves the game to the database
        GameEntity gameEntity = redisGameRepository.save(new GameEntity(game,
                                gameModel.getLobbyId().toString(),
                                gameModel.getLobbyId(),
                                gameModel.getPlayer1(),
                                gameModel.getPlayer2(),
                                whitePlayerId,
                                blackPlayerId,
                                chessClock));
        return gameEntity;
    }

    /**
     * Returns a game by id wrapped in an optional
     */
    public Optional<GameEntity> getGameOptional(String id) {
        return redisGameRepository.findById(id);

    }

    /**
     * Returns a game by id
     */
    public GameEntity getGameEntity(String id){
        return redisGameRepository.findById(id).get();
    }

    /**
     * Accepts a move and executes it on the game
     * @param moveModel
     */
    public ResponseModelRecord makeGameMove(GameMoveModel moveModel) throws GameNotFoundException {

        //Gets the game from the database
        GameEntity gameEntity = getGameEntity(moveModel.getGameId());
        Game game = gameEntity.getGame();

        //Compares the player id to the white and black player id to determine the color of the player
        String playerID = moveModel.getPlayerId();
        int playerColor = Objects.equals(playerID, gameEntity.getWhitePlayerId().toString()) ? 0 : 1;

        //Creates a move action from the move model
        MoveAction moveAction = new MoveAction(moveModel.getMove(), moveModel.getPromoteTo());

        //Checks if the player is allowed to make a move
        if(playerColor != game.getActiveColor()){
            MoveInfo result = game.execute(moveAction);
            result.setLegal(false);
            return new ResponseModelRecord(result, 0, 0);
        }

        //Executes the move and saves the game to the database
        MoveInfo result = game.execute(moveAction);
        if(result.isLegal()){
            gameEntity.getChessClock().setActivePlayer(gameEntity.getChessClock().getActivePlayer() == 0 ? 1 : 0);
        }
        redisGameRepository.save(gameEntity);
        return new ResponseModelRecord(result, gameEntity.getChessClock().getWhiteTimeLeft(), gameEntity.getChessClock().getBlackTimeLeft());
    }

    /**
     * Creates a new debug game
     */
    public Game createDebugGame(){
        Game game = new Game();
        this.gameDebug = game;
        return game;
    }

    /**
     * Creates a new debug game with a given fen string
     */
    public Game createDebugGame(String fenString) throws Exception {
        Game game = new Game(fenString);
        System.out.println("[GAMESERVICE] created new debug game with fen: "+fenString);

        this.gameDebug = game;
        return this.gameDebug;
    }

    /**
     * Loads a position into the debug game
     */
    public Game loadPosition(String fenString) throws Exception {
        if(gameDebug != null){
            gameDebug.load(fenString);
            return gameDebug;
        }
        return null;
    }

    public MoveInfo makeDebugMove(DebugMoveModel moveModel) {
        MoveAction moveAction = new MoveAction(moveModel.getMove(), moveModel.getPromoteTo());
        return gameDebug.execute(moveAction);
    }

}
