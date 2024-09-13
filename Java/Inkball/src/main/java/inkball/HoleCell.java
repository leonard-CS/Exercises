package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public class HoleCell extends Cell {
    public HoleCell(PImage image) {
        super(image);
    }

    @Override
    public void draw(PApplet pApplet, int row, int col) {
        int x = col * CELLSIZE;
        int y = row * CELLSIZE + App.TOPBAR;
        pApplet.image(image, x, y, CELLSIZE*2, CELLSIZE*2);
    }
}
