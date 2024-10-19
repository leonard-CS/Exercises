package inkball;

import processing.core.PImage;

public class WallCell extends Cell {
    private final Color color;

    public WallCell(int x, int y, PImage image, Color color) {
        super(x, y, image);
        this.color = color;
    }

    public inkball.Color getColor() {
        return color;
    }

    public void moveClockwise(int boardWidth, int boardHeight) {
        // Check current position and move to the next one clockwise
        if (position.x < boardWidth - App.CELLSIZE && position.y == App.TOPBAR) {
            position.x += App.CELLSIZE; // Move right
        } else if (position.x >= boardWidth - App.CELLSIZE && position.y < App.TOPBAR + boardHeight - App.CELLSIZE) {
            position.y += App.CELLSIZE; // Move down
        } else if (position.y == boardHeight + App.TOPBAR - App.CELLSIZE && position.x > 0) {
            position.x -= App.CELLSIZE; // Move left
        } else if (position.x == 0 && position.y > App.TOPBAR) {
            position.y -= App.CELLSIZE; // Move up
        }
    }
}
