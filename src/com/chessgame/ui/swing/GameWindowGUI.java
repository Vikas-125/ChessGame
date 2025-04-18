package com.chessgame.ui.swing;

import com.chessgame.ChessApplication;
import com.chessgame.ui.constants.UIConstants;
import com.chessgame.core.game.ChessGame;
import com.chessgame.core.game.Move;
import javax.swing.*;
import java.awt.*;

public class GameWindowGUI {
    private ChessGame game;
    private JFrame gameFrame;
    private BoardPanel boardPanel;
    private JLabel statusLabel;
    private JPanel statusPanel;
    private MoveHistoryPanel moveHistoryPanel;
    private ChessClock chessClock;

    // Track the last manual board orientation to restore after review mode
    private boolean lastManualBoardOrientation = false;

    // Flag to track if we're in manual review mode (to prevent board flipping)
    private boolean inManualReviewMode = false;

    // Initialize buttons at declaration
    private JButton drawButton = createStyledButton("Offer Draw");
    private JButton startButton = createStyledButton("<<"); // Alternative: "<<"
    private JButton backwardButton = createStyledButton("<"); // Alternative: "<"
    private JButton forwardButton = createStyledButton(">"); // Alternative: ">"
    private JButton endButton = createStyledButton(">>"); // Alternative: ">>"
    private JButton resignButton = createStyledButton("Resign");
    private JButton newGameButton = createStyledButton("New Game");
    private JButton flipBoardButton = createStyledButton("Flip Board");

    public GameWindowGUI(ChessGame game, int timeInSeconds, int incrementInSeconds) {
        this.game = game;
        this.boardPanel = new BoardPanel(game, this);
        this.chessClock = new ChessClock(timeInSeconds, incrementInSeconds, this);
        this.moveHistoryPanel = new MoveHistoryPanel(game, boardPanel);

        createAndShowGUI();
        setupNavigationButtons();
        setupControlButtons();
        startGame();
    }

    // Constructor overload for games without time control
    public GameWindowGUI(ChessGame game) {
        this(game, 600, 5); // Default to 10 minutes with 5 second increment
    }

    private void createAndShowGUI() {
        gameFrame = new JFrame("Chess Game");
        gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameFrame.setMinimumSize(new Dimension(1000, 750));

        // Create the main split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBackground(UIConstants.PRIMARY_LIGHT);

        // Create and add the left and right panels
        JPanel leftPanel = createGameAreaPanel();
        JPanel rightPanel = createControlPanel();

        // Set minimum sizes
        leftPanel.setMinimumSize(new Dimension(600, 600));
        rightPanel.setMinimumSize(new Dimension(300, 600));

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        gameFrame.add(splitPane);
        gameFrame.setSize(1000, 750);
        gameFrame.setLocationRelativeTo(null);

        SwingUtilities.invokeLater(() -> {
            splitPane.setDividerLocation(650);
        });

        gameFrame.setVisible(true);
    }

    private JPanel createGameAreaPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIConstants.PRIMARY_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top player panel (Black)
        JPanel topPlayerPanel = new JPanel(new BorderLayout());
        topPlayerPanel.setBackground(UIConstants.PRIMARY_LIGHT);
        JLabel blackPlayerLabel = new JLabel("Black Player");
        blackPlayerLabel.setFont(UIConstants.HEADER_FONT);
        topPlayerPanel.add(blackPlayerLabel, BorderLayout.WEST);
        topPlayerPanel.add(chessClock.getBlackClockPanel(), BorderLayout.EAST);

        // Bottom player panel (White)
        JPanel bottomPlayerPanel = new JPanel(new BorderLayout());
        bottomPlayerPanel.setBackground(UIConstants.PRIMARY_LIGHT);
        JLabel whitePlayerLabel = new JLabel("White Player");
        whitePlayerLabel.setFont(UIConstants.HEADER_FONT);
        bottomPlayerPanel.add(whitePlayerLabel, BorderLayout.WEST);
        bottomPlayerPanel.add(chessClock.getWhiteClockPanel(), BorderLayout.EAST);

        // Center board panel
        JPanel boardWrapper = new JPanel(new GridBagLayout());
        boardWrapper.setBackground(UIConstants.PRIMARY_LIGHT);
        boardWrapper.add(boardPanel);

