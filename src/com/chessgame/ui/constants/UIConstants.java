package com.chessgame.ui.constants;

import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Font;

/**
 * Constants for the UI components.
 * Includes dimensions, colors, fonts, and utility methods for UI styling.
 */
public class UIConstants {
    /** Dimensions for UI components */
    public static final int MAIN_WINDOW_WIDTH = 800;
    public static final int MAIN_WINDOW_HEIGHT = 640;
    public static final int NAV_PANEL_WIDTH = 250;

    /** Padding constants for UI components */
    public static final int PADDING_SMALL = 5;
    public static final int PADDING_MEDIUM = 10;
    public static final int PADDING_LARGE = 20;

    /** Color scheme for UI components */
    public static final Color PRIMARY_DARK = new Color(33, 33, 33); // Dark background
    public static final Color PRIMARY_LIGHT = new Color(245, 245, 245); // Light background
    public static final Color ACCENT = new Color(47, 90, 255); // Normal blue
    public static final Color ACCENT_LIGHT = new Color(82, 142, 207); // Light blue
    public static final Color HOVER_DARK = new Color(71, 108, 255); // Darker blue
    public static final Color HOVER_LIGHT = new Color(230, 235, 255); // Light hover color
    public static final Color NAV_BACKGROUND = new Color(51, 51, 51); // Navigation panel background
    public static final Color BUTTON_TEXT_LIGHT = Color.WHITE; // Light text color
    public static final Color BUTTON_TEXT_DARK = new Color(33, 33, 33); // Dark text color
    public static final Color BORDER = new Color(200, 200, 200); // Border color

    /** Fonts for UI components */
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24); // Title text
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18); // Header text
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16); // Button text
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14); // Label text

    /**
     * Creates a styled button with the application's theme.
     *
     * @param text      The text to display on the button
     * @param isPrimary Whether this is a primary button (currently not used)
     * @return A styled JButton
     */
    public static JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(BUTTON_TEXT_LIGHT); // White text
        button.setBackground(ACCENT); // Blue background
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_LIGHT, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        button.setFocusPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(HOVER_DARK); // Darker blue
                button.setForeground(BUTTON_TEXT_LIGHT); // Keep white text
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT); // Normal blue
                button.setForeground(BUTTON_TEXT_LIGHT); // Keep white text
            }
        });

        return button;
    }
}
