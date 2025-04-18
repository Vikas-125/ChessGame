package com.chessgame.core.game;

import com.chessgame.core.board.Board;

import java.util.List;

public interface GameState {
    Board getBoard();
    Player getCurrentPlayer();
    List<Move> getMoveHistory();
    boolean isGameOver();
}