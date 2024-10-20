package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public class BrickCell extends Cell{
    private final PImage[] images;
    private final Color color;
    private int life = 3;
    public BrickCell(int x, int y, PImage[] images, Color color) {
        super(x, y, null);
        this.images = images;
        this.color = color;
    }

    public inkball.Color getColor() {
        return color;
    }

    public int getLife() {
        return life;
    }

    public void hit() {
        life -= 1;
    }

    @Override
    public void draw(PApplet pApplet) {
        pApplet.image(images[1], getPosition().x, getPosition().y, CELLSIZE, CELLSIZE);
    }
}
