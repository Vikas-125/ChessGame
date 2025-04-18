package com.chessgame.ui.swing;

import com.chessgame.core.game.ChessGame;
import com.chessgame.core.game.Move;
import com.chessgame.core.pieces.Piece;
import com.chessgame.core.pieces.King;
import com.chessgame.core.pieces.Queen;
import com.chessgame.core.pieces.Rook;
import com.chessgame.ui.constants.UIConstants;
import com.chessgame.core.pieces.Bishop;
import com.chessgame.core.pieces.Knight;
import com.chessgame.core.pieces.Pawn;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class MoveHistoryPanel extends JPanel {
    private JTable moveHistoryTable;
    private DefaultTableModel tableModel;
    private List<Move> moves;
    private ChessGame game;
    private BoardPanel boardPanel;
    private static final Color HIGHLIGHT_COLOR = new Color(82, 142, 207, 80);
    private static final Color HEADER_BG_COLOR = new Color(82, 142, 207);
    private static final Color ALTERNATE_ROW_COLOR = new Color(240, 240, 240);

    public MoveHistoryPanel(ChessGame game, BoardPanel boardPanel) {
        this.game = game;
        this.boardPanel = boardPanel;
        this.moves = new ArrayList<>();

        // Set layout with padding
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(UIConstants.PRIMARY_LIGHT);

        // Create table model with 3 columns
        String[] columnNames = { "#", "White", "Black" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create move history table
        moveHistoryTable = new JTable(tableModel);
        moveHistoryTable.setDefaultRenderer(Object.class, new ModernMoveTableCellRenderer());
        moveHistoryTable.setShowHorizontalLines(true);
        moveHistoryTable.setShowVerticalLines(false);
        moveHistoryTable.setIntercellSpacing(new Dimension(10, 1));
        moveHistoryTable.setRowHeight(30);
        moveHistoryTable.setFillsViewportHeight(true);

        // Style the header
        JTableHeader header = moveHistoryTable.getTableHeader();
        header.setBackground(Color.LIGHT_GRAY); // Light grey background
        header.setForeground(Color.BLACK); // Black text
        header.setFont(UIConstants.BUTTON_FONT);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.ACCENT_LIGHT, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        // Add hover effect to header
        header.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                header.setBackground(UIConstants.ACCENT); // Accent color background
                header.setForeground(Color.WHITE); // White text
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                header.setBackground(Color.LIGHT_GRAY); // Light grey background
                header.setForeground(Color.BLACK); // Black text
            }
        });

        // Set column widths
        moveHistoryTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        moveHistoryTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        moveHistoryTable.getColumnModel().getColumn(2).setPreferredWidth(100);

        // Style the table
        moveHistoryTable.setSelectionBackground(UIConstants.HOVER_LIGHT);
        moveHistoryTable.setSelectionForeground(UIConstants.BUTTON_TEXT_DARK);
        moveHistoryTable.setBackground(UIConstants.PRIMARY_LIGHT);
        moveHistoryTable.setForeground(UIConstants.PRIMARY_DARK);
        moveHistoryTable.setGridColor(UIConstants.BORDER);

        // Prevent column resizing/reordering
        moveHistoryTable.getTableHeader().setReorderingAllowed(false);
        moveHistoryTable.getTableHeader().setResizingAllowed(false);

        // Create a custom scroll pane
        JScrollPane scrollPane = new JScrollPane(moveHistoryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER, 1));
        scrollPane.getViewport().setBackground(UIConstants.PRIMARY_LIGHT);

        // Add title label
        JLabel titleLabel = new JLabel("Move History");
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setForeground(HEADER_BG_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Add components to panel
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Set preferred size
        setPreferredSize(new Dimension(300, 400));

        // Add mouse listener to table for move navigation
        moveHistoryTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = moveHistoryTable.rowAtPoint(e.getPoint());
                int col = moveHistoryTable.columnAtPoint(e.getPoint());

                if (row >= 0 && col > 0) { // Ignore clicks on move number column (col 0)
                    int moveIndex = (row * 2) + (col - 1);
                    if (moveIndex < moves.size()) {
                        navigateToMove(moveIndex);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    // Removed duplicate header initialization

    private class ModernMoveTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Set alignment
            setHorizontalAlignment(column == 0 ? CENTER : LEFT);

            // Get row and column under mouse
            Point mousePosition = table.getMousePosition();
            boolean isHovered = false;
            if (mousePosition != null) {
                int hoveredRow = table.rowAtPoint(mousePosition);
                int hoveredCol = table.columnAtPoint(mousePosition);
                isHovered = hoveredRow == row && hoveredCol == column && column > 0;
            }

            // Set colors
            if (isSelected && table.getSelectedColumn() == column) {
                c.setBackground(HIGHLIGHT_COLOR);
                c.setForeground(UIConstants.PRIMARY_DARK);
            } else if (isHovered) {
                c.setBackground(new Color(HIGHLIGHT_COLOR.getRed(),
                        HIGHLIGHT_COLOR.getGreen(),
                        HIGHLIGHT_COLOR.getBlue(),
                        40)); // Lighter highlight for hover
                c.setForeground(UIConstants.PRIMARY_DARK);
            } else {
                c.setBackground(row % 2 == 0 ? UIConstants.PRIMARY_LIGHT : ALTERNATE_ROW_COLOR);
                c.setForeground(UIConstants.PRIMARY_DARK);
            }

            // Highlight current move
            int currentMoveIndex = game.getCurrentMoveIndex();
            int moveNumberForCell = (row * 2) + (column - 1);

            if (column > 0 && moveNumberForCell == currentMoveIndex) {
                c.setBackground(HIGHLIGHT_COLOR);
                setFont(table.getFont().deriveFont(Font.BOLD));
            } else {
                setFont(table.getFont());
            }

            // Add padding
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

            // Add hand cursor for clickable moves
            table.setCursor(column > 0 ? new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));

            return c;
        }
    }

    public void addMove(Move move) {
        moves.add(move);
        updateTable();

        // Scroll to the last row
        int lastRow = moveHistoryTable.getRowCount() - 1;
        if (lastRow >= 0) {
            Rectangle rect = moveHistoryTable.getCellRect(lastRow, 0, true);
            moveHistoryTable.scrollRectToVisible(rect);
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (int i = 0; i < moves.size(); i += 2) {
            String moveNumber = ((i / 2) + 1) + ".";
            String whiteMove = formatMove(moves.get(i));
            String blackMove = (i + 1 < moves.size()) ? formatMove(moves.get(i + 1)) : "";
            tableModel.addRow(new Object[] { moveNumber, whiteMove, blackMove });
        }
        moveHistoryTable.repaint();
    }

    public void highlightCurrentMove(int moveIndex) {
        moveHistoryTable.repaint();

        if (moveIndex >= -1 && moveIndex < moves.size()) {
            if (moveIndex == -1) {
                moveHistoryTable.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
            } else {
                int row = moveIndex / 2;
                int col = (moveIndex % 2) + 1;
                Rectangle cellRect = moveHistoryTable.getCellRect(row, col, true);
                moveHistoryTable.scrollRectToVisible(cellRect);
            }
        }
    }

    private void navigateToMove(int targetIndex) {
        if (targetIndex < -1 || targetIndex >= moves.size())
            return;

        // Use the new resetAndReplayMoves method for more reliable navigation
        if (targetIndex == -1) {
            // If navigating to the start, just reset the game
            game.reset();
        } else {
            // Set the current move index to the target and replay all moves
            game.setCurrentMoveIndex(targetIndex);
            game.resetAndReplayMoves();
        }

        boardPanel.repaint();
        moveHistoryTable.repaint();
    }

    public void clear() {
        moves.clear();
        tableModel.setRowCount(0);
        moveHistoryTable.repaint();
    }

    public void setGame(ChessGame game) {
        this.game = game;
        clear(); // Clear the move history when setting a new game
    }

    private String formatMove(Move move) {
        if (move == null || move.getMovingPiece() == null) {
            return "";
        }

        StringBuilder notation = new StringBuilder();
        Piece piece = move.getMovingPiece();

        // Add piece letter (except for pawns)
        if (!(piece instanceof Pawn)) {
            notation.append(getPieceLetter(piece));
        }

        // Handle castling
        if (move.isCastling()) {
            return move.getEnd().getY() > move.getStart().getY() ? "O-O" : "O-O-O";
        }

        // Add capture symbol
        if (piece instanceof Pawn && move.getStart().getY() != move.getEnd().getY()) {
            notation.append(getFileNotation(move.getStart().getY())).append("x");
        } else if (move.getCapturedPiece() != null) {
            notation.append("x");
        }

        // Add destination square
        notation.append(getFileNotation(move.getEnd().getY()))
                .append(getRankNotation(move.getEnd().getX()));

        // Add check or checkmate symbol
        if (move.isCheckmate()) {
            notation.append("#");
        } else if (move.isCheck()) {
            notation.append("+");
        }

        return notation.toString();
    }

    private String getPieceLetter(Piece piece) {
        if (piece instanceof King)
            return "K";
        if (piece instanceof Queen)
            return "Q";
        if (piece instanceof Rook)
            return "R";
        if (piece instanceof Bishop)
            return "B";
        if (piece instanceof Knight)
            return "N";
        return "";
    }

    private String getFileNotation(int y) {
        return String.valueOf((char) ('a' + y));
    }

    private String getRankNotation(int x) {
        return String.valueOf(x + 1);
    }
}
