package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Ball {
    private static final float RADIUS = (float) ((App.CELLSIZE / 2) * 0.75);
    private final PImage image;
    private PVector position;
    private PVector velocity;

    public Ball(PImage ballsImage, int x, int y) {
        this.image = ballsImage;
        this.position = new PVector(x, y);
        this.velocity = new PVector(0, 0);
    }

    public void start(PVector position) {
        this.position = position;
        this.velocity = new PVector(randomV(), randomV());
    }

    private float randomV() {
        return App.random.nextBoolean() ? 2 : -2;
    }

    public void moveLeft(int pixels) {
        position.x = position.x - pixels;
    }

    public void update(GameBoard gameBoard) {
        position.add(velocity);

        // Check for collisions with the game board cells
        for (int row = 0; row < gameBoard.numRows; row++) {
            for (int col = 0; col < gameBoard.numCols; col++) {
                Cell cell = gameBoard.getCell(row, col);
                if (cell instanceof WallCell) {
                    if (checkCollision(cell)) {
//                        velocity.set(0,0);
                        handleCollision(cell);
                    }
                }
            }
        }

        // Ensure the ball stays within the screen bounds
        if (position.x < RADIUS || position.x > App.WIDTH - RADIUS) {
            velocity.x = -velocity.x;
        }
        if (position.y < RADIUS || position.y > App.HEIGHT - RADIUS) {
            velocity.y = -velocity.y;
        }
    }

    private boolean checkCollision(Cell cell) {
        PVector cellPosition = cell.getCenterPosition();
        float distance = PVector.dist(cellPosition, position);
        return distance < RADIUS + App.CELLSIZE/2;
    }

    private void handleCollision(Cell cell) {
        float cellX = cell.getPosition().x;
        float cellY = cell.getPosition().y;

        float overlapX = 0;
        float overlapY = 0;

        if (position.x < cellX) {
            overlapX = (cellX - position.x) + RADIUS;
        } else if (position.x > cellX + Cell.CELLSIZE) {
            overlapX = (position.x - (cellX + Cell.CELLSIZE)) + RADIUS;
        }

        if (position.y < cellY) {
            overlapY = (cellY - position.y) + RADIUS;
        } else if (position.y > cellY + Cell.CELLSIZE) {
            overlapY = (position.y - (cellY + Cell.CELLSIZE)) + RADIUS;
        }

        // Resolve the collision based on the smallest overlap
        if (Math.abs(overlapX) > Math.abs(overlapY)) {
            velocity.x = -velocity.x;
            position.x += velocity.x; // Move ball out of the cell
        } else {
            velocity.y = -velocity.y;
            position.y += velocity.y; // Move ball out of the cell
        }
    }

    public void draw(PApplet p) {
        int x = (int) (position.x - RADIUS);
        int y = (int) (position.y - RADIUS);
        int diameter = (int) (RADIUS * 2);
        p.image(image, x, y, diameter, diameter);
    }
}
