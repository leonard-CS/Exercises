package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public class Ball {
    private static final float BALL_SIZE = (float) (App.CELLSIZE * 0.75);
    private final PImage image;
    private int x, y;

    public Ball(PImage ballsImage, int x, int y) {
        image = ballsImage;
        this.x = x;
        this.y = y;
    }

    public void draw(PApplet p) {
        int radius = (int) (BALL_SIZE/2);
        p.image(image, x-radius, y-radius, BALL_SIZE, BALL_SIZE);
    }
}
