package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Ball {
    private static final float RADIUS = (float) ((App.CELLSIZE / 2) * 0.75);
    private final PImage image;
    private final PVector position;

    public Ball(PImage ballsImage, int x, int y) {
        this.image = ballsImage;
        this.position = new PVector(x, y);
    }

    public void moveLeft(int pixels) {
        position.x = position.x - pixels;
    }

    public void draw(PApplet p) {
        int x = (int) (position.x - RADIUS);
        int y = (int) (position.y - RADIUS);
        int diameter = (int) (RADIUS * 2);
        p.image(image, x, y, diameter, diameter);
    }
}
