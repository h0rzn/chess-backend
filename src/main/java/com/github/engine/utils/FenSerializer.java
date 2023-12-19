package com.github.engine.utils;

import com.github.engine.Game;
import com.github.engine.move.Move;

import java.util.ArrayList;
import java.util.List;

public class FenSerializer {

    private Game game;

    // serializes relevant rows from move and returns
    // list of serialized rows [fromRow, toRow]
    private List<String> serializeUpdated(Move move) {
        int from = move.getFrom().getIndex();
        int to = move.getTo().getIndex();
        // get first index in that row
        int fromStart = from - (from % 8);
        int toStart = to - (to % 8);

        long[] setWhite = game.getSetWhite();
        long[] setBlack = game.getSetBlack();

        StringBuilder fenFrom = new StringBuilder();
        StringBuilder fenTo = new StringBuilder();

        //System.out.println("[serialize updated] from "+from+" to "+to+ " fromStart "+fromStart+" toStart "+toStart);

        long cursorFrom = (1L << fromStart);
        long cursorTo = (1L << toStart);
        int emptyFrom = 0;
        int emptyTo = 0;
        for (int i = 0; i < 8; i++) {
            // System.out.println("[[[ from "+cursorFrom+" to "+cursorTo);
            int pieceFrom = -1;
            int pieceTo = -1;

            boolean fromFound = false;
            boolean toFound = false;

            // iterative over each piece type
            for (int p = 0; p < 6; p++) {
                //
                // FROM
                //
                if (!fromFound) {
                    if ((setWhite[p]&cursorFrom) != 0) {
                        //System.out.println("[FROM] MATCH W "+p+ " on "+i+ " cursor: "+i+ " --> "+cursorFrom);
                        pieceFrom = p;
                        fromFound = true;
                    }
                    if ((setBlack[p]&cursorFrom) != 0) {
                        //System.out.println("[FROM] MATCH W "+p+ " on "+i+ " cursor: "+i+ " --> "+cursorFrom);
                        pieceFrom = p+6;
                        fromFound = true;
                    }
                }

                //
                // TO
                //

                if (!toFound) {
                    if ((setWhite[p]&cursorTo) != 0) {
                        //System.out.println("[TO] MATCH W "+p+ " on "+i+ " cursor: "+i+ " --> "+cursorTo);
                        pieceTo = p;
                        toFound = true;
                    }
                    if ((setBlack[p]&cursorTo) != 0) {
                        //System.out.println("[TO] MATCH W "+p+ " on "+i+ " cursor: "+i+ " --> "+cursorTo);
                        pieceTo = p+6;
                        toFound = true;
                    }
                }



                // stop looking with piece groups if we found from & to
                if (fromFound && toFound) {
                    break;
                }
            }
            //System.out.println("## FROM "+(fromStart+i)+ " TO "+(toStart+i)+ " i :: "+i+ " --> "+(i+fromStart));
            //System.out.println("|--> from piece: "+pieceFrom);
            // move both cursors one square further
            cursorFrom <<= 1;
            cursorTo <<= 1;

            //
            // FROM
            //
            if (pieceFrom > -1) {
                if (emptyFrom > 0) {
                    fenFrom.append(emptyFrom);
                    emptyFrom = 0;
                    // System.out.println("-- FROM: write empty "+i);
                }
                fenFrom.append(tokenByPieceType(pieceFrom));
            } else {
                emptyFrom++;
                // System.out.println("++ emptyFrom");
            }


            //
            // TO
            //

            if (pieceTo > -1) {
                if (emptyTo > 0) {
                    fenTo.append(emptyTo);
                    emptyTo = 0;
                    //System.out.println("-- TO: write empty "+i);
                }
                fenTo.append(tokenByPieceType(pieceTo));
            } else {
                emptyTo++;
                //System.out.println("++ emptyTo");
            }

            // fill empty squares with empty square amount number
            if (i == 7) {
                if (emptyFrom > 0) {
                    // rSystem.out.println("FROM: filling empty: "+emptyFrom);
                    fenFrom.append(emptyFrom);
                }
                if (emptyTo > 0) {
                    //System.out.println("TO: filling empty: "+emptyTo);
                    fenTo.append(emptyTo);
                }
            }
        }

        //System.out.println("FROM: "+fenFrom.toString());
        //System.out.println("TO  : "+fenTo.toString());

        List<String> result = new ArrayList<>();
        result.add(fenFrom.toString());
        result.add(fenTo.toString());
        return result;
    }

    // just serialize changed rows and embed updated rows
    // into given fen string
    public String serialize(Move move, String oldFen) {
        List<String> updates = serializeUpdated(move);
        String fromRowUpdated = updates.get(0);
        String toRowUpdated = updates.get(1);

        String[] fenGroups = oldFen.split(" ");


        String placementGroup = fenGroups[0];
        String[] rows = placementGroup.split("/");

        int fromRow = move.getFrom().getIndex() / 8;
        int toRow = move.getTo().getIndex() / 8;

        rows[fromRow] = fromRowUpdated;
        rows[toRow] = toRowUpdated;

        fenGroups[0] = String.join("/", rows);

        return String.join(" ", fenGroups);
    }

    // serialize full board
    public String serializeAll() {
        long cursor = 1;
        int empty = 0;


        long[] setWhite = game.getSetWhite();
        long[] setBlack = game.getSetBlack();

        StringBuilder fenString = new StringBuilder();

        for (int i = 0; i < 64; i++) {
            if(i % 8 == 0 && i != 0) {
                fenString.insert(0, "/");
            }

            int piece = -1;

            for (int p = 0; p < 6; p++) {
                if ((setWhite[p]&cursor) != 0) {
                    piece = p;
                }
                if ((setBlack[p]&cursor) != 0) {
                    piece = p+6;
                }
            }

            if (piece > -1) {
                if (empty > 0) {
                    fenString.insert(0, empty);
                    empty = 0;
                }
                String token = tokenByPieceType(piece);
                fenString.insert(0, token);
            } else {
                empty++;
            }

            if (i % 8 == 7) {
                if (empty > 0) {
                    fenString.insert(0, empty);
                    empty = 0;
                }
            }


            cursor <<= 1;
        }

        return fenString.toString();
    }

    public String tokenByPieceType(int pieceType) {
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


    public FenSerializer(Game game) {
        this.game = game;
    }
}