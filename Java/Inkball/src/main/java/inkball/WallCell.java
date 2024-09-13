package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public class WallCell extends Cell {
    public WallCell(PImage image) {
        super(image);
    }

    @Override
    public void draw(PApplet pApplet, int row, int col) {
        int x = col * CELLSIZE;
        int y = row * CELLSIZE + App.TOPBAR;
        pApplet.image(image, x, y, CELLSIZE, CELLSIZE);
    }
}
