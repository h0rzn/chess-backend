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
    private String name;
    @Getter
    private String description;
    @Getter
    private final long[] setWhite;
    @Getter
    private final long[] setBlack;
    @Getter
    private int activeColor;
    @Getter
    private int halfMoveClock;
    @Getter
    private int fullMoveClock;
    @Setter
    private boolean logsEnabled;

    private int pieceByChar(char pieceChar) {
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

    // get piece color by uppercase (white)
    // or lowercase (black)
    private int getColorForPiece(char pieceChar) {
        return Character.isUpperCase(pieceChar) ? 0 : 1;
    }

    // parse placement group string
    // starts on square index 0 (LL) and splits rows by '/'
    // '8' -> empty row
    // 'P7' -> white pawn on left most square, followed by 7 empty squares in that row
    // '7P' -> 7 empty squares followed by white pawn
    // can be combined like: Q6n
    private List<long[]> parsePlacements(String group) {
        ArrayList<long[]> pieceSets = new ArrayList<>();
        String[] placements = group.split("/");

        int cursor = 0;
        for (int row = placements.length-1; row >= 0; row--) {
            log("=== ROW raw " + row + ": " + placements[row]);
            if (placements[row].equals("8")) {
                log("--> empty row" + " cursor: " +cursor+ " -> "+(cursor+8));
                cursor += 8;
            } else {
                // TODO throw exception if row malformed: not 8 or not all squares covered by FEN
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

    private int parseMoveClock(String clockGroup) throws Exception {
        try {
            int clock = Integer.parseInt(clockGroup);
            // Ignore 50-Rule for now
            if (clock >= 0) {
                return clock;
            }
            throw new Exception("move clock parsing: must be >= 0, have: "+clock);
        } catch (NumberFormatException exception) {
            throw new Exception("move clock parsing: failed to parse int: " + exception);
        }
    }

    public void parse(String fenToken) throws Exception {
        //log(fenToken);
        String[] groups = fenToken.split(" ");
        if (groups.length != 6) {
            throw new Exception("token does not have required 6 groups, have: "+groups.length);
        }

        if (logsEnabled) {
            for (String group : groups) {
                System.out.println("GROUP " + group);
            }
        }

        // G0 Parse placements
        parsePlacements(groups[0]);

        // G1 Parse active color
        String activeColorGroup = groups[1];
        if (activeColorGroup.equals("w")) {
            this.activeColor = 0;
        } else if (activeColorGroup.equals("b")) {
            this.activeColor = 1;
        } else {
            throw new Exception("active color group: unkown color "+activeColorGroup);
        }

        // TODO add parsing for group 2 & 3
        // G2 Castling availability
        // G3 En passant target square

        // Move clock parsing throws NumberFormatException
        // G4 Half Move Clock
        int halfMoveClock = parseMoveClock(groups[4]);
        if (halfMoveClock > -1) {
            this.halfMoveClock = halfMoveClock;
        } else {
            throw new Exception("half move clock group: must be >= 0, have: "+halfMoveClock);
        }
        // G5 Full Move Clock
        int fullMoveClock = parseMoveClock(groups[5]);
        if (fullMoveClock > -1) {
            this.fullMoveClock = fullMoveClock;
        } else {
            throw new Exception("full move clock group: must be >= 0, have: "+fullMoveClock);
        }
    }

    public void log(String line) {
        if (logsEnabled) {
            System.out.println(line);
        }
    }
    
    public FenParser() {
        this.setWhite = new long[6];
        this.setBlack = new long[6];
        this.activeColor = -1;
        this.halfMoveClock = -1;
        this.fullMoveClock = -1;
    }

    // Constructor to enable logs
    public FenParser(boolean logsEnabled) {
        this();
        this.logsEnabled = true;
    }
}
