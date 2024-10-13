package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public abstract class Cell {
    public static final int CELLSIZE = App.CELLSIZE;
    protected final PImage image;
    private final PVector position;

    // Constructor to set the cell size
    public Cell(PImage image, int x, int y) {
        this.image = image;
        this.position = new PVector(x, y);
    }

    public PVector getPosition() {
        return new PVector(position.x, position.y);
    }

    public PVector getCenterPosition() {
        int offset = CELLSIZE / 2;
        return new PVector(position.x+offset, position.y+offset);
    }

    public void draw(PApplet pApplet) {
        pApplet.image(image, position.x, position.y, CELLSIZE, CELLSIZE);
    }
}
