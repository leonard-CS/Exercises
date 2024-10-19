package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public class HoleCell extends Cell {
    private final Color color;

    public HoleCell(int x, int y, PImage image, Color color) {
        super(x, y, image);
        this.color = color;
    }

    @Override
    public void draw(PApplet pApplet) {
        pApplet.image(image, getPosition().x, getPosition().y, CELLSIZE*2, CELLSIZE*2);
    }
}
