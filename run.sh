#!/bin/bash

echo "Chess Game Launcher"
echo "=================="
echo

echo "Creating bin directory if it doesn't exist..."
mkdir -p bin

echo "Compiling the game..."
javac -d bin -cp src src/com/chessgame/ChessApplication.java

if [ $? -ne 0 ]; then
    echo
    echo "Compilation failed! Please check the error messages above."
    echo
    read -p "Press Enter to exit..."
    exit 1
fi

echo
echo "Launching the game..."
echo
java -cp bin com.chessgame.ChessApplication

if [ $? -ne 0 ]; then
    echo
    echo "Game exited with an error! Please check the error messages above."
    echo
    read -p "Press Enter to exit..."
fi

exit 0
