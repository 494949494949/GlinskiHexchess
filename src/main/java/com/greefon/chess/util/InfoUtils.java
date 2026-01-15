package com.greefon.chess.util;

import com.greefon.chess.logic.ChessMoves;
import com.greefon.chess.logic.ChessPiece;
import com.greefon.chess.logic.Game;

public class InfoUtils {
    public static void printAllHexes(Game game) {
        String address;
        for (int rank = 1; rank <= 11; ++rank) {
            for (int fileIndex = 0; fileIndex < ChessMoves.getColumnHeights().length; ++fileIndex) {
                if (rank <= ChessMoves.getColumnHeights()[fileIndex]) {
                    address = ChessMoves.getFileLetters()[fileIndex] + Integer.toString(rank);
                    System.out.println(address);
                    ChessPiece piece = game.getPiece(ChessMoves.toPoint(address).x, ChessMoves.toPoint(address).y);
                    if (piece != null) {
                        System.out.println(piece);
                    } else {
                        System.out.println("Empty");
                    }
                }
            }
        }
    }
}