        panel.add(topPlayerPanel, BorderLayout.NORTH);
        panel.add(boardWrapper, BorderLayout.CENTER);
        panel.add(bottomPlayerPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIConstants.PRIMARY_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Status panel at top with modern styling
        statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(UIConstants.ACCENT);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        statusLabel = new JLabel(game.getCurrentPlayer().getColor() + "'s turn");
        statusLabel.setFont(UIConstants.HEADER_FONT);
        statusLabel.setForeground(UIConstants.PRIMARY_LIGHT);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusPanel.add(statusLabel, BorderLayout.CENTER);

        // Navigation panel with modern styling
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        navigationPanel.setBackground(UIConstants.PRIMARY_LIGHT);
        navigationPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER),
                BorderFactory.createEmptyBorder(5, 0, 5, 0)));
        navigationPanel.add(startButton);
        navigationPanel.add(backwardButton);
        navigationPanel.add(forwardButton);
        navigationPanel.add(endButton);

        // Combine status and navigation in top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIConstants.PRIMARY_LIGHT);
        topPanel.add(statusPanel, BorderLayout.NORTH);
        topPanel.add(navigationPanel, BorderLayout.SOUTH);

        // Move history in center with modern styling
        JScrollPane historyScrollPane = new JScrollPane(moveHistoryPanel);
        historyScrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));

        // Control buttons at bottom with modern styling
        JPanel controlPanel = new JPanel(new GridLayout(4, 1, 8, 8));
        controlPanel.setBackground(UIConstants.PRIMARY_LIGHT);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        // Set consistent size for control buttons
        Dimension buttonSize = new Dimension(200, 35);
        drawButton.setPreferredSize(buttonSize);
        resignButton.setPreferredSize(buttonSize);
        newGameButton.setPreferredSize(buttonSize);
        flipBoardButton.setPreferredSize(buttonSize);

        controlPanel.add(drawButton);
        controlPanel.add(resignButton);
        controlPanel.add(newGameButton);
        controlPanel.add(flipBoardButton);

        // Add all components to main panel
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(historyScrollPane, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void setupNavigationButtons() {
        JButton[] navButtons = { startButton, backwardButton, forwardButton, endButton };

        for (JButton button : navButtons) {
            button.setFont(UIConstants.BUTTON_FONT);
            button.setForeground(Color.BLACK); // Black text
            button.setBackground(Color.LIGHT_GRAY); // Light grey background
            button.setOpaque(true);
            button.setContentAreaFilled(true);
            button.setBorderPainted(true);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIConstants.ACCENT_LIGHT, 1),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)));

            // Add hover effects
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(UIConstants.ACCENT); // Accent color background
                    button.setForeground(Color.WHITE); // White text
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(UIConstants.ACCENT, 1),
                            BorderFactory.createEmptyBorder(10, 20, 10, 20)));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(Color.LIGHT_GRAY); // Light grey background
                    button.setForeground(Color.BLACK); // Black text
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(UIConstants.ACCENT_LIGHT, 1),
                            BorderFactory.createEmptyBorder(10, 20, 10, 20)));
                }
            });
        }

        // Add the existing action listeners
        startButton.addActionListener(e -> {
            // Save the current board orientation before entering review mode
            if (!inManualReviewMode) {
                lastManualBoardOrientation = boardPanel.isInverted();
            }

            // Set review mode flag
            inManualReviewMode = true;

            // Go to the start of the game
            game.reset();

            boardPanel.repaint();
            moveHistoryPanel.highlightCurrentMove(game.getCurrentMoveIndex());
            updateNavigationButtons();
        });

        backwardButton.addActionListener(e -> {
            if (game.canMoveBackward()) {
                // Save the current board orientation before entering review mode
                if (!inManualReviewMode) {
                    lastManualBoardOrientation = boardPanel.isInverted();
                }

                // Set review mode flag
                inManualReviewMode = true;

                // Use resetAndReplayMoves for more reliable navigation
                int targetIndex = game.getCurrentMoveIndex() - 1;
                game.setCurrentMoveIndex(targetIndex);
                game.resetAndReplayMoves();

                boardPanel.repaint();
                moveHistoryPanel.highlightCurrentMove(game.getCurrentMoveIndex());
                updateNavigationButtons();
            }
        });

        forwardButton.addActionListener(e -> {
            if (game.canMoveForward()) {
                // Use resetAndReplayMoves for more reliable navigation
                int targetIndex = game.getCurrentMoveIndex() + 1;
                game.setCurrentMoveIndex(targetIndex);
                game.resetAndReplayMoves();

                // If we're exiting review mode, restore the previous board orientation
                if (targetIndex == game.getMoveHistory().size() - 1) {
                    // We're returning to the current game state
                    inManualReviewMode = false;
                    boardPanel.setInverted(lastManualBoardOrientation);
                }

                boardPanel.repaint();
                moveHistoryPanel.highlightCurrentMove(game.getCurrentMoveIndex());
                updateNavigationButtons();
            }
        });

        endButton.addActionListener(e -> {
            // Go to the end of the move history
            if (game.getMoveHistory().size() > 0) {
                game.setCurrentMoveIndex(game.getMoveHistory().size() - 1);
                game.resetAndReplayMoves();

                // We're returning to the current game state
                inManualReviewMode = false;
                boardPanel.setInverted(lastManualBoardOrientation);
            }
            boardPanel.repaint();
            moveHistoryPanel.highlightCurrentMove(game.getCurrentMoveIndex());
            updateNavigationButtons();
        });

        // Initial button state
        updateNavigationButtons();
    }

    private void updateNavigationButtons() {
        if (statusLabel == null || game == null)
            return; // Add null check

        boolean canMoveBackward = game.canMoveBackward();
        boolean canMoveForward = game.canMoveForward();

        startButton.setEnabled(canMoveBackward);
        backwardButton.setEnabled(canMoveBackward);
        forwardButton.setEnabled(canMoveForward);
        endButton.setEnabled(canMoveForward);

        // Handle review mode status
        if (inManualReviewMode || game.isInReviewMode()) {
            // We're in review mode - don't change board orientation
            statusLabel.setText("Reviewing moves");
        } else {
            // We're in normal gameplay mode
            String currentPlayerColor = game.getCurrentPlayer().getColor();
            statusLabel.setText(currentPlayerColor + "'s turn");

            // Update board orientation based on current player's color
            boardPanel.setInverted(currentPlayerColor.equals("Black"));
        }
    }

    public void updateMoveHistory(Move move) {
        handleMove(move);
    }

    private void handleMove(Move move) {
        if (game.isInReviewMode()) {
            return; // Prevent making new moves while in review mode
        }

        moveHistoryPanel.addMove(move);

        // Update board orientation based on current player's color if not in review
        // mode
        if (!inManualReviewMode && !game.isInReviewMode()) {
            String currentPlayerColor = game.getCurrentPlayer().getColor();
            boardPanel.setInverted(currentPlayerColor.equals("Black"));
        }

        boardPanel.repaint();
        chessClock.switchTurn();
        updateNavigationButtons();

        if (game.isGameOver()) {
            chessClock.stop();
            String result = game.getGameResult();
            StatusType type;

            if (result.contains("stalemate")) {
                type = StatusType.DRAW;
            } else if (result.contains("insufficient material") ||
                    result.contains("fifty-move rule") ||
                    result.contains("threefold repetition")) {
                type = StatusType.DRAW;
            } else if (result.contains("checkmate")) {
                type = StatusType.SUCCESS;
            } else {
                type = StatusType.NORMAL;
            }

            showGameOver(result);
            updateStatus(result, type);
            disableGameControls();
        } else if (!game.isInReviewMode()) { // Changed from isInReviewMode() to game.isInReviewMode()
            // Only update the turn status if the game is still ongoing and not in review
            // mode
            updateStatus(game.getCurrentPlayer().getColor() + "'s turn", StatusType.NORMAL);
        }
    }

    public void showGameOver(String message) {
        StatusType type;
        if (message.contains("wins")) {
            type = message.contains("resignation") ? StatusType.ERROR : StatusType.SUCCESS;
        } else if (message.contains("drawn")) {
            type = StatusType.DRAW;
        } else {
            type = StatusType.NORMAL;
        }

        updateStatus(message, type);

        Window gameWindow = SwingUtilities.getWindowAncestor(boardPanel);

        JOptionPane.showMessageDialog(
                gameWindow,
                message,
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE);

        chessClock.stop();
        boardPanel.setEnabled(false);

        if (gameWindow != null) {
            disableGameControlButtons(gameWindow);
        }
    }

    private void disableGameControlButtons(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                if (button.getText().equals("Offer Draw") ||
                        button.getText().equals("Resign")) {
                    button.setEnabled(false);
                }
            } else if (comp instanceof Container) {
                disableGameControlButtons((Container) comp);
            }
        }
    }

    private void setupControlButtons() {
        JButton[] controlButtons = { drawButton, resignButton, newGameButton, flipBoardButton };

        for (JButton button : controlButtons) {
            button.setFont(UIConstants.BUTTON_FONT);
            button.setForeground(Color.BLACK); // Black text
            button.setBackground(Color.LIGHT_GRAY); // Light grey background
            button.setOpaque(true);
            button.setContentAreaFilled(true);
            button.setBorderPainted(true);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIConstants.ACCENT_LIGHT, 1),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)));

            // Hover effects
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(UIConstants.ACCENT); // Accent color background
                    button.setForeground(Color.WHITE); // White text
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(UIConstants.ACCENT, 1),
                            BorderFactory.createEmptyBorder(10, 20, 10, 20)));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(Color.LIGHT_GRAY); // Light grey background
                    button.setForeground(Color.BLACK); // Black text
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(UIConstants.ACCENT_LIGHT, 1),
                            BorderFactory.createEmptyBorder(10, 20, 10, 20)));
                }
            });
        }

        // Draw button action
        drawButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                    gameFrame,
                    "Do both players agree to a draw?",
                    "Draw Offer",
                    JOptionPane.YES_NO_OPTION);

            if (response == JOptionPane.YES_OPTION) {
                updateStatus("Game drawn by agreement!", StatusType.DRAW);
                showGameOver("Game drawn by agreement!");
                disableGameControls();
            }
        });

        // Resign button action
        resignButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                    gameFrame,
                    game.getCurrentPlayer().getColor() + " resigns. Are you sure?",
                    "Resign",
                    JOptionPane.YES_NO_OPTION);

            if (response == JOptionPane.YES_OPTION) {
                String winner = game.getCurrentPlayer().getColor().equals("White") ? "Black" : "White";
                String message = winner + " wins by resignation!";
                updateStatus(message, StatusType.ERROR);
                showGameOver(message);
                disableGameControls();
            }
        });

        // New game button action
        newGameButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                    gameFrame,
                    "Start a new game?",
                    "New Game",
                    JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                gameFrame.dispose();
                ChessApplication.showMainWindow();
            }
        });

        // Flip board button action
        flipBoardButton.addActionListener(e -> {
            // Toggle the board orientation manually
            boolean newOrientation = !boardPanel.isInverted();
            boardPanel.setInverted(newOrientation);

            // Save this as the manual orientation if not in review mode
            if (!inManualReviewMode && !game.isInReviewMode()) {
                lastManualBoardOrientation = newOrientation;
            }
        });
    }

    private void disableGameControls() {
        boardPanel.setEnabled(false);
        for (Component comp : gameFrame.getContentPane().getComponents()) {
            if (comp instanceof Container) {
                disableGameControlButtons((Container) comp);
            }
        }
    }

    public void startGame() {
        // Set initial board orientation based on player color
        String initialPlayerColor = game.getCurrentPlayer().getColor();
        boardPanel.setInverted(initialPlayerColor.equals("Black"));

        chessClock.reset();
        chessClock.start();
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    public JFrame getGameFrame() {
        return gameFrame;
    }

    public JLabel getStatusLabel() {
        return statusLabel;
    }

    public ChessGame getGame() {
        return game;
    }

    /**
     * Checks if the game is in manual review mode.
     *
     * @return true if in manual review mode, false otherwise
     */
    public boolean isInManualReviewMode() {
        return inManualReviewMode;
    }

    public void updateStatus(String status, StatusType type) {
        statusLabel.setText(status);

        switch (type) {
            case NORMAL:
                statusPanel.setBackground(UIConstants.ACCENT);
                statusLabel.setForeground(UIConstants.PRIMARY_LIGHT);
                break;
            case WARNING:
                statusPanel.setBackground(new Color(255, 193, 7));
                statusLabel.setForeground(UIConstants.PRIMARY_DARK);
                break;
            case SUCCESS:
                statusPanel.setBackground(new Color(40, 167, 69));
                statusLabel.setForeground(UIConstants.PRIMARY_LIGHT);
                break;
            case ERROR:
                statusPanel.setBackground(new Color(220, 53, 69));
                statusLabel.setForeground(UIConstants.PRIMARY_LIGHT);
                break;
            case DRAW:
                statusPanel.setBackground(new Color(23, 162, 184));
                statusLabel.setForeground(UIConstants.PRIMARY_LIGHT);
                break;
            case DRAW_PENDING:
                statusPanel.setBackground(new Color(255, 193, 7));
                statusLabel.setForeground(UIConstants.PRIMARY_DARK);

                // Only create timer for DRAW_PENDING
                Timer fadeTimer = new Timer(2000, e -> {
                    if (!game.isGameOver()) { // Only revert if game isn't over
                        updateStatus(game.getCurrentPlayer().getColor() + "'s turn", StatusType.NORMAL);
                    }
                });
                fadeTimer.setRepeats(false);
                fadeTimer.start();
                break;
        }
    }

    // Add an enum for status types
    private enum StatusType {
        NORMAL,
        WARNING,
        SUCCESS,
        ERROR,
        DRAW,
        DRAW_PENDING
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        // Use a larger font size for navigation symbols
        if (text.matches("[⏮◀▶⏭]")) {
            button.setFont(new Font("SansSerif", Font.BOLD, 24)); // Increased font size for symbols
        } else {
            button.setFont(UIConstants.BUTTON_FONT);
        }
        button.setForeground(Color.BLACK); // Black text
        button.setBackground(Color.LIGHT_GRAY); // Light grey background
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.ACCENT_LIGHT, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(220, 45));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(UIConstants.ACCENT); // Accent color background
                button.setForeground(Color.WHITE); // White text
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIConstants.ACCENT, 1),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.LIGHT_GRAY); // Light grey background
                button.setForeground(Color.BLACK); // Black text
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIConstants.ACCENT_LIGHT, 1),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)));
            }
        });

        return button;
    }
}
