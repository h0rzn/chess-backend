package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.GameBoard;
import com.github.engine.SinglePiece;
import com.github.engine.move.Position;

import java.util.ArrayList;
import java.util.List;

// Generator class is the top level class
// of the move generation and acts as a
// distributor for the corresponding move generation
// class of the piece
// TODO implement logic to pass playerColor to generate for enemy player
public class Generator {
    private long[] boardWhite;
    private long[] boardBlack;
    private GameBoard gameBoard;

    public Generator(GameBoard gameBoard){
        this.gameBoard = gameBoard;
        this.boardWhite = gameBoard.getSetWhite();
        this.boardBlack = gameBoard.getSetBlack();
    }

    // Distributes the Position information and color
    // to the respective Piece Move Generation which
    // is sourced by the pieceType given in Position
    public long generate(Position position, int color){
        SinglePiece piece = SinglePiece.fromNumber(position.getPieceType());
        switch (piece){
            case Pawn -> {
                PawnMoveGenerator pawnMove = new PawnMoveGenerator(gameBoard);
                return pawnMove.generate(color, position);
            }
            case Knight -> {
                KnightMoveGenerator knightMove = new KnightMoveGenerator(gameBoard);
                return knightMove.generate(color, position);
            }
            case King -> {
                KingMoveGenerator kingMove = new KingMoveGenerator(gameBoard);
                return kingMove.generate(color, position);
            }
            case Queen -> {
                QueenMoveGenerator queenMove = new QueenMoveGenerator(gameBoard);
                return queenMove.generate(color, position);
            }
            case Bishop -> {
                BishopMoveGenerator bishopMove = new BishopMoveGenerator(gameBoard);
                return bishopMove.generate(color, position);
            }
            case Rook -> {
                RookMoveGenerator rookMove = new RookMoveGenerator(gameBoard);
                return rookMove.generate(color, position);
            }
        }
        return 0;
    }

    // move generation for each piece of each piece set of
    // given player
    // returns a long[6]: each long contains move generation
    // of each piece in that group combined
    public long[] generateAll(int playerColor) {
        long[] generated = new long[6];

        long[] merged = GameBoard.mergePlayerBoards(playerColor, gameBoard.getSetWhite(), gameBoard.getSetBlack());
        long mergedWhite = merged[0];
        long mergedBlack = merged[1];

        long[] playerPieces;
        if (playerColor == 0) {
            playerPieces = gameBoard.getSetWhite();
        } else {
            playerPieces = gameBoard.getSetBlack();
        }

        for (int pieceType = 0; pieceType < 6; pieceType++) {
            for (Integer square : Bitboard.bitscanMulti(playerPieces[pieceType])) {
                Position position = new Position(square, pieceType);
                generated[pieceType] |= generate(position, playerColor);
            }
        }
        return generated;

    }
}
