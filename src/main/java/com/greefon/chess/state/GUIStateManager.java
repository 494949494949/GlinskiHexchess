package com.greefon.chess.state;

import com.greefon.chess.gui.Board;
import com.greefon.chess.logic.Game;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class GUIStateManager {
    public static void saveGameToJson(Board board) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setSelectedFile(new File("state.json"));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files (*.json)", "json");
        fileChooser.setFileFilter(filter);

        Game game = board.getGameInstance();

        int userSelection = fileChooser.showSaveDialog(board);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            if (!filePath.toLowerCase().endsWith(".json")) {
                fileToSave = new File(filePath + ".json");
                filePath = fileToSave.getAbsolutePath();
            }

            if (fileToSave.exists()) {
                int response = JOptionPane.showConfirmDialog(
                        board,
                        "The file '" + fileToSave.getName() + "' already exists.\nDo you want to replace it?",
                        "Confirm Overwrite",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) {
                    return;
                }
            }

            StateManager.saveGameToJson(game, filePath);

//            JOptionPane.showMessageDialog(board, "State saved successfully!");
        }
    }
    public static void loadGameFromJson(Board board) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setSelectedFile(new File("state.json"));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files (*.json)", "json");
        fileChooser.setFileFilter(filter);

        Game game = board.getGameInstance();

        int userSelection = fileChooser.showSaveDialog(board);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            String filePath = fileToLoad.getAbsolutePath();

            if (!filePath.toLowerCase().endsWith(".json")) {
                fileToLoad = new File(filePath + ".json");
                filePath = fileToLoad.getAbsolutePath();
            }

            if (fileToLoad.exists()) {
                StateManager.loadGameFromJson(game, filePath);
//                JOptionPane.showMessageDialog(board, "State loaded successfully!");
            }
        }
    }
}
