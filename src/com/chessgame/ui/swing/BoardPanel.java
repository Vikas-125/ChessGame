package com.chessgame.ui.swing;

import com.chessgame.core.board.Position;
import com.chessgame.core.board.Square;
import com.chessgame.core.game.ChessGame;
import com.chessgame.core.game.Move;
import com.chessgame.core.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;
import java.util.ArrayList;

public class BoardPanel extends JPanel {
    private ChessGame game;
    private GameWindowGUI gui;
    private static final double LABEL_RATIO = 0.04; // smaller coordinates
    private static final int MAX_LABEL_SIZE = 20; // Maximum size for coordinate labels
    private Position selectedPosition = null;
    private List<Move> validMoves = null;
    private Position hoveredPosition = null;
    private Position dragStartPosition = null;
    private Piece draggedPiece = null;
    private Point currentMousePosition = null;
    private boolean isDragging = false;
    private Timer dragTimer;
    private Point mousePressPoint = null;
    private static final int DRAG_THRESHOLD = 5;
    private boolean isInverted = false;

    /**
     * Checks if the board is currently inverted (Black's perspective).
     *
     * @return true if the board is inverted, false otherwise
     */
    public boolean isInverted() {
        return isInverted;
    }

    /**
     * Sets whether the board should be inverted (shown from Black's perspective).
     * When inverted, Black pieces are at the bottom and coordinates are flipped.
     *
     * @param inverted true to show the board from Black's perspective, false for
     *                 White's perspective
     */
    public void setInverted(boolean inverted) {
        this.isInverted = inverted;
        repaint();
    }

    // Define board colors
    private static final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private static final Color DARK_SQUARE = new Color(181, 136, 99);
    private static final Color SELECTED_COLOR = new Color(255, 255, 0, 100);
    private static final Color VALID_MOVE_COLOR = new Color(0, 255, 0, 100);
    private static final Color BORDER_COLOR = new Color(90, 90, 90);
    private static final Color BORDER_BACKGROUND = new Color(181, 136, 99).darker();
    private static final Color COORDINATE_TEXT_COLOR = Color.WHITE;
    private static final int BORDER_THICKNESS = 12;

