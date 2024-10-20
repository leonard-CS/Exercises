package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import static processing.core.PApplet.map;

public class Ball {
    private static final float DEFAULT_RADIUS = App.CELLSIZE * 0.5f * 0.75f;
    private static final float MIN_RADIUS = 8;
    private float currentRadius = DEFAULT_RADIUS;
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

    public PVector getPosition() {
        return new PVector(position.x, position.y);
    }

    public void setPosition(int x, int y) {
        position = new PVector(x, y);
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
        checkHoleAttractions(gameBoard);
    }

    private void checkCellCollisions(GameBoard gameBoard) {
        for (int row = 0; row < gameBoard.numRows; row++) {
            for (int col = 0; col < gameBoard.numCols; col++) {
                Cell cell = gameBoard.getCell(row, col);
                if ((cell instanceof WallCell || cell instanceof BrickCell) && checkCellCollision(cell)) {
                    handleCellCollision(cell, gameBoard);
                }
            }
        }
    }

    private void checkLineCollisions(GameBoard gameBoard) {
        for (Line line : gameBoard.getLines()) {
            if (line.getPoints().size() < 2) {
                gameBoard.removeLine(line);
                continue;
            }
            if (checkLineCollision(line)) {
                handleLineCollision(line, gameBoard);
            }
        }
    }

    private void checkHoleAttractions(GameBoard gameBoard) {
        for (HoleCell hole : gameBoard.getHoles()) {
            PVector holeCenter = hole.getCenterPosition();
            float distanceToHole = PVector.dist(holeCenter, position);

            if (distanceToHole < App.CELLSIZE) { // If within attraction range
                PVector attractionForce = PVector.sub(holeCenter, position).mult(0.005f); // 0.5% attraction
                velocity.add(attractionForce);

                // Calculate size reduction based on distance to hole
                float sizeReduction = map(distanceToHole, 0, App.CELLSIZE, 0, DEFAULT_RADIUS - MIN_RADIUS);
                currentRadius = DEFAULT_RADIUS - sizeReduction;

                // Check if the ball is captured by the hole
                if (distanceToHole < currentRadius) {
                    handleHoleCapture(hole, gameBoard);
                    return; // Exit after capture
                }
                return;
            }
        }
        // Reset currentRadius if not near any hole
        currentRadius = DEFAULT_RADIUS;
    }

    private boolean checkCellCollision(Cell cell) {
        PVector cellPosition = cell.getCenterPosition();
        float distance = PVector.dist(cellPosition, position);
        return distance < DEFAULT_RADIUS + (float) App.CELLSIZE / 2;
    }

    private void handleCellCollision(Cell cell, GameBoard gameBoard) {
        if (cell instanceof WallCell) {
            // Change ball color
            WallCell wallCell = (WallCell) cell;
            if (wallCell.getColor() != Color.GREY) {
                color = wallCell.getColor();
                changeImage(gameBoard);
            }
        }

        float cellX = cell.getPosition().x;
        float cellY = cell.getPosition().y;

        float overlapX = 0;
        float overlapY = 0;

        if (position.x < cellX) {
            overlapX = (cellX - position.x) + DEFAULT_RADIUS;
        } else if (position.x > cellX + Cell.CELLSIZE) {
            overlapX = (position.x - (cellX + Cell.CELLSIZE)) + DEFAULT_RADIUS;
        }

        if (position.y < cellY) {
            overlapY = (cellY - position.y) + DEFAULT_RADIUS;
        } else if (position.y > cellY + Cell.CELLSIZE) {
            overlapY = (position.y - (cellY + Cell.CELLSIZE)) + DEFAULT_RADIUS;
        }

        // Resolve the collision based on the smallest overlap
        if (Math.abs(overlapX) > Math.abs(overlapY)) {
            velocity.x = -velocity.x;
            position.x += velocity.x; // Move ball out of the cell
        } else {
            velocity.y = -velocity.y;
            position.y += velocity.y; // Move ball out of the cell
        }

        if (cell instanceof BrickCell) {
            BrickCell brickCell = (BrickCell) cell;
            if (brickCell.getColor() == color || brickCell.getColor() == Color.GREY) {
                brickCell.hit();
            }
            if (brickCell.getLife() < 0) {
                PVector position = brickCell.getPosition();
                int row = (int) ((position.y - App.TOPBAR) / App.CELLSIZE);
                int col = (int) (position.x / App.CELLSIZE);
                gameBoard.setTile(row, col);
            }
        }
    }

    private void changeImage(GameBoard gameBoard) {
        image = gameBoard.getBallImage(color.getValue());
    }

    private boolean checkLineCollision(Line line) {
        PVector closestPoint = line.getClosestPointOnLine(position);
        return PVector.dist(closestPoint, position) < DEFAULT_RADIUS;
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
        PVector closestPoint = line.getClosestPointOnLine(position);
        float overlap = DEFAULT_RADIUS - PVector.dist(position, closestPoint);
        position.add(PVector.mult(normal, overlap));

        gameBoard.removeLine(line);
    }

    private void handleHoleCapture(HoleCell hole, GameBoard gameBoard) {
        // Check for successful capture
        if (hole.getColor() == this.color || hole.getColor() == Color.GREY || this.color == Color.GREY) {
            // Increase score
            gameBoard.increaseScore(color); // Assuming this method exists
            gameBoard.removeBall(this);
        } else {
            // Decrease score
            gameBoard.decreaseScore(color); // Assuming this method exists
            currentRadius = DEFAULT_RADIUS; // Reset radius
            gameBoard.resetBall(this);
        }
    }

    public void draw(PApplet p) {
        int x = (int) (position.x - currentRadius);
        int y = (int) (position.y - currentRadius);
        int diameter = (int) (currentRadius * 2);
        p.image(image, x, y, diameter, diameter);
    }
}
