package com.chessgame.ui.swing;

import javax.swing.*;

import com.chessgame.core.pieces.Piece;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Icon implementation for chess pieces.
 * Loads and caches piece images for efficient rendering.
 */
public class PieceIcon implements Icon {
    /** Cache of loaded piece images to avoid reloading */
    private static Map<String, Image> pieceImages = new HashMap<>();

    /** The chess piece this icon represents */
    private final Piece piece;

    /** The size of the icon in pixels */
    private final int size;

    /**
     * Creates a new piece icon.
     *
     * @param piece The chess piece to create an icon for
     * @param size  The size of the icon in pixels
     */
    public PieceIcon(Piece piece, int size) {
        this.piece = piece;
        this.size = size;
    }

    /**
     * Paints the piece icon at the specified location.
     * Loads the image if it hasn't been loaded yet.
     *
     * @param c The component to paint on
     * @param g The graphics context
     * @param x The x-coordinate to paint at
     * @param y The y-coordinate to paint at
     */
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        String imageKey = getImageKey(piece);
        Image image = pieceImages.get(imageKey);

        if (image == null) {
            try {
                image = loadImage(imageKey);
                pieceImages.put(imageKey, image);
            } catch (IOException e) {
                System.err.println("Failed to load image: " + imageKey);
                e.printStackTrace();
            }
        }

        if (image != null) {
            g.drawImage(image, x, y, size, size, null);
        }
    }

    /**
     * Gets the width of the icon.
     *
     * @return The width in pixels
     */
    @Override
    public int getIconWidth() {
        return size;
    }

    /**
     * Gets the height of the icon.
     *
     * @return The height in pixels
     */
    @Override
    public int getIconHeight() {
        return size;
    }

    /**
     * Generates a key for the image cache based on the piece type and color.
     *
     * @param piece The chess piece
     * @return A string key in the format "PieceType_Color.png"
     */
    private String getImageKey(Piece piece) {
        String pieceName = piece.getClass().getSimpleName();
        String color = piece.getColor();
        return pieceName + "_" + color + ".png";
    }

    /**
     * Loads an image for a chess piece.
     * Tries multiple locations to find the image.
     *
     * @param fileName The filename of the image
     * @return The loaded image
     * @throws IOException If the image cannot be loaded
     */
    private Image loadImage(String fileName) throws IOException {
        // Try direct file system access first since we know the exact path
        java.io.File file = new java.io.File("src/resources/images/" + fileName);
        if (file.exists()) {
            return new ImageIcon(file.getAbsolutePath()).getImage();
        }

        // Try loading from classpath as fallback
        java.net.URL imageURL = getClass().getResource("/images/" + fileName);
        if (imageURL == null) {
            imageURL = getClass().getResource("/resources/images/" + fileName);
        }
        if (imageURL == null) {
            imageURL = getClass().getResource("/src/resources/images/" + fileName);
        }

        if (imageURL != null) {
            ImageIcon icon = new ImageIcon(imageURL);
            if (icon.getImageLoadStatus() != MediaTracker.ERRORED) {
                return icon.getImage();
            }
        }

        // If we get here, we couldn't find the image
        System.err.println("Attempted to load image from: " + file.getAbsolutePath());
        throw new IOException("Image not found: " + fileName);
    }
}
