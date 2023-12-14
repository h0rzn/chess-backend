package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.move.Position;
import lombok.Setter;

import java.util.List;

public class CheckValidator {
    @Setter
    private Bitboard bitboard;
    public boolean inCheck() {
        long[] playerPieces;
        long[] enemyPieces;
        if (bitboard.getColorToMove() == 0) {
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
            return false;
        }

        long[] mergedBoards = Bitboard.mergePlayerBoards(bitboard.getColorToMove(), playerPieces, enemyPieces);
        long mergedPlayerPieces = mergedBoards[0];
        long mergedEnemyPieces = mergedBoards[1];


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

            for (Integer occupiedSquare : enemyPieceSquares) {
                Position enemyPosition = new Position(occupiedSquare);
                long enemyPieceMoves = enemyGenerator.generate(enemyPosition, bitboard.getColorToMove());

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
        long kingMoves = enemyGenerator.generate(playerKingPosition, bitboard.getColorToMove());
        long kingEscapes = (kingMoves & ~enemyCovers);




        // TODO:
        // also return later:
        // - king escapes
        // - attackBoards[6]
        // - enemyCovers
        return kingInCheck;
    }

    public CheckValidator(Bitboard bitboard) {
        this.bitboard = bitboard;
    }
}
