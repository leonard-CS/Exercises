package inkball;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Line {
    private static final float LINE_THICKNESS = 10;
    private final ArrayList<PVector> points = new ArrayList<>();

    public Line(float x, float y) {
        points.add(new PVector(x, y));
    }

    public void addPoint(int x, int y) {
        points.add(new PVector(x, y));
    }

    public ArrayList<PVector> getPoints() {
        return new ArrayList<>(points);
    }

    public void draw(PApplet p) {
        p.stroke(0);
        p.strokeWeight(LINE_THICKNESS);
        p.noFill();

        p.beginShape();
        for (PVector point : points) {
            p.curveVertex(point.x, point.y);
        }
        p.endShape();
    }

    public boolean isMouseNear(int mouseX, int mouseY) {
        for (PVector point : points) {
            float distance = PVector.dist(new PVector(mouseX, mouseY), point);
            if (distance < App.COLLISION_THRESHOLD) {
                return true;
            }
        }
        return false;
    }

    private PVector getClosestPointOnSegment(PVector pointA, PVector pointB, PVector targetPoint) {
        PVector vectorToA = PVector.sub(targetPoint, pointA);
        PVector lineDir = PVector.sub(pointB, pointA);
        float lineLength = lineDir.mag();
        lineDir.normalize();

        float projection = vectorToA.dot(lineDir);
        // Clamp projection to [0, lineLength]
        projection = Math.max(0, Math.min(projection, lineLength));

        // Return the closest point on the line segment
        return PVector.add(pointA, PVector.mult(lineDir, projection));
    }

    public PVector getClosestPointOnLine(PVector targetPoint) {
        return getClosestPointAndIndex(targetPoint)[0];
    }

    public int getIndexOfClosestSegment(PVector targetPoint) {
        return (int) getClosestPointAndIndex(targetPoint)[1].x;
    }

    private PVector[] getClosestPointAndIndex(PVector targetPoint) {
        PVector closestPoint = null;
        int closestIndex = -1;
        float minDistance = Float.MAX_VALUE;

        for (int i = 0; i < points.size() - 1; i++) {
            PVector pointA = points.get(i);
            PVector pointB = points.get(i + 1);
            PVector segmentClosestPoint = getClosestPointOnSegment(pointA, pointB, targetPoint);
            float distance = PVector.dist(segmentClosestPoint, targetPoint);

            if (distance < minDistance) {
                minDistance = distance;
                closestPoint = segmentClosestPoint;
                closestIndex = i; // Store the index of the closest segment
            }
        }

        return new PVector[] { closestPoint, new PVector(closestIndex, 0) }; // Use a PVector to return both values
    }
}
