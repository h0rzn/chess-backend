package com.github.engine.interfaces;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface IBoard {

    // Bitboards for the border ranks of a chessboard, used for knight move generation
    long NOT_A_FILE = 0xfefefefefefefefeL; // 1111111011111110111111101111111011111110111111101111111011111110
    long NOT_H_FILE = 0x7f7f7f7f7f7f7f7fL; // 0111111101111111011111110111111101111111011111110111111101111111
    long NOT_AB_FILE = 0xfcfcfcfcfcfcfcfcL; // 1111110011111100111111001111110011111100111111001111110011111100
    long NOT_GH_FILE = 0x3f3f3f3f3f3f3f3fL; // 0011111100111111001111110011111100111111001111110011111100111111

}
