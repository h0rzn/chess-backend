package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.interfaces.IBoard;
import com.github.engine.SinglePiece;
import com.github.engine.move.Move;
import com.github.engine.move.Position;

import java.util.List;

public class Generator implements IBoard {
    private long[] boardWhite;
    private long[] boardBlack;
    private Bitboard bitboard;

    public Generator(Bitboard bitboard){
        this.bitboard = bitboard;
        this.boardWhite = bitboard.getBoardWhite();
        this.boardBlack = bitboard.getBoardBlack();
    }

    // Generates all possible moves for a given piece
    public long generate(Position position, int color){
        SinglePiece piece = SinglePiece.fromNumber(position.getPieceType());
        switch (piece){
            case Pawn -> {
                PawnMoveGenerator pawnMove = new PawnMoveGenerator(bitboard);
                return pawnMove.generate(color, position);
            }
            case Knight -> {
                KnightMoveGenerator knightMove = new KnightMoveGenerator(bitboard);
                return knightMove.generate(color, position);
            }
            case King -> {
                KingMoveGenerator kingMove = new KingMoveGenerator(bitboard);
                return kingMove.generate(color, position);
            }
            case Queen -> {
                QueenMoveGenerator queenMove = new QueenMoveGenerator(bitboard);
                return queenMove.generate(color, position);
            }
            case Bishop -> {
                BishopMoveGenerator bishopMove = new BishopMoveGenerator(bitboard);
                return bishopMove.generate(color, position);
            }
            case Rook -> {
                RookMoveGenerator rookMove = new RookMoveGenerator(bitboard);
                return rookMove.generate(color, position);
            }
        }
        return 0;
    }
}
