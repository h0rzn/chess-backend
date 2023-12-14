package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.models.CheckInfo;
import com.github.engine.models.CheckResolveInfo;
import com.github.engine.move.Position;
import lombok.Setter;

import java.util.List;

public class CheckValidator {
    @Setter
    private Bitboard bitboard;
    public CheckInfo inCheck(int playerColor) {
        long[] playerPieces;
        long[] enemyPieces;
        if (playerColor == 0) {
            playerPieces = bitboard.getBoardWhite();
            enemyPieces = bitboard.getBoardBlack();
        } else {
            playerPieces = bitboard.getBoardBlack();
            enemyPieces = bitboard.getBoardWhite();
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
        Generator enemyGenerator = new Generator(bitboard);
        for (int enemyPiece = 0; enemyPiece < 6; enemyPiece++) {
            // get all occupied squares of that pieceType
            List<Integer> enemyPieceSquares = Bitboard.bitscanMulti(enemyPieces[enemyPiece]);

            // iterate over each square (=each Piece) and run move gen
            for (Integer occupiedSquare : enemyPieceSquares) {
                Position enemyPosition = new Position(occupiedSquare);
                long enemyPieceMoves = enemyGenerator.generate(enemyPosition, playerColor);

                if ((enemyPieceMoves & kingSquare) != 0) {
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
        long kingMoves = enemyGenerator.generate(playerKingPosition, playerColor);
        kingMoves &= ~enemyPieces[5];

        long kingEscapes = (kingMoves & ~enemyCovers);

        return new CheckInfo(kingInCheck, kingEscapes,attackBoards, enemyCovers);
    }

    public CheckResolveInfo isCheckResolvable(int playerColor, long[] attackBoards) {
        long[] playerPieces;
        long[] enemyPieces;
        if (playerColor == 0) {
            playerPieces = bitboard.getBoardWhite();
            enemyPieces = bitboard.getBoardBlack();
        } else {
            playerPieces = bitboard.getBoardBlack();
            enemyPieces = bitboard.getBoardWhite();
        }

        long[] attack2Defend = new long[6];
        long[] block2Defend = new long[6];
        boolean a2dResolvable = false;
        boolean b2dResolvable = false;

        Generator generator = new Generator(bitboard);
        //
        // ATTACK TO DEFEND
        // resolve chess by attacking threatening piece
        //
        for (int enemyPiece = 0; enemyPiece < 6; enemyPiece++) {
            // there are no attacks for this enemy piece
            // or the enemy piece is the king
            // TODO check if this actually works
            //if (attackBoards[enemyPiece] == 0 || enemyPiece == 5) {
            //    continue;
            //}

            // TODO we should bitscan this board and do the following steps for each piece occurrences.
            long enemyPieceBoard = enemyPieces[enemyPiece];
            for (int playerPiece = 0; playerPiece < 6; playerPiece++) {
                List<Integer> playerPieceSquares = Bitboard.bitscanMulti(playerPieces[playerPiece]);

                for (int singlePlayerPieceSquare : playerPieceSquares) {
                    long playerAttackMoves = generator.generate(new Position(singlePlayerPieceSquare), playerColor);

                    if ((playerAttackMoves&enemyPieceBoard) != 0) {
                        attack2Defend[playerPiece] |= enemyPieceBoard;
                    }
                }


            }

        }





        return null;
    }

    public CheckValidator(Bitboard bitboard) {
        this.bitboard = bitboard;
    }
}
