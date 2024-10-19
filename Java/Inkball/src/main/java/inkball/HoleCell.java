package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class HoleCell extends Cell {
    private final Color color;

    public HoleCell(int x, int y, PImage image, Color color) {
        super(x, y, image);
        this.color = color;
    }

    @Override
    public PVector getCenterPosition() {
        int offset = CELLSIZE;
        return new PVector(position.x+offset, position.y+offset);
    }

    @Override
    public void draw(PApplet pApplet) {
        pApplet.image(image, getPosition().x, getPosition().y, CELLSIZE*2, CELLSIZE*2);
    }

    public inkball.Color getColor() {
        return color;
    }
}
