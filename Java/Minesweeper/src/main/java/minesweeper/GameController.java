package minesweeper;

import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class GameController {
    private GameBoard gameBoard;

    public GameController(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public void handleMousePress(MouseEvent e) {
        // Handle mouse press events
    }

    public void handleKeyPress(KeyEvent e) {
        // Handle key press events
    }

    public void revealCell(int row, int col) {
        Cell cell = this.gameBoard.getCell(row, col);
        cell.isRevealed = true;
    }
}
