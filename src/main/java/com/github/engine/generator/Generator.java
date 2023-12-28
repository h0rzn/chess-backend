package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.GameBoard;
import com.github.engine.SinglePiece;
import com.github.engine.interfaces.IGenerator;
import com.github.engine.move.Position;
import lombok.Getter;
import lombok.Setter;

// Generator class is the top level class
// of the move generation and acts as a
// distributor for the corresponding move generation
// class of the piece.
// The generator instance can be reused for the enemy,
// by updating the playerColor with setPlayerColor
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
    public long generate(Position position, boolean withCovers){
        //System.out.println(":::: GENERATE called color "+position.getColor()+" index "+position.getIndex()+ " :::");
        IGenerator pieceMoveGenerator = switch (SinglePiece.fromNumber(position.getPieceType())){
            case Pawn -> new PawnMoveGenerator(playerColor, gameBoard);
            case Knight -> new KnightMoveGenerator(playerColor, gameBoard);
            case King -> new KingMoveGenerator(playerColor, gameBoard);
            case Queen -> new QueenMoveGenerator(playerColor, gameBoard);
            case Bishop -> new BishopMoveGenerator(playerColor, gameBoard);
            case Rook -> new RookMoveGenerator(playerColor, gameBoard);
        };


        long generatedMoves = pieceMoveGenerator.generate(position);
        if (withCovers) {
            return generatedMoves;
        }

        //System.out.println("--- GENERATOR: without covers, move gen bef: "+generatedMoves);
        long[] playerPieces = getPlayerColor() == 0 ? gameBoard.getSetWhite() : gameBoard.getSetBlack();
        for (int playerPiece = 0; playerPiece < 6; playerPiece++) {
            if (position.getPieceType() == playerPiece || position.getPieceType() == 3) {
                continue;
            }
            generatedMoves &= ~playerPieces[playerPiece];
        }
        //System.out.println("--- GENERATOR: without covers, move gen aft: "+generatedMoves);
        return generatedMoves;
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
                Position position = new Position(square, pieceType);
                generated[pieceType] |= generate(position, false);
            }
        }
        return generated;

    }
}
