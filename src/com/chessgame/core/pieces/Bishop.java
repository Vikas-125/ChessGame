package com.chessgame.core.pieces;

import com.chessgame.core.board.Board;
import com.chessgame.core.board.Position;
import com.chessgame.core.game.Move;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    public Bishop(String color) {
        super(color);
    }

    @Override
    public List<Move> calculateLegalMoves(Board board, Position currentPosition) {
        List<Move> legalMoves = new ArrayList<>();
        int[][] directions = {
            {1, 1}, {-1, -1}, {1, -1}, {-1, 1} // Diagonal
        };

        for (int[] direction : directions) {
            int newX = currentPosition.getX();
            int newY = currentPosition.getY();

            while (true) {
                newX += direction[0];
                newY += direction[1];

                if (!board.isValidPosition(newX, newY)) break;

                Piece targetPiece = board.getPieceAt(newX, newY);
                if (targetPiece == null) {
                    legalMoves.add(new Move(currentPosition, new Position(newX, newY)));
                } else {
                    if (!targetPiece.getColor().equals(this.getColor())) {
                        legalMoves.add(new Move(currentPosition, new Position(newX, newY)));
                    }
                    break;
                }
            }
        }

        return legalMoves;
    }

    @Override
    public Piece copy() {
        return new Bishop(this.getColor());
    }
}

