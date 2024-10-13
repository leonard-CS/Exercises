package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Ball {
    private static final float RADIUS = (float) ((App.CELLSIZE / 2) * 0.75);
    private final PImage image;
    private PVector position;
    private PVector velocity;

    public Ball(PImage ballsImage, int x, int y) {
        this.image = ballsImage;
        this.position = new PVector(x, y);
        this.velocity = new PVector(0, 0);
    }

    public void start(PVector position) {
        this.position = position;
        this.velocity = new PVector(randomV(), randomV());
    }

    private float randomV() {
        return App.random.nextBoolean() ? 2 : -2;
    }

    public void moveLeft(int pixels) {
        position.x = position.x - pixels;
    }

    public void update() {
        position.add(velocity);

        // Ensure the ball stays within the screen bounds
        if (position.x < RADIUS || position.x > App.WIDTH - RADIUS) {
            velocity.x = -velocity.x;
        }
        if (position.y < RADIUS || position.y > App.HEIGHT - RADIUS) {
            velocity.y = -velocity.y;
        }
    }

    public void draw(PApplet p) {
        int x = (int) (position.x - RADIUS);
        int y = (int) (position.y - RADIUS);
        int diameter = (int) (RADIUS * 2);
        p.image(image, x, y, diameter, diameter);
    }
}
