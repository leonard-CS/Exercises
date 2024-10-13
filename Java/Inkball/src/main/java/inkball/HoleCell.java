package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public class HoleCell extends Cell {
    public HoleCell(PImage image, int x, int y) {
        super(image, x, y);
    }

    @Override
    public void draw(PApplet pApplet) {
        pApplet.image(image, getPosition().x, getPosition().y, CELLSIZE*2, CELLSIZE*2);
    }
}
