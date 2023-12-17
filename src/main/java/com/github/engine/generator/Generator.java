package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.GameBoard;
import com.github.engine.SinglePiece;
import com.github.engine.move.Position;
import lombok.Getter;
import lombok.Setter;

// Generator class is the top level class
// of the move generation and acts as a
// distributor for the corresponding move generation
// class of the piece.
// The generator instance can be reused for the enemy,
// by updating the playerColor with setPlayerColor
// TODO implement logic to pass playerColor to generate for enemy player
public class Generator {
    private long[] boardWhite;
    private long[] boardBlack;
    private GameBoard gameBoard;
    @Getter
    @Setter
    private int playerColor;

    public Generator(int playerColor, GameBoard gameBoard){
        this.boardWhite = gameBoard.getSetWhite();
        this.boardBlack = gameBoard.getSetBlack();
        this.gameBoard = gameBoard;
        this.playerColor = playerColor;
    }

    // Distributes the Position information and color
    // to the respective Piece Move Generation which
    // is sourced by the pieceType given in Position
    public long generate(Position position){
        SinglePiece piece = SinglePiece.fromNumber(position.getPieceType());
        switch (piece){
            case Pawn -> {
                PawnMoveGenerator pawnMove = new PawnMoveGenerator(playerColor, gameBoard);
                return pawnMove.generate(position);
            }
            case Knight -> {
                KnightMoveGenerator knightMove = new KnightMoveGenerator(playerColor, gameBoard);
                return knightMove.generate(position);
            }
            case King -> {
                KingMoveGenerator kingMove = new KingMoveGenerator(playerColor, gameBoard);
                return kingMove.generate(position);
            }
            case Queen -> {
                QueenMoveGenerator queenMove = new QueenMoveGenerator(playerColor, gameBoard);
                return queenMove.generate(position);
            }
            case Bishop -> {
                BishopMoveGenerator bishopMove = new BishopMoveGenerator(playerColor, gameBoard);
                return bishopMove.generate(position);
            }
            case Rook -> {
                RookMoveGenerator rookMove = new RookMoveGenerator(playerColor, gameBoard);
                return rookMove.generate(position);
            }
        }
        return 0;
    }

    // move generation for each piece of each piece set of
    // given player
    // returns a long[6]: each long contains move generation
    // of each piece in that group combined
    public long[] generateAll() {
        long[] generated = new long[6];

        long[] playerPieces;
        if (playerColor == 0) {
            playerPieces = gameBoard.getSetWhite();
        } else {
            playerPieces = gameBoard.getSetBlack();
        }

        for (int pieceType = 0; pieceType < 6; pieceType++) {
            for (Integer square : Bitboard.bitscanMulti(playerPieces[pieceType])) {
                System.out.println(getPlayerColor() +" GEN ALL type "+pieceType+" square "+square);
                Position position = new Position(square, pieceType);
                generated[pieceType] |= generate(position);
            }
        }
        return generated;

    }
}
