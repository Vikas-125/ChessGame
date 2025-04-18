@echo off
echo Chess Game Launcher
echo ==================
echo.

echo Creating bin directory if it doesn't exist...
if not exist bin mkdir bin

echo Compiling the game...
javac -d bin -cp src src/com/chessgame/ChessApplication.java

if %errorlevel% neq 0 (
    echo.
    echo Compilation failed! Please check the error messages above.
    echo.
    pause
    exit /b %errorlevel%
)

echo.
echo Launching the game...
echo.
java -cp bin com.chessgame.ChessApplication

if %errorlevel% neq 0 (
    echo.
    echo Game exited with an error! Please check the error messages above.
    echo.
    pause
)

exit /b 0
