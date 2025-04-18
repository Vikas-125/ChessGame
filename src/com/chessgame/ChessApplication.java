package com.chessgame;

import com.chessgame.ui.swing.MainMenuGUI;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Main application class for the Chess Game.
 * Serves as the entry point and manages the application lifecycle.
 */
public class ChessApplication {
    /** Logger for application events */
    private static final Logger LOGGER = Logger.getLogger(ChessApplication.class.getName());

    /** Main GUI instance */
    private static MainMenuGUI mainGui = null;

    /**
     * Application entry point.
     * Initializes the UI and shows the main window.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            LOGGER.info("Starting Chess Application...");

            SwingUtilities.invokeLater(() -> {
                try {
                    showMainWindow();
                    mainGui.show();
                    LOGGER.info("Main window initialized successfully");
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to initialize main window", e);
                    System.exit(1);
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Application startup failed", e);
            System.exit(1);
        }
    }

    /**
     * Shows the main application window.
     * Creates a new instance if one doesn't exist, otherwise brings the existing
     * window to front.
     */
    public static void showMainWindow() {
        if (mainGui == null) {
            LOGGER.info("Creating new MainMenuGUI instance");
            mainGui = new MainMenuGUI();
        } else {
            LOGGER.fine("Bringing existing window to front");
            mainGui.getMainFrame().toFront();
        }
    }

    /**
     * Gets the main GUI instance.
     *
     * @return The MainMenuGUI instance
     */
    public static MainMenuGUI getMainGui() {
        return mainGui;
    }

    /**
     * Shuts down the application.
     * Performs any necessary cleanup before exiting.
     */
    public static void shutdown() {
        LOGGER.info("Shutting down Chess Application");
        // Add any cleanup code here
        System.exit(0);
    }
}
