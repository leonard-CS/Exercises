package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Ball {
    private static final float RADIUS = (float) ((App.CELLSIZE / 2) * 0.75);
    private PImage image;
    private Color color;
    private PVector position;
    private PVector velocity;

    public Ball(int x, int y, PImage ballsImage, inkball.Color color) {
        this.image = ballsImage;
        this.color = color;
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
        position.x -= pixels;
    }

    public void update(GameBoard gameBoard) {
        position.add(velocity);

        // Check for collisions with walls
        checkCellCollisions(gameBoard);

        // Check for collisions with lines
        checkLineCollisions(gameBoard);

        // Check for attraction to holes
//        checkHoleAttractions(gameBoard);

        // Ensure the ball stays within the screen bounds
        if (position.x < RADIUS || position.x > App.WIDTH - RADIUS) {
            velocity.x = -velocity.x;
        }
        if (position.y < RADIUS || position.y > App.HEIGHT - RADIUS) {
            velocity.y = -velocity.y;
        }
    }

    private void checkCellCollisions(GameBoard gameBoard) {
        for (int row = 0; row < gameBoard.numRows; row++) {
            for (int col = 0; col < gameBoard.numCols; col++) {
                Cell cell = gameBoard.getCell(row, col);
                if (cell instanceof WallCell && checkCellCollision(cell)) {
                    handleCellCollision((WallCell) cell, gameBoard);
                }
            }
        }
    }

    private void checkLineCollisions(GameBoard gameBoard) {
        for (Line line : gameBoard.getLines()) {
            if (line.getPoints().size() < 2) {
                continue;
            }
            if (checkLineCollision(line)) {
                handleLineCollision(line, gameBoard);
            }
        }
    }

//    private void checkHoleAttractions(GameBoard gameBoard) {
//        for (int row = 0; row < gameBoard.numRows; row++) {
//            for (int col = 0; col < gameBoard.numCols; col++) {
//                Cell cell = gameBoard.getCell(row, col);
//                if (cell instanceof HoleCell) {
//                    if (checkHoleAttraction((HoleCell) cell)) {
//                        handleHoleAttraction((HoleCell) cell, gameBoard);
//                    }
//                }
//            }
//        }
//    }

    private boolean checkCellCollision(Cell cell) {
        PVector cellPosition = cell.getCenterPosition();
        float distance = PVector.dist(cellPosition, position);
        return distance < RADIUS + (float) App.CELLSIZE / 2;
    }

    private void handleCellCollision(WallCell cell, GameBoard gameBoard) {
        // Change ball color
        if (cell.getColor() != Color.GREY) {
            color = cell.getColor();
            changeImage(gameBoard);
        }

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

    private void changeImage(GameBoard gameBoard) {
        image = gameBoard.getBallImage(color.getValue());
    }

    private boolean checkLineCollision(Line line) {
        PVector closestPoint = line.getClosestPointOnLine(position);
        return PVector.dist(closestPoint, position) < RADIUS;
    }

    private void handleLineCollision(Line line, GameBoard gameBoard) {
        // Bounce the ball off the line
        int indexOfClosestSegment = line.getIndexOfClosestSegment(position);
        PVector lineDir = PVector.sub(line.getPoints().get(indexOfClosestSegment), line.getPoints().get(indexOfClosestSegment + 1));
        lineDir.normalize();

        // Calculate the normal vector
        PVector normal = new PVector(-lineDir.y, lineDir.x);

        // Reflect the velocity
        float dotProduct = velocity.dot(normal);
        velocity.sub(PVector.mult(normal, 2 * dotProduct));

        // Move the ball out of the line
        PVector cloestPoint = line.getClosestPointOnLine(position);
        float overlap = RADIUS - PVector.dist(position, cloestPoint);
        position.add(PVector.mult(normal, overlap));

        gameBoard.removeLine(line);
    }

    public void draw(PApplet p) {
        int x = (int) (position.x - RADIUS);
        int y = (int) (position.y - RADIUS);
        int diameter = (int) (RADIUS * 2);
        p.image(image, x, y, diameter, diameter);
    }
}
