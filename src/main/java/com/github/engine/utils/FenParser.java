package com.github.engine.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// FenParser parses input in the Forsyth-Edwards-Notation
// to source board scenarios from external input
// 6 Groups separated by white space
// G1: Piece placement :: TL -> BR, WHITE & black
// G2: Active Color :: w | b
// G3: Castling availability :: - |
// G4: En passant target square
// G5: Halfmove clock
// G6: Fullmove number
//
// Example FEN for initial board
// rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
public class FenParser {
    @Getter
    private long[] setWhite;
    @Getter
    private long[] setBlack;
    
    @Setter
    private boolean logsEnabled;

    public int pieceByChar(char pieceChar) {
        int piece = switch (pieceChar) {
            case 'p', 'P' -> 0;
            case 'n', 'N' -> 1;
            case 'b', 'B' -> 2;
            case 'r', 'R' -> 3;
            case 'q', 'Q' -> 4;
            case 'k', 'K' -> 5;
            default -> -1;
        };
        return piece;
    }

    public int getColorForPiece(char pieceChar) {
        return Character.isUpperCase(pieceChar) ? 0 : 1;
    }
    public List<long[]> parsePlacements(String group) {
        ArrayList<long[]> pieceSets = new ArrayList<>();
        String[] placements = group.split("/");

        int cursor = 0;
        for (int row = placements.length-1; row >= 0; row--) {
            log("=== ROW raw " + row + ": " + placements[row]);
            if (placements[row].equals("8")) {
                log("--> empty row" + " cursor: " +cursor+ " -> "+(cursor+8));
                cursor += 8;
            } else {
                for (char pieceChar : placements[row].toCharArray()) {
                    int charNum = Character.getNumericValue(pieceChar);
                    if (charNum <= 8) {
                        log("   [NUM] " + charNum + " cursor: " +cursor+ " -> "+(cursor+charNum));
                        cursor += charNum;
                    } else {
                        log("   [CHA] " + pieceChar);
                        log("   ---> [CHA] write " + pieceChar + " @ " + cursor);

                        int pieceType = pieceByChar(pieceChar);
                        if (getColorForPiece(pieceChar) == 0) {
                            setWhite[pieceType] |= (1L << cursor);
                        } else {
                            setBlack[pieceType] |= (1L << cursor);
                        }
                        cursor++;
                    }
                }
            }
        }

        // log("\nWHITE:: "+setWhite[0]);
        // log("BLACK:: "+setBlack[0]);
        return pieceSets;
    }

    public void parse(String fenToken) throws Exception {
        //log(fenToken);
        String[] groups = fenToken.split(" ");
        if (groups.length != 6) {
            throw new Exception("token does not have required 6 groups, has: "+groups.length);
        }
        /*
        for (String group : groups) {
            log(group);
        }
         */

        parsePlacements(groups[0]);
    }

    public void log(String line) {
        if (logsEnabled) {
            System.out.println(line);
        }
    }
    
    public FenParser() {
        this.setWhite = new long[6];
        this.setBlack = new long[6];
        this.logsEnabled = true;
    }
}
