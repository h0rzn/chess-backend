package com.github.engine.utils;
import com.github.engine.Game;
import com.github.engine.move.Move;


public class FenSerializer {
    // serialize the full board with positions and active color
    public static String serialize(Game game) {
        // counter for empty squares in a row
        int empty = 0;

        StringBuilder fen = new StringBuilder();
        // index for current cursor: starts top left of board (= index 56)
        int curIdx = 56;
        while (curIdx > -8) {
            // tokenize the square the cursor points to
            String squareToken = FenSerializer.tokenizeSquare(game, curIdx);
            if (squareToken.equals("?")) {
                // empty square
                empty++;
            } else {
                if (empty > 0) {
                    // square not empty so prefix number for empty squares and reset counter
                    fen.append(empty);
                    empty = 0;
                }
                fen.append(squareToken);
            }


            // preparation for next row
            if (curIdx % 8 == 7) {
                curIdx -= 15; // wrap to first square of next line
                if (empty > 0) {
                    fen.append(empty);
                    empty = 0;
                }
                // skip row delimiter for last row
                if (curIdx != -8) {
                    fen.append("/");
                }
            } else {
                curIdx++; // push cursor on square to the right (++direction)
            }
        }

        // add other groups
        String colorToken = game.getActiveColor() == 0 ? "w" : "b";
        fen.append(" ").append(colorToken);
        // placeholders
        fen.append(" - - 1 1");

        return fen.toString();
    }

    public static String serializeUpdate(Game game, Move move) {
        int startFrom = move.getFrom().getIndex() / 8;
        int startTo = move.getTo().getIndex() / 8;

        int c = 8;
        int emptyFrom = 0;
        int emptyTo = 0;
        int idxFrom = move.getFrom().getIndex() - (move.getFrom().getIndex() % 8);
        int idxTo = move.getTo().getIndex() - (move.getTo().getIndex() % 8);
        StringBuilder fromRow = new StringBuilder();
        StringBuilder toRow = new StringBuilder();
        do {
            String pieceTokenFrom = FenSerializer.tokenizeSquare(game, idxFrom);
            if (pieceTokenFrom.equals("?")) {
                // empty square
                emptyFrom++;
            } else {
                if (emptyFrom> 0) {
                    // square not empty so prefix number for empty squares and reset counter
                    fromRow.append(emptyFrom);
                    emptyFrom = 0;
                }
                fromRow.append(pieceTokenFrom);
            }

            String pieceTokenTo = FenSerializer.tokenizeSquare(game, idxTo);
            if (pieceTokenTo.equals("?")) {
                // empty square
                emptyTo++;
            } else {
                if (emptyTo> 0) {
                    // square not empty so prefix number for empty squares and reset counter
                    toRow.append(emptyTo);
                    emptyTo = 0;
                }
                toRow.append(pieceTokenTo);
            }


            c--;
            idxFrom++;
            idxTo++;
        } while (c > 0);

        if (emptyFrom > 0) {
            fromRow.append(emptyFrom);
        }
        if (emptyTo > 0) {
            toRow.append(emptyTo);
        }

        String[] lastFenGroups = game.getLastMoveFen().split(" ", 2);
        String[] fenRows = lastFenGroups[0].split("/");

        fenRows[( 7 -startFrom )] = fromRow.toString();
        fenRows[( 7 -startTo )] = toRow.toString();
        lastFenGroups[0] = String.join("/", fenRows);

        return String.join(" ", lastFenGroups);
    }

    public static String tokenizeSquare(Game game, int square) {
        long cursor = (1L << square);

        int piece = -1;
        // search for piece type occupying this square
        for (int p = 0; p < 6; p++) {
            if ((game.getSetWhite()[p]&cursor) != 0) {
                piece = p;
                break;
            }
            if ((game.getSetBlack()[p]&cursor) != 0) {
                piece = p+6;
                break;
            }
        }

        return tokenByPieceType(piece);
    }

    public static String tokenByPieceType(int pieceType) {
        String pieceToken = switch (pieceType) {
            case 0 -> "P";
            case 1 -> "N";
            case 2 -> "B";
            case 3 -> "R";
            case 4 -> "Q";
            case 5 -> "K";
            case 6 -> "p";
            case 7 -> "n";
            case 8 -> "b";
            case 9 -> "r";
            case 10 -> "q";
            case 11 -> "k";
            default -> "?";
        };
        return pieceToken;
    }
}
