package com.greefon.chess.gui;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.github.weisj.jsvg.view.ViewBox;
import com.greefon.chess.logic.ChessColor;
import com.greefon.chess.logic.ChessPiece;
import com.greefon.chess.logic.ChessType;

import java.awt.*;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

public class PieceRenderer {
    private static final Map<
            ChessColor, Map<ChessType, SVGDocument>
            > spriteCache = new EnumMap<>(ChessColor.class);

    static {
        loadSprites();
    }

    private static void loadSprites() {
        if (!spriteCache.isEmpty()) return;

        SVGLoader loader = new SVGLoader();

        for (ChessColor c : ChessColor.values()) {
            Map<ChessType, SVGDocument> typeMap = new EnumMap<>(ChessType.class);
            spriteCache.put(c, typeMap);

            for (ChessType t : ChessType.values()) {
                String pieceType = t.name().toLowerCase();
                char pieceColor = c.name().toLowerCase().charAt(0);
                String filename = "/pieces-svg/" + pieceType + "-" + pieceColor + ".svg";
                URL url = PieceRenderer.class.getResource(filename);

                if (url != null) {
                    typeMap.put(t, loader.load(url));
                } else {
                    System.err.println("Could not find SVG: " + filename);
                }
            }
        }
    }

    public static void paint(
            Board board, Graphics2D g2d,
            ChessPiece piece, int x, int y, double size) {
        if (piece == null) return;

        SVGDocument svg = spriteCache.get(piece.getColor()).get(piece.getType());

        if (svg != null) {
            double centerX = x - (size / 2);
            double centerY = y - (size / 2);

            ViewBox box = new ViewBox(
                    (float) centerX, (float) centerY,
                    (float) size, (float) size
            );

            svg.render(board, g2d, box);
        }
    }
}