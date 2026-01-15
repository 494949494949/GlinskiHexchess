package com.greefon.chess.state;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.greefon.chess.logic.ChessColor;
import com.greefon.chess.logic.ChessPiece;
import com.greefon.chess.logic.ChessType;
import com.greefon.chess.logic.Game;

import java.awt.Point;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateManager {
    /**
     * Применение:
     * StateManager.saveGameToJson(game, "state.json");
     * StateManager.loadGame(game, "state.json");
     */
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void saveGameToJson(Game game, String filePath) {
        try (Writer writer = new FileWriter(filePath)) {
            GameSnapshot snapshot = new GameSnapshot(game);

            gson.toJson(snapshot, writer);
            System.out.println("Game saved successfully to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadGameFromJson(Game game, String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            GameSnapshot snapshot = gson.fromJson(reader, GameSnapshot.class);

            if (snapshot != null) {
                applySnapshotToGame(game, snapshot);
                System.out.println("Game loaded successfully from " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void applySnapshotToGame(Game game, GameSnapshot snapshot) {
        game.setTurn(snapshot.turn);
        game.setGameOver(snapshot.gameOver);

        if (snapshot.enPassantPawnX != null && snapshot.enPassantPawnY != null) {
            game.setEnPassantPawn(new Point(snapshot.enPassantPawnX, snapshot.enPassantPawnY));
        } else {
            game.setEnPassantPawn(null);
        }

        Map<Point, ChessPiece> newPieces = new HashMap<>();
        for (PieceWrapper piece : snapshot.pieces) {
            newPieces.put(new Point(piece.x, piece.y), new ChessPiece(piece.type, piece.color));
        }
        game.setPiecesMap(newPieces);

        game.setDoubleMovablePawns(convertToPoints(snapshot.doubleMovableWhitePawns), ChessColor.WHITE);
        game.setDoubleMovablePawns(convertToPoints(snapshot.doubleMovableBlackPawns), ChessColor.BLACK);
    }

    private static List<Point> convertToPoints(List<PointWrapper> dataList) {
        List<Point> points = new ArrayList<>();
        if (dataList != null) {
            for (PointWrapper pd : dataList) {
                points.add(new Point(pd.x, pd.y));
            }
        }
        return points;
    }

    private static class GameSnapshot {
        ChessColor turn;
        boolean gameOver;
        Integer enPassantPawnX;
        Integer enPassantPawnY;
        List<PieceWrapper> pieces = new ArrayList<>();
        List<PointWrapper> doubleMovableWhitePawns = new ArrayList<>();
        List<PointWrapper> doubleMovableBlackPawns = new ArrayList<>();
        int totalTurns;

        public GameSnapshot(Game game) {
            this.turn = game.getTurn();
            this.totalTurns = game.getTotalTurns();
            this.gameOver = game.isGameOver();

            if (game.getEnPassantPawn() != null) {
                this.enPassantPawnX = game.getEnPassantPawn().x;
                this.enPassantPawnY = game.getEnPassantPawn().y;
            }

            for (Map.Entry<Point, ChessPiece> entry : game.getPiecesMap().entrySet()) {
                pieces.add(new PieceWrapper(
                        entry.getKey().x,
                        entry.getKey().y,
                        entry.getValue().getType(),
                        entry.getValue().getColor()
                ));
            }

            game.doubleMovableWhitePawns.forEach(p -> doubleMovableWhitePawns.add(new PointWrapper(p.x, p.y)));
            game.doubleMovableBlackPawns.forEach(p -> doubleMovableBlackPawns.add(new PointWrapper(p.x, p.y)));
        }
    }

    private static class PieceWrapper {
        int x, y;
        ChessType type;
        ChessColor color;

        public PieceWrapper(int x, int y, ChessType type, ChessColor color) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.color = color;
        }
    }

    private static class PointWrapper {
        int x, y;
        public PointWrapper(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}