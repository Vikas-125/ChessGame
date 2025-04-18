package com.chessgame.ui.swing;

// import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.*;
import com.chessgame.ui.constants.UIConstants;

public class ChessClock {
    private Timer timer;
    private JPanel whiteClockPanel;
    private JPanel blackClockPanel;
    private JLabel whiteTimeLabel;
    private JLabel blackTimeLabel;
    private int whiteTimeLeft;
    private int blackTimeLeft;
    private int increment;
    private boolean isWhiteTurn;
    private GameWindowGUI gui;
    private final int initialTimeInSeconds;

    public ChessClock(int timeInSeconds, int incrementInSeconds, GameWindowGUI gui) {
        super();  // Call JPanel constructor
        this.initialTimeInSeconds = timeInSeconds;
        this.whiteTimeLeft = timeInSeconds;
        this.blackTimeLeft = timeInSeconds;
        this.increment = incrementInSeconds;
        this.gui = gui;
        this.isWhiteTurn = true;

        // Initialize clock panels
        createClockPanels();
        
        timer = new Timer(1000, e -> {
            if (isWhiteTurn) {
                whiteTimeLeft--;
            } else {
                blackTimeLeft--;
            }
            updateDisplays();
            
            if (whiteTimeLeft <= 0 || blackTimeLeft <= 0) {
                handleTimeOut();
            }
        });
        
        updateDisplays(); // Initialize display
    }

    private void createClockPanels() {
        whiteClockPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2)); // Reduced vertical padding
        blackClockPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2)); // Reduced vertical padding
        
        whiteTimeLabel = new JLabel(formatTime(whiteTimeLeft));
        blackTimeLabel = new JLabel(formatTime(blackTimeLeft));
        
        // Set fonts and colors
        whiteTimeLabel.setFont(UIConstants.HEADER_FONT);
        blackTimeLabel.setFont(UIConstants.HEADER_FONT);
        
        whiteTimeLabel.setForeground(UIConstants.PRIMARY_DARK);
        blackTimeLabel.setForeground(UIConstants.PRIMARY_DARK);
        
        whiteClockPanel.add(whiteTimeLabel);
        blackClockPanel.add(blackTimeLabel);
        
        // Set panel backgrounds
        whiteClockPanel.setBackground(UIConstants.PRIMARY_LIGHT);
        blackClockPanel.setBackground(UIConstants.PRIMARY_LIGHT);
        
        // Add borders with reduced padding
        whiteClockPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER),
            BorderFactory.createEmptyBorder(2, 7, 2, 7)  // Reduced padding
        ));
        blackClockPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER),
            BorderFactory.createEmptyBorder(2, 7, 2, 7)  // Reduced padding
        ));
    }

    public void start() {
        if (!timer.isRunning()) {
            timer.start();
            updateDisplays();
        }
    }

    public void stop() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }

    public void switchTurn() {
        if (timer.isRunning()) {
            if (isWhiteTurn) {
                whiteTimeLeft += increment;
            } else {
                blackTimeLeft += increment;
            }
            isWhiteTurn = !isWhiteTurn;
            updateDisplays();
        }
    }

    private void updateDisplays() {
        whiteTimeLabel.setText(formatTime(whiteTimeLeft));
        blackTimeLabel.setText(formatTime(blackTimeLeft));
        
        // Highlight active player's clock
        if (isWhiteTurn) {
            whiteClockPanel.setBackground(UIConstants.ACCENT);
            whiteTimeLabel.setForeground(UIConstants.PRIMARY_LIGHT);
            blackClockPanel.setBackground(UIConstants.PRIMARY_LIGHT);
            blackTimeLabel.setForeground(UIConstants.PRIMARY_DARK);
        } else {
            blackClockPanel.setBackground(UIConstants.ACCENT);
            blackTimeLabel.setForeground(UIConstants.PRIMARY_LIGHT);
            whiteClockPanel.setBackground(UIConstants.PRIMARY_LIGHT);
            whiteTimeLabel.setForeground(UIConstants.PRIMARY_DARK);
        }
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }

    private void handleTimeOut() {
        timer.stop();
        String loser = isWhiteTurn ? "White" : "Black";
        gui.showGameOver(loser + " lost on time");
    }

    public JPanel getWhiteClockPanel() {
        return whiteClockPanel;
    }

    public JPanel getBlackClockPanel() {
        return blackClockPanel;
    }

    public void reset() {
        timer.stop();
        whiteTimeLeft = initialTimeInSeconds;
        blackTimeLeft = initialTimeInSeconds;
        isWhiteTurn = true;
        updateDisplays();
    }
} 