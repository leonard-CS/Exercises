package minesweeper;

import org.checkerframework.checker.units.qual.min;

import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class GameController {
    private GameBoard gameBoard;

    public GameController(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public void revealCell(int row, int col) {
        Cell cell = gameBoard.getCell(row, col);

        if (cell.isRevealed() || cell.isFlagged()) {
            return; // No action if the cell is already revealed or flagged
        }

        if (cell.reveal()) {
            gameBoard.revealCount++;
        }

        if (cell.isMine()) {
            cell.setExploded(true);
            gameBoard.setGameOver(true);
            setupMinesToExplodeList(cell);
        } else if (cell.getNeighboringMines() == 0) {
            revealAdjacentCells(row, col);
        }
    }

    private void setupMinesToExplodeList(Cell firstMine) {
        for (int i = 0; i < gameBoard.minesToExplode.size(); i++) {
            Cell mine = gameBoard.minesToExplode.get(i);
            if (mine.xPos == firstMine.xPos && mine.yPos == firstMine.yPos) {
                gameBoard.minesToExplode.remove(i);
                gameBoard.minesToExplode.add(0, firstMine);
                return;
            }
        }
    }

    private void revealAdjacentCells(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
    
                if (isValidPosition(newRow, newCol)) {
                    Cell adjacentCell = gameBoard.getCell(newRow, newCol);
    
                    if (!adjacentCell.isRevealed() && !adjacentCell.isMine()) {
                        revealCell(newRow, newCol); // Recursively reveal adjacent cells
                    }
                }
            }
        }
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < gameBoard.getRows() && col >= 0 && col < gameBoard.getCols();
    }
}