    public BoardPanel(ChessGame game, GameWindowGUI gui) {
        this.game = game;
        this.gui = gui;

        // Set the preferred size to be moderate initially
        setPreferredSize(new Dimension(600, 600));
        setMinimumSize(new Dimension(320, 320)); // New minimum size for board

        // Add component listener for resizing
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Update preferred size when resized
                int size = Math.min(getParent().getWidth(), getParent().getHeight());
                setPreferredSize(new Dimension(size, size));
                revalidate();
                repaint();
            }
        });

        // Initialize drag timer
        dragTimer = new Timer(100, e -> {
            if (mousePressPoint != null) {
                Point currentPoint = getMousePosition();
                if (currentPoint != null) {
                    int dx = currentPoint.x - mousePressPoint.x;
                    int dy = currentPoint.y - mousePressPoint.y;
                    if (Math.sqrt(dx * dx + dy * dy) > DRAG_THRESHOLD) {
                        startDragging();
                    }
                }
            }
        });
        dragTimer.setRepeats(false);

        // Mouse listeners with adjusted coordinates
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (game.isInReviewMode())
                    return; // Suppress interaction in review mode

                Dimension sizes = calculateSizes();
                int labelSize = sizes.width;
                int tileSize = sizes.height;

                // Calculate board position
                int totalBoardWidth = 8 * tileSize + 2 * labelSize;
                int totalBoardHeight = 8 * tileSize + 2 * labelSize;
                int xOffset = (getWidth() - totalBoardWidth) / 2;
                int yOffset = (getHeight() - totalBoardHeight) / 2;

                // Adjust coordinates relative to board position
                int x = e.getX() - xOffset - labelSize;
                int y = e.getY() - yOffset - labelSize;

                if (x >= 0 && x < 8 * tileSize && y >= 0 && y < 8 * tileSize) {
                    MouseEvent adjustedEvent = new MouseEvent(
                            e.getComponent(), e.getID(), e.getWhen(), e.getModifiersEx(),
                            x, y, e.getClickCount(), e.isPopupTrigger(), e.getButton());
                    handleMousePress(adjustedEvent);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (game.isInReviewMode())
                    return; // Suppress interaction in review mode

                Dimension sizes = calculateSizes();
                int labelSize = sizes.width;
                int tileSize = sizes.height;

                // Calculate board position
                int totalBoardWidth = 8 * tileSize + 2 * labelSize;
                int totalBoardHeight = 8 * tileSize + 2 * labelSize;
                int xOffset = (getWidth() - totalBoardWidth) / 2;
                int yOffset = (getHeight() - totalBoardHeight) / 2;

                // Adjust coordinates relative to board position
                int x = e.getX() - xOffset - labelSize;
                int y = e.getY() - yOffset - labelSize;

                MouseEvent adjustedEvent = new MouseEvent(
                        e.getComponent(), e.getID(), e.getWhen(), e.getModifiersEx(),
                        x, y, e.getClickCount(), e.isPopupTrigger(), e.getButton());
                handleMouseRelease(adjustedEvent);
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (game.isInReviewMode())
                    return; // Suppress interaction in review mode

                Dimension sizes = calculateSizes();
                int labelSize = sizes.width;
                int tileSize = sizes.height;

                // Calculate board position
                int totalBoardWidth = 8 * tileSize + 2 * labelSize;
                int totalBoardHeight = 8 * tileSize + 2 * labelSize;
                int xOffset = (getWidth() - totalBoardWidth) / 2;
                int yOffset = (getHeight() - totalBoardHeight) / 2;

                // Adjust coordinates relative to board position
                int x = e.getX() - xOffset - labelSize;
                int y = e.getY() - yOffset - labelSize;

                MouseEvent adjustedEvent = new MouseEvent(
                        e.getComponent(), e.getID(), e.getWhen(), e.getModifiersEx(),
                        x, y, e.getClickCount(), e.isPopupTrigger(), e.getButton());
                handleMouseMove(adjustedEvent);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (game.isInReviewMode())
                    return; // Suppress interaction in review mode

                Dimension sizes = calculateSizes();
                int labelSize = sizes.width;
                int tileSize = sizes.height;

                // Calculate board position
                int totalBoardWidth = 8 * tileSize + 2 * labelSize;
                int totalBoardHeight = 8 * tileSize + 2 * labelSize;
                int xOffset = (getWidth() - totalBoardWidth) / 2;
                int yOffset = (getHeight() - totalBoardHeight) / 2;

                // Adjust coordinates relative to board position
                int x = e.getX() - xOffset - labelSize;
                int y = e.getY() - yOffset - labelSize;

                MouseEvent adjustedEvent = new MouseEvent(
                        e.getComponent(), e.getID(), e.getWhen(), e.getModifiersEx(),
                        x, y, e.getClickCount(), e.isPopupTrigger(), e.getButton());
                handleMouseDrag(adjustedEvent);
            }
        });
    }

    private Dimension calculateSizes() {
        // Calculate the maximum square size that will fit in the panel
        int width = getWidth();
        int height = getHeight();
        int minDimension = Math.min(width, height);

        // Label size should be proportional but not too large
        int labelSize = Math.min((int) (minDimension * LABEL_RATIO), MAX_LABEL_SIZE);

        // Calculate tile size based on available space
        int boardSpace = minDimension - (2 * labelSize);
        int tileSize = boardSpace / 8;

        return new Dimension(labelSize, tileSize);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Calculate sizes based on current window dimensions
        Dimension sizes = calculateSizes();
        int labelSize = sizes.width;
        int tileSize = sizes.height;

        // Calculate centering offsets
        int totalWidth = 8 * tileSize + 2 * labelSize;
        int totalHeight = 8 * tileSize + 2 * labelSize;
        int xOffset = (getWidth() - totalWidth) / 2;
        int yOffset = (getHeight() - totalHeight) / 2;

        // Draw outer border
        g2d.setColor(BORDER_COLOR);
        g2d.fillRect(xOffset + labelSize - BORDER_THICKNESS,
                yOffset + labelSize - BORDER_THICKNESS,
                8 * tileSize + 2 * BORDER_THICKNESS,
                8 * tileSize + 2 * BORDER_THICKNESS);

        // Draw border background
        g2d.setColor(BORDER_BACKGROUND);
        g2d.fillRect(xOffset + labelSize - (BORDER_THICKNESS - 2),
                yOffset + labelSize - (BORDER_THICKNESS - 2),
                8 * tileSize + 2 * (BORDER_THICKNESS - 2),
                8 * tileSize + 2 * (BORDER_THICKNESS - 2));

        // Draw the board squares
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                drawSquare(g2d, row, col, xOffset + labelSize, yOffset + labelSize, tileSize);
            }
        }

        // Draw coordinates with white color
        g2d.setColor(COORDINATE_TEXT_COLOR);
        int fontSize = Math.min(labelSize - 4, 12); // Reduce font size to ensure it fits
        g2d.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        FontMetrics metrics = g2d.getFontMetrics();

        // Calculate bezel area
        int bezelTop = yOffset + labelSize - BORDER_THICKNESS;
        int bezelBottom = yOffset + labelSize + 8 * tileSize;
        int bezelLeft = xOffset + labelSize - BORDER_THICKNESS;
        int bezelRight = xOffset + labelSize + 8 * tileSize;

        // Draw file letters (a-h)
        for (int col = 0; col < 8; col++) {
            // Adjust column index if board is inverted
            int adjustedCol = isInverted ? 7 - col : col;
            String file = String.valueOf((char) ('a' + adjustedCol));
            int x = xOffset + labelSize + col * tileSize + (tileSize - metrics.stringWidth(file)) / 2;

            // Top bezel
            int topY = bezelTop + (BORDER_THICKNESS + metrics.getAscent()) / 2 - 1;
            g2d.drawString(file, x, topY);

            // Bottom bezel
            int bottomY = bezelBottom + (BORDER_THICKNESS + metrics.getAscent()) / 2 - 1;
            g2d.drawString(file, x, bottomY);
        }

        // Draw rank numbers (1-8)
        for (int row = 0; row < 8; row++) {
            // Adjust rank number if board is inverted
            String rank = isInverted ? String.valueOf(row + 1) : String.valueOf(8 - row);
            int y = yOffset + labelSize + row * tileSize + (tileSize + metrics.getAscent()) / 2;

            // Left bezel
            int textWidth = metrics.stringWidth(rank);
            int leftX = bezelLeft + (BORDER_THICKNESS - textWidth) / 2;
            g2d.drawString(rank, leftX, y);

            // Right bezel
            int rightX = bezelRight + (BORDER_THICKNESS - textWidth) / 2;
            g2d.drawString(rank, rightX, y);
        }

        // Draw pieces
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // Adjust coordinates based on board orientation
                int boardX = isInverted ? i : 7 - i;
                int boardY = isInverted ? 7 - j : j;

                Piece piece = game.getBoard().getPieceAt(boardX, boardY);
                if (piece != null && (!isDragging || !new Position(boardX, boardY).equals(dragStartPosition))) {
                    PieceIcon icon = new PieceIcon(piece, tileSize);
                    icon.paintIcon(this, g2d,
                            j * tileSize + labelSize + xOffset,
                            i * tileSize + labelSize + yOffset);
                }
            }
        }

        // Draw selection highlight
        if (selectedPosition != null) {
            g2d.setColor(SELECTED_COLOR);
            int highlightY = isInverted ? selectedPosition.getX() : 7 - selectedPosition.getX();
            int highlightX = isInverted ? 7 - selectedPosition.getY() : selectedPosition.getY();
            g2d.fillRect(highlightX * tileSize + labelSize + xOffset,
                    highlightY * tileSize + labelSize + yOffset,
                    tileSize, tileSize);
        }

        // Draw valid moves
        if (validMoves != null) {
            g2d.setColor(VALID_MOVE_COLOR);
            for (Move move : validMoves) {
                Position end = move.getEnd();
                int moveY = isInverted ? end.getX() : 7 - end.getX();
                int moveX = isInverted ? 7 - end.getY() : end.getY();
                g2d.fillRect(moveX * tileSize + labelSize + xOffset,
                        moveY * tileSize + labelSize + yOffset,
                        tileSize, tileSize);
            }
        }

        // Draw dragged piece
        if (isDragging && draggedPiece != null && currentMousePosition != null) {
            PieceIcon icon = new PieceIcon(draggedPiece, tileSize);
            icon.paintIcon(this, g2d,
                    currentMousePosition.x - tileSize / 2,
                    currentMousePosition.y - tileSize / 2);
        }
    }

    private void handleMousePress(MouseEvent e) {
        if (!isEnabled())
            return;

        // Check if we're in review mode (either through game state or GUI review mode)
        if (game.isInReviewMode() || gui.isInManualReviewMode()) {
            return; // Prevent moves during review mode
        }

        Dimension sizes = calculateSizes();
        int tileSize = sizes.height; // Only keep tileSize since labelSize isn't used

        int file = e.getX() / tileSize;
        int rank = e.getY() / tileSize;

        // Convert screen coordinates to board coordinates based on orientation
        int boardX = isInverted ? rank : 7 - rank;
        int boardY = isInverted ? 7 - file : file;

        if (boardY < 0 || boardY >= 8 || boardX < 0 || boardX >= 8) {
            selectedPosition = null;
            validMoves = null;
            repaint();
            return;
        }

        Square square = game.getBoard().getSquareAt(boardX, boardY);
        Piece piece = square.getPiece();

        // If a piece is already selected, try to move it
        if (selectedPosition != null && !isDragging) {
            Position end = new Position(boardX, boardY);
            Move moveToMake = null;

            // Find the matching move in validMoves
            if (validMoves != null) {
                for (Move move : validMoves) {
                    if (move.getStart().equals(selectedPosition) && move.getEnd().equals(end)) {
                        moveToMake = move;
                        break;
                    }
                }
            }

            if (moveToMake != null) {
                // Check if this is a promotion move
                if (moveToMake.isPromotion()) {
                    // Show promotion dialog
                    Piece movingPiece = game.getBoard().getPieceAt(selectedPosition.getX(), selectedPosition.getY());
                    PromotionDialog dialog = new PromotionDialog(gui.getGameFrame(), movingPiece.getColor());
                    Piece promotedPiece = dialog.showDialog();

                    if (promotedPiece != null) {
                        // Create a new move with the selected promotion piece
                        Move promotionMove = new Move(selectedPosition, end);
                        promotionMove.setMovingPiece(movingPiece);
                        promotionMove.setPromotedPiece(promotedPiece);

                        // Execute the move
                        if (game.movePiece(promotionMove)) {
                            Move move = game.getLastMove();
                            gui.updateMoveHistory(move);
                        }
                    }
                } else {
                    // Regular move
                    if (game.movePiece(selectedPosition, end)) {
                        Move move = game.getLastMove();
                        gui.updateMoveHistory(move);
                    }
                }

                selectedPosition = null;
                validMoves = null;
            } else {
                selectedPosition = null;
                validMoves = null;
            }
            repaint();
            return;
        }

        // Start selection if clicking on a piece of the current player's color
        if (piece != null && piece.getColor().equals(game.getCurrentPlayer().getColor())) {
            selectedPosition = new Position(boardX, boardY);
            validMoves = filterLegalMoves(piece.calculateLegalMoves(game.getBoard(), selectedPosition));
            mousePressPoint = e.getPoint();
            dragTimer.start();
        }
        repaint();
    }

    private void startDragging() {
        // Don't allow dragging in review mode
        if (game.isInReviewMode() || gui.isInManualReviewMode()) {
            return;
        }

        if (selectedPosition != null && !isDragging) {
            isDragging = true;
            dragStartPosition = selectedPosition;
            draggedPiece = game.getBoard().getPieceAt(selectedPosition.getX(), selectedPosition.getY());
            currentMousePosition = mousePressPoint;
        }
    }

    private void handleMouseDrag(MouseEvent e) {
        if (!isEnabled())
            return;

        // Check if we're in review mode (either through game state or GUI review mode)
        if (game.isInReviewMode() || gui.isInManualReviewMode()) {
            return; // Prevent moves during review mode
        }

        if (draggedPiece != null) {
            currentMousePosition = e.getPoint();
            repaint();
        }
    }

    private void handleMouseRelease(MouseEvent e) {
        if (!isEnabled())
            return;

        // Check if we're in review mode (either through game state or GUI review mode)
        if (game.isInReviewMode() || gui.isInManualReviewMode()) {
            return; // Prevent moves during review mode
        }

        dragTimer.stop();
        if (draggedPiece != null) {
            Dimension sizes = calculateSizes();
            int tileSize = sizes.height;

            int file = e.getX() / tileSize;
            int rank = e.getY() / tileSize;

            // Convert screen coordinates to board coordinates based on orientation
            int boardX = isInverted ? rank : 7 - rank;
            int boardY = isInverted ? 7 - file : file;

            if (boardY >= 0 && boardY < 8 && boardX >= 0 && boardX < 8) {
                Position end = new Position(boardX, boardY);
                Move moveToMake = null;

                // Find the matching move in validMoves
                if (validMoves != null) {
                    for (Move move : validMoves) {
                        if (move.getStart().equals(dragStartPosition) && move.getEnd().equals(end)) {
                            moveToMake = move;
                            break;
                        }
                    }
                }

                if (moveToMake != null) {
                    // Check if this is a promotion move
                    if (moveToMake.isPromotion()) {
                        // Show promotion dialog
                        PromotionDialog dialog = new PromotionDialog(gui.getGameFrame(), draggedPiece.getColor());
                        Piece promotedPiece = dialog.showDialog();

                        if (promotedPiece != null) {
                            // Create a new move with the selected promotion piece
                            Move promotionMove = new Move(dragStartPosition, end);
                            promotionMove.setMovingPiece(draggedPiece);
                            promotionMove.setPromotedPiece(promotedPiece);

                            // Execute the move
                            if (game.movePiece(promotionMove)) {
                                Move move = game.getLastMove();
                                gui.updateMoveHistory(move);
                            }
                        }
                    } else {
                        // Regular move
                        if (game.movePiece(dragStartPosition, end)) {
                            Move move = game.getLastMove();
                            gui.updateMoveHistory(move);
                        }
                    }
                }
            }

            draggedPiece = null;
            dragStartPosition = null;
            selectedPosition = null;
            validMoves = null;
            currentMousePosition = null;
            isDragging = false;
            mousePressPoint = null;
            repaint();
        }
    }

    private void handleMouseMove(MouseEvent e) {
        Dimension sizes = calculateSizes();
        int tileSize = sizes.height;

        int file = e.getX() / tileSize;
        int rank = e.getY() / tileSize;

        // Convert screen coordinates to board coordinates based on orientation
        int boardX = isInverted ? rank : 7 - rank;
        int boardY = isInverted ? 7 - file : file;

        if (boardY < 0 || boardY >= 8 || boardX < 0 || boardX >= 8) {
            if (hoveredPosition != null) {
                hoveredPosition = null;
                repaint();
            }
            return;
        }

        Position newHoveredPosition = new Position(boardX, boardY);
        if (!newHoveredPosition.equals(hoveredPosition)) {
            hoveredPosition = newHoveredPosition;
            repaint();
        }
    }

    public void setGame(ChessGame game) {
        this.game = game;
        this.selectedPosition = null;
        this.validMoves = null;
        this.hoveredPosition = null;
        this.dragStartPosition = null;
        this.draggedPiece = null;
        this.currentMousePosition = null;
        this.isDragging = false;
        this.mousePressPoint = null;
        repaint();
    }

    private void drawSquare(Graphics2D g2d, int row, int col, int xOffset, int yOffset, int tileSize) {
        // Determine square color
        Color squareColor = (row + col) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE;
        g2d.setColor(squareColor);

        // Calculate position
        int x = col * tileSize + xOffset;
        int y = row * tileSize + yOffset;

        // Draw the square
        g2d.fillRect(x, y, tileSize, tileSize);
    }

    @Override
    public Dimension getPreferredSize() {
        // If we're in a container, use its size
        if (getParent() != null) {
            int size = Math.min(getParent().getWidth(), getParent().getHeight());
            return new Dimension(size, size);
        }
        // Default size if not in container
        return new Dimension(800, 800);
    }

    private List<Move> filterLegalMoves(List<Move> moves) {
        List<Move> filteredMoves = new ArrayList<>();
        for (Move move : moves) {
            if (!game.wouldPutKingInCheck(move.getStart(), move.getEnd())) {
                filteredMoves.add(move);
            }
        }
        return filteredMoves;
    }
}
