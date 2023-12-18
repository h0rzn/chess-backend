package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.Game;
import com.github.engine.GameBoard;
import com.github.engine.models.CheckInfo;
import com.github.engine.models.CheckResolveInfo;
import com.github.engine.move.Position;
import lombok.Setter;

import java.util.List;

// CheckValidator contains methods to inspect a potential
// check situation and check if the check is resolvable
public class CheckValidator {
    @Setter
    private GameBoard gameBoard;

    // validate if the given player is in a check situation
    // CheckInfo returns additional information
    public CheckInfo inCheck(int playerColor) {
        long[] playerPieces;
        long[] enemyPieces;
        int enemyColor;
        if (playerColor == 0) {
            playerPieces = gameBoard.getSetWhite();
            enemyPieces = gameBoard.getSetBlack();
            enemyColor = 1;
        } else {
            playerPieces = gameBoard.getSetBlack();
            enemyPieces = gameBoard.getSetWhite();
            enemyColor = 0;
        }

        long kingBoard = playerPieces[5];
        int kingSquare = Bitboard.bitscanSingle(kingBoard);

        if (kingSquare == -1) {
            // TODO case where king cannot be found
            return null;
        }

        // list of bitboards with move generation for each enemy piece that attack the players king
        long[] attackBoards = new long[6];
        // attackBoards[6] merged into a single bitboard
        long attackRoutes = 0;
        // bitboard marking all squares that enemy pieces can currently move to
        long enemyCovers = 0;

        // generate enemy moves
        Generator generator = new Generator(enemyColor, gameBoard);
        for (int enemyPiece = 0; enemyPiece < 6; enemyPiece++) {
            // skip empty piece boards
            if (enemyPieces[enemyPiece] == 0) {
                continue;
            }
            // get all occupied squares of that pieceType
            List<Integer> enemyPieceSquares = Bitboard.bitscanMulti(enemyPieces[enemyPiece]);

            // iterate over each square (=each Piece) and run move gen
            for (Integer occupiedSquare : enemyPieceSquares) {
                Position enemyPosition = new Position(occupiedSquare, enemyPiece);
                long enemyPieceMoves = generator.generate(enemyPosition);

                // if enemy player move generation includes the players king square
                // add that move generation to the combined attackRoutes and
                // the attackBoard of the respective enemy piece
                // if there is no match we still add the move gen to the enemy covers
                // the get a map of all enemy reachable squares
                if ((enemyPieceMoves & kingBoard) != 0) {
                    attackRoutes |= enemyPieceMoves;
                    attackBoards[enemyPiece] |= enemyPieceMoves;
                } else {
                    enemyCovers |= enemyPieceMoves;
                }
            }
        }

        // if any of the enemies attack routes match our kings position,
        // then the players king is in check
        boolean kingInCheck = (attackRoutes & kingBoard) != 0;

        // kingEscapes stores all valid moves for king that resolve
        // a **potential** check situation
        Position playerKingPosition = new Position(kingSquare);
        playerKingPosition.setPieceType(5);
        long kingMoves = generator.generate(playerKingPosition);
        kingMoves &= ~enemyPieces[5];

        long kingEscapes = (kingMoves & ~enemyCovers);

        return new CheckInfo(kingInCheck, kingEscapes,attackBoards, enemyCovers);
    }

