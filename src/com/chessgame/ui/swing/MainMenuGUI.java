package com.chessgame.ui.swing;

import com.chessgame.core.game.ChessGame;
import com.chessgame.core.game.GameMode;
import com.chessgame.ui.constants.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenuGUI {
    private final JFrame mainFrame;
    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    
    // Navigation panel items
    private final JPanel navigationPanel;
    private final JButton playButton;
    private final JButton profileButton;
    private final JButton settingsButton;
    private final JButton helpButton;
    private final JButton exitButton;
    
    // Content panels
    private final JPanel playPanel;
    private final JPanel profilePanel;
    private final JPanel settingsPanel;
    private final JPanel helpPanel;
    
    // Add timePanel as a class field
    private final JPanel timePanel;
    private ButtonGroup timeControlGroup;

    public MainMenuGUI() {
        timeControlGroup = new ButtonGroup();
        // Initialize all panels first
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(
            UIConstants.PADDING_MEDIUM,
            UIConstants.PADDING_MEDIUM,
            UIConstants.PADDING_MEDIUM,
            UIConstants.PADDING_MEDIUM
        ));

        // Initialize timePanel before it's used
        timePanel = createTimeControlPanel();

        // Initialize navigation buttons
        playButton = createNavigationButton("Play", "PLAY");
        profileButton = createNavigationButton("Profile", "PROFILE");
        settingsButton = createNavigationButton("Settings", "SETTINGS");
        helpButton = createNavigationButton("Help", "HELP");
        exitButton = createNavigationButton("Exit", null);

        // Create content panels
        playPanel = createPlayPanel();
        profilePanel = createProfilePanel();
        settingsPanel = createSettingsPanel();
        helpPanel = createHelpPanel();

        // Add panels to content panel
        contentPanel.add(playPanel, "PLAY");
        contentPanel.add(profilePanel, "PROFILE");
        contentPanel.add(settingsPanel, "SETTINGS");
        contentPanel.add(helpPanel, "HELP");

        // Create navigation panel
        navigationPanel = createNavigationPanel();

        // Create and setup main frame
        mainFrame = new JFrame("Chess Game");
        setupMainFrame();
    }

    private void setupMainFrame() {
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(UIConstants.MAIN_WINDOW_WIDTH, UIConstants.MAIN_WINDOW_HEIGHT);
        mainFrame.setMinimumSize(new Dimension(800, 640)); // Increased minimum size

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(250); // Increased from 160 to 250
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);

        mainFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = mainFrame.getWidth();
                splitPane.setDividerLocation(Math.min(250, width / 4)); // Increased from 160 to 250
            }
        });

        splitPane.setLeftComponent(navigationPanel);
        splitPane.setRightComponent(contentPanel);

        mainFrame.add(splitPane);
        mainFrame.setLocationRelativeTo(null);
    }

    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.NAV_BACKGROUND); // Dark background
        panel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20)); // Increased padding

        JLabel titleLabel = new JLabel("Chess Game");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE); // Ensure white text
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(30));

        addNavigationButton(panel, playButton);
        panel.add(Box.createVerticalStrut(15));
        addNavigationButton(panel, profileButton);
        panel.add(Box.createVerticalStrut(15));
        addNavigationButton(panel, settingsButton);
        panel.add(Box.createVerticalStrut(15));
        addNavigationButton(panel, helpButton);
        panel.add(Box.createVerticalGlue());
        addNavigationButton(panel, exitButton);

        return panel;
    }

    private JButton createNavigationButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.BUTTON_FONT);
        button.setForeground(Color.BLACK);  // Black text
        button.setBackground(Color.LIGHT_GRAY);  // Light grey background
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.ACCENT_LIGHT, 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(220, 45));
        
        // Add action listener to handle navigation and exit
        button.addActionListener(e -> {
            if (cardName == null) {
                handleExit();  // Called when Exit button is clicked
            } else {
                cardLayout.show(contentPanel, cardName);
                updateNavigationHighlight(button);  // Called when navigation occurs
            }
        });
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(UIConstants.ACCENT);  // Accent color background
                button.setForeground(Color.WHITE);  // White text
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIConstants.ACCENT, 1),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.LIGHT_GRAY);  // Light grey background
                button.setForeground(Color.BLACK);  // Black text
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIConstants.ACCENT_LIGHT, 1),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }
        });
        
        return button;
    }

    private void addNavigationButton(JPanel panel, JButton button) {
        panel.add(button);
        panel.add(Box.createVerticalStrut(UIConstants.PADDING_SMALL));
    }

    private JPanel createPlayPanel() {
        // Create the main container panel that will be returned
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(UIConstants.PRIMARY_LIGHT);

        // Create the content panel that will hold all our components
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UIConstants.PRIMARY_LIGHT);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Game Setup");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Game Mode Panel
        JPanel modePanel = createStyledPanel("Game Mode");
        ButtonGroup modeGroup = new ButtonGroup();
        
        JRadioButton[] modeButtons = {
            new JRadioButton("vs Player (Local)", true),
            new JRadioButton("vs Computer"),
            new JRadioButton("vs Player (Online)")
        };

        for (JRadioButton button : modeButtons) {
            styleRadioButton(button);
            modeGroup.add(button);
            modePanel.add(button);
        }

        contentPanel.add(modePanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Time Control Panel - Modified to show in grid layout
        contentPanel.add(timePanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Color Selection Panel
        JPanel colorPanel = createStyledPanel("Color Preference");
        ButtonGroup colorGroup = new ButtonGroup();
        
        JRadioButton whiteButton = new JRadioButton("White");
        JRadioButton blackButton = new JRadioButton("Black");
        JRadioButton randomButton = new JRadioButton("Random", true); // Set Random as default
        
        JRadioButton[] colorButtons = {whiteButton, blackButton, randomButton};

        for (JRadioButton button : colorButtons) {
            styleRadioButton(button);
            colorGroup.add(button);
            colorPanel.add(button);
        }

        contentPanel.add(colorPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Start Game Button
        JButton startGameButton = UIConstants.createStyledButton("Start Game", true);
        startGameButton.setForeground(Color.BLACK);  // Black text
        startGameButton.setBackground(Color.LIGHT_GRAY);  // Light grey background
        startGameButton.setOpaque(true);
        startGameButton.setContentAreaFilled(true);
        startGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startGameButton.setMaximumSize(new Dimension(200, 40));
        
        startGameButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                startGameButton.setBackground(UIConstants.ACCENT);  // Accent color background
                startGameButton.setForeground(Color.WHITE);  // White text
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                startGameButton.setBackground(Color.LIGHT_GRAY);  // Light grey background
                startGameButton.setForeground(Color.BLACK);  // Black text
            }
        });

        startGameButton.addActionListener(e -> {
            GameMode gameMode = GameMode.LOCAL;
            int minutes = 10;
            int increment = 0;
            String color = "random"; // default
            
            // Get selected game mode
            for (int i = 0; i < modeButtons.length; i++) {
                if (modeButtons[i].isSelected()) {
                    gameMode = i == 0 ? GameMode.LOCAL : 
                              i == 1 ? GameMode.AI : GameMode.ONLINE;
                    break;
                }
            }
            
            // Get selected time control
            int[] timeControl = getSelectedTimeControl();
            minutes = timeControl[0];
            increment = timeControl[1];
            
            // Get selected color
            if (whiteButton.isSelected()) {
                color = "white";
            } else if (blackButton.isSelected()) {
                color = "black";
            } else {
                color = "random";
            }
            
            ChessGame game = new ChessGame();
            game.setGameMode(gameMode);
            game.setPlayerColor(color);
            
            // Remove this line: mainFrame.setVisible(false);
            new GameWindowGUI(game, minutes * 60, increment);
        });

        contentPanel.add(startGameButton);

        // Create scroll pane and add the content panel to it
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Add the scroll pane to the container panel
        containerPanel.add(scrollPane, BorderLayout.CENTER);
        
        return containerPanel;
    }

    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.PRIMARY_LIGHT);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(title),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));
        return panel;
    }

    private void styleRadioButton(JRadioButton button) {
        button.setBackground(UIConstants.PRIMARY_LIGHT);
        button.setFont(UIConstants.LABEL_FONT);
        button.setForeground(UIConstants.PRIMARY_DARK);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Set preferred size for all radio buttons
        button.setPreferredSize(new Dimension(200, 30));
        button.setMaximumSize(new Dimension(200, 30));
        button.setMinimumSize(new Dimension(200, 30));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(240, 240, 240));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(UIConstants.PRIMARY_LIGHT);
                }
            }
        });
    }

    private JPanel createTimeControlPanel() {
        JPanel timePanel = createStyledPanel("Time Control");
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
        timePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(UIConstants.BORDER), "Time Control"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Time control options
        String[][] bulletOptions = {{"1+0", "Bullet (1 min)"}};
        String[][] blitzOptions = {
            {"2+1", "2 min + 1 sec"},
            {"3+0", "3 min"},
            {"3+2", "3 min + 2 sec"},
            {"5+0", "5 min"},
            {"5+3", "5 min + 3 sec"}
        };
        String[][] rapidOptions = {
            {"10+0", "10 min"},
            {"10+5", "10 min + 5 sec"},
            {"15+0", "15 min"},
            {"15+10", "15 min + 10 sec"}
        };
        String[][] classicOptions = {
            {"30+0", "30 min"},
            {"30+20", "30 min + 20 sec"}
        };

        // Create categories with dropdowns
        addTimeControlRow(timePanel, "Bullet", bulletOptions, false);
        timePanel.add(Box.createVerticalStrut(5));
        addTimeControlRow(timePanel, "Blitz", blitzOptions, false);
        timePanel.add(Box.createVerticalStrut(5));
        addTimeControlRow(timePanel, "Rapid", rapidOptions, true);
        timePanel.add(Box.createVerticalStrut(5));
        addTimeControlRow(timePanel, "Classical", classicOptions, false);

        return timePanel;
    }

    private void addTimeControlRow(JPanel panel, String category, String[][] options, boolean isDefault) {
        // Create row panel with FlowLayout for horizontal alignment
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rowPanel.setBackground(UIConstants.PRIMARY_LIGHT);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // Create radio button instead of label
        JRadioButton radioButton = new JRadioButton(category + ":");
        radioButton.setFont(UIConstants.LABEL_FONT);
        radioButton.setBackground(UIConstants.PRIMARY_LIGHT);
        radioButton.setPreferredSize(new Dimension(100, 25));

        // Create combo box
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setFont(UIConstants.LABEL_FONT);
        comboBox.setBackground(UIConstants.PRIMARY_LIGHT);
        comboBox.setPreferredSize(new Dimension(150, 25));

        // Add options to combo box
        for (String[] option : options) {
            comboBox.addItem(option[1]);
        }

        // Initially disable the combo box
        comboBox.setEnabled(false);

        // Set up radio button listener
        radioButton.addActionListener(e -> {
            // Disable all other combo boxes
            for (Component comp : panel.getComponents()) {
                if (comp instanceof JPanel) {
                    JPanel row = (JPanel) comp;
                    for (Component c : row.getComponents()) {
                        if (c instanceof JComboBox) {
                            c.setEnabled(false);
                        }
                    }
                }
            }
            // Enable this combo box
            comboBox.setEnabled(true);
        });

        // Set default selection if specified
        if (isDefault) {
            radioButton.setSelected(true);
            comboBox.setEnabled(true);
        }

        // Add to button group
        if (timeControlGroup == null) {
            timeControlGroup = new ButtonGroup();
        }
        timeControlGroup.add(radioButton);

        // Add components to row
        rowPanel.add(radioButton);
        rowPanel.add(comboBox);

        // Add row to panel
        panel.add(rowPanel);
    }

    private int[] getSelectedTimeControl() {
        // Iterate through all components in the time panel
        for (Component comp : timePanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel rowPanel = (JPanel) comp;
                JRadioButton radioButton = null;
                JComboBox<?> comboBox = null;
                
                // Find the radio button and combo box in this row
                for (Component c : rowPanel.getComponents()) {
                    if (c instanceof JRadioButton) {
                        radioButton = (JRadioButton) c;
                    } else if (c instanceof JComboBox) {
                        comboBox = (JComboBox<?>) c;
                    }
                }
                
                // If this row is selected, get its time control
                if (radioButton != null && radioButton.isSelected() && comboBox != null) {
                    String selectedItem = (String) comboBox.getSelectedItem();
                    if (selectedItem != null) {
                        int minutes = 0;
                        int increment = 0;
                        
                        if (selectedItem.contains("+")) {
                            String[] parts = selectedItem.split("\\+");
                            minutes = Integer.parseInt(parts[0].trim().split(" ")[0]);
                            increment = Integer.parseInt(parts[1].trim().split(" ")[0]);
                        } else {
                            // Handle special cases like "Bullet (1 min)"
                            String[] parts = selectedItem.split("[^0-9]+");
                            for (String part : parts) {
                                if (!part.isEmpty()) {
                                    minutes = Integer.parseInt(part);
                                    break;
                                }
                            }
                        }
                        return new int[]{minutes, increment};
                    }
                }
            }
        }
        return new int[]{10, 0}; // Default to 10 minutes with no increment
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel("Profile Panel (Coming Soon)", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel("Settings Panel (Coming Soon)", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label);
        
        return panel;
    }

    private JPanel createHelpPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Getting Started", createHelpTab(getGettingStartedContent()));
        tabbedPane.addTab("Game Rules", createHelpTab(getGameRulesContent()));
        tabbedPane.addTab("Controls", createHelpTab(getControlsContent()));
        tabbedPane.addTab("FAQ", createHelpTab(getFAQContent()));
        
        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createHelpTab(String content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JTextArea textArea = new JTextArea(content);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private String getGettingStartedContent() {
        return "Welcome to Chess Game!\n\n" +
               "To start playing:\n" +
               "1. Click the 'Play' button in the navigation menu\n" +
               "2. Choose your preferred game mode\n" +
               "3. Select a time control\n" +
               "4. Start playing!\n\n" +
               "You can customize your experience in the Settings menu.";
    }

    private String getGameRulesContent() {
        return "Chess Rules:\n\n" +
               "Basic Movement:\n" +
               "- Pawn: Moves forward one square at a time\n" +
               "- Knight: Moves in an L-shape\n" +
               "- Bishop: Moves diagonally\n" +
               "- Rook: Moves horizontally and vertically\n" +
               "- Queen: Moves in any direction\n" +
               "- King: Moves one square in any direction";
    }

    private String getControlsContent() {
        return "Game Controls:\n\n" +
               "Mouse Controls:\n" +
               "- Click and drag pieces to move them\n" +
               "- Click a piece to select it, then click a destination square";
    }

    private String getFAQContent() {
        return "Frequently Asked Questions:\n\n" +
               "Q: How do I start a new game?\n" +
               "A: Click the 'Play' button and select your preferred game mode.\n\n" +
               "Q: Can I customize the board appearance?\n" +
               "A: Yes, visit the Settings menu to change board and piece themes.";
    }

    private void handleExit() {
        int result = JOptionPane.showConfirmDialog(
            mainFrame,
            "Are you sure you want to exit?",
            "Exit Confirmation",
            JOptionPane.YES_NO_OPTION
        );
        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void updateNavigationHighlight(JButton selectedButton) {
        for (Component c : navigationPanel.getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                if (button == selectedButton) {
                    button.setBackground(UIConstants.ACCENT);  // Highlight with accent color
                    button.setForeground(UIConstants.BUTTON_TEXT_LIGHT);
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIConstants.ACCENT_LIGHT, 1),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                    ));
                } else {
                    button.setBackground(UIConstants.NAV_BACKGROUND);  // Dark background for non-selected
                    button.setForeground(UIConstants.BUTTON_TEXT_LIGHT);
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIConstants.ACCENT_LIGHT, 1),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                    ));
                }
            }
        }
    }

    public void show() {
        mainFrame.setVisible(true);
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }
}
