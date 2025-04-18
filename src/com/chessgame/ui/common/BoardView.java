package com.chessgame.ui.common;

import com.chessgame.core.board.Board;
import com.chessgame.core.board.Position;
import com.chessgame.core.pieces.Piece;
import java.awt.Point;
import java.util.List;

/**
 * Interface for chess board views.
 * Defines methods that any board view implementation must provide.
 */
public interface BoardView {
    /**
     * Updates the view with the current board state.
     *
     * @param board The current board state
     */
    void updateBoard(Board board);

    /**
     * Highlights the specified squares on the board.
     *
     * @param positions The positions to highlight
     */
    void highlightSquares(List<Position> positions);

    /**
     * Shows a piece being dragged at the specified location.
     *
     * @param piece    The piece being dragged
     * @param location The current mouse location
     */
    void showPieceDrag(Piece piece, Point location);
}