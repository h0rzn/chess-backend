package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.IBoard;

import java.util.BitSet;
import java.util.List;

public class PawnMove implements IBoard {
    private final BitSet[] whitePieces;
    private final BitSet[] blackPieces;

    public PawnMove(Bitboard board) {
        this.whitePieces = board.getWhitePieces();
        this.blackPieces = board.getBlackPieces();
    }

    public List<Integer> generate(){
        return null;

    }
}
