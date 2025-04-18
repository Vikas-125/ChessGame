package com.chessgame.core.pieces;

import com.chessgame.core.board.Board;
import com.chessgame.core.board.Position;
import com.chessgame.core.game.Move;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    public Knight(String color) {
        super(color);
    }

    @Override
    public List<Move> calculateLegalMoves(Board board, Position currentPosition) {
        List<Move> legalMoves = new ArrayList<>();
        int[][] moveOffsets = {
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] offset : moveOffsets) {
            int newX = currentPosition.getX() + offset[0];
            int newY = currentPosition.getY() + offset[1];

            if (board.isValidPosition(newX, newY)) {
                Piece targetPiece = board.getPieceAt(newX, newY);
                if (targetPiece == null || !targetPiece.getColor().equals(this.getColor())) {
                    legalMoves.add(new Move(currentPosition, new Position(newX, newY)));
                }
            }
        }

        return legalMoves;
    }

    @Override
    public Piece copy() {
        return new Knight(this.getColor());
    }
}