    public CheckResolveInfo isCheckResolvable(int playerColor, long[] attackBoards) {
        long[] playerPieces;
        long[] enemyPieces;
        int enemyColor;
        if (playerColor == 0) {
            playerPieces = gameBoard.getSetWhite();
            enemyPieces = gameBoard.getSetBlack();
            enemyColor = 1;
        } else {
            playerPieces = gameBoard.getSetBlack();
            enemyPieces = gameBoard.getSetWhite();
            enemyColor = 0;
        }

        long[] attack2Defend = new long[6];
        long[] block2Defend = new long[6];
        boolean a2dResolvable = false;
        boolean b2dResolvable = false;

        Generator generator = new Generator(playerColor, gameBoard);
        //
        // ATTACK TO DEFEND
        // resolve chess by attacking threatening piece
        //
        /*
        for (int enemyPiece = 0; enemyPiece < 6; enemyPiece++) {
            // there are no attacks for this enemy piece
            // or the enemy piece is the king
            // TODO check if this actually works/is needed
            //if (attackBoards[enemyPiece] == 0 || enemyPiece == 5) {
            //    continue;
            //}

            // check if any of players pieces can directly attack enemy pieces that threaten
            // the players king
            List<Integer> enemyPieceSquares = Bitboard.bitscanMulti(enemyPieces[enemyPiece]);
            for (int playerPiece = 0; playerPiece < 6; playerPiece++) {
                List<Integer> playerPieceSquares = Bitboard.bitscanMulti(playerPieces[playerPiece]);

                for (int singlePlayerPieceSquare : playerPieceSquares) {
                    long playerAttackMoves = generator.generate(new Position(singlePlayerPieceSquare, playerPiece));
                    // for each occurrence of a attacker piece:
                    // check if player can attack
                    for (int enemyPieceSquare : enemyPieceSquares) {
                        if ((playerAttackMoves&(1L << enemyPieceSquare)) != 0) {
                            attack2Defend[playerPiece] |= (1L << enemyPieceSquare);
                        }
                    }
                }
            }

        }

        //
        // check resolvability with player move generation
        //
        /*
        Generator resolveMoves = new Generator(enemyColor, gameBoard);
        long[] playerMoves = generator.generateAll();

        for (int ePiece = 0; ePiece < 6; ePiece++) {
            //
            // ATTACK 2 DEFEND (a2d)
            //
            List<Integer> enemyPieceSquares = Bitboard.bitscanMulti(enemyPieces[enemyPiece]);


        }
         */


        int playerKingSquare = Bitboard.bitscanSingle(playerPieces[5]);
        long[] playerMoves = generator.generateAll();

        generator.setPlayerColor(enemyColor);
        for (int attackPiece = 0; attackPiece < 6; attackPiece++) {
            // filter out non-attacks
            if (attackBoards[attackPiece] == 0) {
                continue;
            }

            // check resolve actions for each player piece
            for (int playerPiece = 0; playerPiece < 6; playerPiece++) {
                //
                // ATTACK 2 DEFEND
                //
                List<Integer> pieceSquares = Bitboard.bitscanMulti(playerPieces[playerPiece]);
                for (Integer pieceSquare : pieceSquares) {
                    if ((playerMoves[playerPiece] & enemyPieces[attackPiece]) != 0) {
                        //System.out.println("attack piece "+attackPiece+" attackBoard "+attackBoards[attackPiece]+" player defender piece "+playerPiece+" (@"+pieceSquare+") with moves "+playerMoves[playerPiece]);
                        // store piece position in piece group board
                        // TODO check if we should store attacked enemy piece as well
                        attack2Defend[playerPiece] |= (1L << pieceSquare);
                    }
                }

                //
                // BLOCK 2 DEFEND
                //
                if (playerPiece == 5) {
                    continue;
                }
                // check if move generation routes of attacker and player piece match
                long routeMatchBoard = (playerMoves[playerPiece]&attackBoards[attackPiece]);
                if (routeMatchBoard != 0) {
                    System.out.println("B2D handling attacker "+attackPiece+" with player piece "+playerPiece);
                    System.out.println("--> attacker move gen "+attackBoards[attackPiece]);
                    List<Integer> routeMatches = Bitboard.bitscanMulti(routeMatchBoard);
                    System.out.println("--> route matches: "+routeMatches);

                    // long attackRoute = isolateCheckRoute();

                }
            }

        }

        for (int i = 0; i < 6; i++) {
            if (attack2Defend[i] != 0) {
                System.out.println("RESULT a2d "+i+" "+attack2Defend[i]);
            }
            if (block2Defend[i] != 0) {
                System.out.println("RESULT b2d "+i+" "+block2Defend[i]);
            }
        }

        /*
        eMovesCombined := combine(enemyMoves)
        attackToDefend[5] &^= eMovesCombined
        for _, a2d := range attackToDefend {
            if a2d != 0 {
                a2dResolvable = true
                break
            }
        }
         */


        // ...

        // get all moves for enemy pieces
        generator.setPlayerColor(enemyColor);
        long[] enemyMoves = generator.generateAll();


        System.out.println("ENEMY MOVES");
        for (int i = 0; i < 6; i++) {
            System.out.println(i+" "+enemyMoves[i]);
        }

        /*
        eMovesCombined := combine(enemyMoves)
        attackToDefend[5] &^= eMovesCombined
        for _, a2d := range attackToDefend {
            if a2d != 0 {
                a2dResolvable = true
                break
            }
        }
         */

        // drop a2d for king if move puts him in check



        //
        // BLOCK TO DEFEND
        // resolve chess by blocking attack path
        //
        //int playerKingSquare = Bitboard.bitscanSingle(playerPieces[5]);
        //int playerKingColumn = playerKingSquare % 8;



        return null;
    }

    // extract the direct route between threatening enemy piece
    // and player king
    private long isolateCheckRoute(int playerColor, List<Integer> matches, Position attacker, int kingSquare) {
        int playerKingColumn = kingSquare % 8;

        for (int match : matches) {

            // Same column
            if (playerKingColumn == match % 8) {

            }


        }

        return 0;
    }

    public CheckValidator(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }
}
