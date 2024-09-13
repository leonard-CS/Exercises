package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public abstract class Cell {
    protected final PImage image;
    public static final int CELLSIZE = App.CELLSIZE;

    // Constructor to set the cell size
    public Cell(PImage image) {
        this.image = image;
    }

    // Abstract draw method
    public abstract void draw(PApplet pApplet, int row, int col);
}
