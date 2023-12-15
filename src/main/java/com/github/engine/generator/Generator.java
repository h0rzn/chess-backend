package com.github.engine.generator;

import com.github.engine.GameBoard;
import com.github.engine.SinglePiece;
import com.github.engine.move.Position;

// Generator class is the top level class
// of the move generation and acts as a
// distributor for the corresponding move generation
// class of the piece
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
}
