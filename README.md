# Chess Game

A Java-based chess application with a graphical user interface (GUI) built using Swing. This project allows users to play chess with a clean and intuitive interface.

## Features

- Play chess with a friend locally
- Interactive GUI with a modern look and feel
- Full implementation of chess rules including castling, en passant, and pawn promotion
- Move history panel with algebraic notation
- Game state tracking (check, checkmate)
- Piece movement highlighting
- Logging for debugging and monitoring application behavior

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- Visual Studio Code (recommended) or any Java IDE

## Running the Game

### Using Visual Studio Code (Easiest Method)

1. Open the project folder in VS Code
2. Make sure you have the "Extension Pack for Java" installed
3. Click the Run button (▶) in the Run and Debug panel or press F5
4. Select "Launch Chess Game" from the dropdown menu if prompted

### Using Command Line

#### Windows

```
mkdir -p bin
javac -d bin -cp src src/com/chessgame/ChessApplication.java
java -cp bin com.chessgame.ChessApplication
```

#### Mac/Linux

```
mkdir -p bin
javac -d bin -cp src src/com/chessgame/ChessApplication.java
java -cp bin com.chessgame.ChessApplication
```

## Project Structure

- `src/` - Source code
- `src/resources/images/` - Chess piece images
- `bin/` - Compiled class files (created when you build the project)
- `.vscode/` - VS Code configuration files

## Note

Assets: Chess piece images sourced from PNGPlay.com. Used under their terms of service.

## Game Controls

- Click on a piece to select it
- Click on a valid destination square to move the selected piece
- The game enforces all standard chess rules

## Troubleshooting

### Missing Chess Piece Images

If you encounter errors related to missing chess piece images, ensure that:

1. The `src/resources/images/` directory contains all the required image files
2. The image files are named correctly (e.g., `Bishop_Black.png`, `Knight_White.png`, etc.)

### Compilation Issues

If you have trouble compiling the project:

1. Make sure your JDK is properly installed and configured
2. Try cleaning the `bin` directory and recompiling
3. Check for any error messages in the console output
