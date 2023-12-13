package com.github.engine.interfaces;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface IBoard {

    // Bitboards for the border ranks of a chessboard, used for knight move generation
    long NOT_A_FILE = 0xfefefefefefefefeL; // 1111111011111110111111101111111011111110111111101111111011111110
    long NOT_H_FILE = 0x7f7f7f7f7f7f7f7fL; // 0111111101111111011111110111111101111111011111110111111101111111
    long NOT_AB_FILE = 0xfcfcfcfcfcfcfcfcL; // 1111110011111100111111001111110011111100111111001111110011111100
    long NOT_GH_FILE = 0x3f3f3f3f3f3f3f3fL; // 0011111100111111001111110011111100111111001111110011111100111111

    // Merges whitePieces and blackPieces respectively
    // decision on what is player and enemy is based on playerColor
    // returns [playerPiecesMerged, enemyPiecesMerged]
    default long[] mergePlayerBoards(int playerColor, long[] whitePieces, long[] blackPieces) {
        long[] mergedBoards = new long[2];
        long boardWhitePieces = (whitePieces[0] | whitePieces[1] | whitePieces[2] | whitePieces[3] | whitePieces[4] | whitePieces[5]);
        long boardBlackPieces = (blackPieces[0] | blackPieces[1] | blackPieces[2] | blackPieces[3] | blackPieces[4] | blackPieces[5]);
        mergedBoards[0] = (playerColor == 0) ? boardWhitePieces : boardBlackPieces;
        mergedBoards[1] = (playerColor == 0) ? boardBlackPieces : boardWhitePieces;

        return mergedBoards;
    }

}
