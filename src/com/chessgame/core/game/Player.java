package com.chessgame.core.game;

/**
 * Represents a chess player.
 * Each player has a color (White or Black).
 */
public class Player {
    /** The color of the player's pieces ("White" or "Black") */
    private final String color;

    /**
     * Creates a new player with the specified color.
     *
     * @param color The color of the player's pieces ("White" or "Black")
     */
    public Player(String color) {
        this.color = color;
    }

    /**
     * Gets the color of the player's pieces.
     *
     * @return The color ("White" or "Black")
     */
    public String getColor() {
        return color;
    }
}