package com.greefon.chess.gui;

import com.greefon.chess.logic.*;
import com.greefon.chess.state.GUIStateManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Board extends JComponent implements ActionListener, MouseListener,
        KeyListener, MouseWheelListener {
    private final Timer timer;
    private static final double SIN_60 = Math.sin(Math.toRadians(60));
    private static final double COS_60 = Math.cos(Math.toRadians(60));

    private final double scaleFactor = 0.8;
    private final Set<Integer> pressedKeys = new HashSet<>();
    private static final Color COLOR1 = new Color(255, 255, 255);
    private static final Color COLOR2 = new Color(205, 205, 205);
    private static final Color COLOR3 = new Color(154, 154, 154);
    private static final Color COLOR_SELECTED = new Color(230, 250, 0); // Yellow
    private static final Color COLOR_LEGAL = new Color(180, 250, 100);  // Greenish-Yellow

    private Game game;
    private double boardSize;

    private Point selectedPiece = null;
    private List<Point> validMoves = new ArrayList<>();


    public Board() {
        addKeyListener(this);
        addMouseListener(this);

        setFocusable(true);
        requestFocusInWindow();

        timer = new Timer(100, this);
        timer.start();

        double width = getWidth();
        double height = getHeight();
        this.boardSize = scaleFactor * Math.min(width, height);

        this.init();
    }

    public void init() {
        this.game = new Game();
        selectedPiece = null;
        validMoves = new ArrayList<>();
//        printAllHexes(Game game);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        paintBoard(g2d);
    }

    private void paintBoard(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();
        boardSize = scaleFactor * Math.min(width, height);
        int[] columnHeights = ChessMoves.getColumnHeights();

        Polygon polygon;
        for (int i = 0; i < 11; ++i) {
            for (int j = 0; j < columnHeights[i]; ++j) {
                Point point = new Point(i, j);
                double x = getXOnScreen(point);
                double y = getYOnScreen(point);

                double hexSize = boardSize / (10 * SIN_60);
                polygon = prepareHexagon(x, y, hexSize);

                int order = (i + (i / 6) * (i + 1) + j) % 3;

                if (point.equals(selectedPiece)) {
                    g2d.setColor(COLOR_SELECTED);
                    g2d.fillPolygon(polygon);
                }
                else if (validMoves.contains(point)) {
                    g2d.setColor(COLOR_LEGAL);
                    g2d.fillPolygon(polygon);
                } else if (order == 0) {
                    g2d.setColor(COLOR1);
                    g2d.fillPolygon(polygon);
                } else if (order == 1) {
                    g2d.setColor(COLOR2);
                    g2d.fillPolygon(polygon);
                } else {
                    g2d.setColor(COLOR3);
                    g2d.fillPolygon(polygon);
                }

                ChessPiece piece = game.getPiece(i, j);
                if (piece != null) {
                    paintChessPiece(g2d, piece, (int) x, (int) y);
                }
            }
        }
    }

    private void paintChessPiece(Graphics2D g2d, ChessPiece piece, int x, int y) {
        double size = (boardSize / (10 * SIN_60)) * 0.8;
        PieceRenderer.paint(this, g2d, piece, x, y, size);
    }

    private Polygon prepareHexagon(double x, double y, double size) {
        Polygon polygon = new Polygon();

        for (int i = -3; i < 3; ++i) {
            double angle = Math.toRadians(30 + 60 * i);
            polygon.addPoint(
                    (int) (x - size * (Math.sin(angle) / 2)),
                    (int) (y - size * (Math.cos(angle) / 2))
            );
        }
        return polygon;
    }

    private double getXOnScreen(Point pos) {
        return (double) (getWidth() - boardSize
                * Math.sin(Math.PI * 60 / 180)
                * (pos.x - pos.y - (pos.x - 5)
                * (int) (pos.x / 6)) / 5) / 2;
    }

    private double getYOnScreen(Point pos) {
        return (double) (getHeight()
                - Math.min(getWidth(), getHeight())
                + (1 - scaleFactor) * Math.min(getWidth(), getHeight())
                + boardSize * COS_60
                * (pos.x + pos.y + (pos.x - 5) * (int) (pos.x / 6)) / 5) / 2;
    }

    public Game getGameInstance() {
        return game;
    }

    private void promptRestart() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Restart game?",
                "Restart confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) {
            return;
        }

        init();
        repaint();
    }

    private void promptExit() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Exit game?",
                "Exit confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) {
            return;
        }

        GUIStateManager.saveGameToJson(this);

        System.exit(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer) {
            repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE && pressedKeys.add(e.getKeyCode())) {
            promptExit();
        }
        if (e.getKeyCode() == KeyEvent.VK_R && pressedKeys.add(e.getKeyCode())) {
            promptRestart();
        }
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S && pressedKeys.add(e.getKeyCode())) {
            GUIStateManager.saveGameToJson(this);
        }
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_L && pressedKeys.add(e.getKeyCode())) {
            GUIStateManager.loadGameFromJson(this);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if (game.isGameOver()) {
            init();
            repaint();
            return;
        }

        double width = getWidth();
        double height = getHeight();
        boardSize = scaleFactor * Math.min(width, height);
        double hexSize = boardSize / (10 * SIN_60);

        double clickRadius = hexSize * SIN_60 / 2;

        int[] columnHeights = ChessMoves.getColumnHeights();
        boolean clickedOnBoard = false;

        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < columnHeights[i]; j++) {
                Point point = new Point(i, j);
                double centerX = getXOnScreen(point);
                double centerY = getYOnScreen(point);

                if (Point.distance(e.getX(), e.getY(), centerX, centerY) < clickRadius) {
                    handleHexClick(point);
                    clickedOnBoard = true;
                    break;
                }
            }
            if (clickedOnBoard) break;
        }

        if (!clickedOnBoard) {
            selectedPiece = null;
            validMoves.clear();
        }
        repaint();
    }

    private void handleHexClick(Point clickedPoint) {
        if (selectedPiece != null && validMoves.contains(clickedPoint)) {
            game.performMove(selectedPiece, clickedPoint);
            selectedPiece = null;
            validMoves.clear();

            if (game.isGameOver()) {
                JOptionPane.showMessageDialog(this, "Game over in " + game.getTotalTurns() +
                        "turns!\n Winner: " + (game.getWinner() == ChessColor.WHITE ? "black" : "white"));
            }
            return;
        }

        ChessPiece clickedPiece = game.getPiece(clickedPoint);
        if (clickedPiece != null && clickedPiece.getColor() == game.getTurn()) {
            selectedPiece = clickedPoint;
            validMoves = game.getLegalMoves(selectedPiece);
        } else {
            selectedPiece = null;
            validMoves.clear();
        }
    }
    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {}
}
