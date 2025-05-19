/**
 * Provides functionality for animating geometric figures and configure some parameters.
 *
 * This package contains classes responsible for GUI and controls
 *
 * Main class: Creates the user interface and handles user interaction with it;
 * figures/Figure3D class: Provides interface for all figures.
 * figures/* class: Handle figure animations, zooming, speed and other settings.
 *
 *
 * @author Tsitsei Pavlo
 */
package cz.cuni.mff.tsitseip.figures;

import java.awt.*;
import java.util.*;

/**
 * Represents a 3D rotating torus (donut shape) rendered manually using simple rotation
 * and projection math. Implements animation with configurable parameters such as speed,
 * zoom, trail length, and color.
 */

public class Donut3D implements Figure3D {

    // Rotation angles for X, Y, Z axes
    private double A = 0;
    private double B = 0;
    private double C = 0;

    // Rotation speeds for each axis
    private double rotateX = 0.1;
    private double rotateY = 0.1;
    private double rotateZ = 0.1;

    // Transparency factor for motion trails
    private float gamma = 0.9f;

    // Colors for the figure and background
    private Color figureColor = Color.WHITE;
    private Color backgroundColor = Color.BLACK;

    // Constants defining torus shape and perspective projection
    private static final double R1 = 100; // Inner radius
    private static final double R2 = 200; // Outer radius
    private static final double K1 = 1500; // Camera distance
    private static final double K2 = 500; // Projection scale factor

    private double speed = 1;
    private double zoom = 1.0;

    private boolean paused = false;

    // Store recent frames
    private int trailLength = 3;
    private final LinkedList<ArrayList<Point>> previousFrames = new LinkedList<>();

    /**
     * Renders the donut figure on the provided Graphics context.
     *
     * @param g      The Graphics object to draw on.
     * @param width  Width of the canvas.
     * @param height Height of the canvas.
     */
    @Override
    public void render(Graphics g, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;

        // Draw background
        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, width, height);
        if (trailLength == 0) {
            return;
        }

        // Draw previous frames with fading alpha
        int k = 0;
        float alpha = 0;

        for (ArrayList<Point> framePixels : previousFrames) {
            if (trailLength == 1){
                alpha = 1.0f;
            }
            else {
                alpha = gamma * (k / (float) (previousFrames.size() - 1));
            }
            k += 1;
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2.setComposite(ac);
            g2.setColor(figureColor);
            for (Point p : framePixels) {
                g2.fillRect(p.x, p.y, 2, 2);
            }
        }

        // Draw current frame pixels and collect them
        ArrayList<Point> currentPixels = new ArrayList<>();

        int w = width;
        int h = height;

        double[] zBuffer = new double[w * h];
        for (int i = 0; i < zBuffer.length; i++) zBuffer[i] = 0;


        double cosA = Math.cos(A), sinA = Math.sin(A);
        double cosB = Math.cos(B), sinB = Math.sin(B);
        double cosC = Math.cos(C), sinC = Math.sin(C);

        for (double theta = 0; theta < 2 * Math.PI; theta += 0.07) {
            double cosTheta = Math.cos(theta), sinTheta = Math.sin(theta);
            for (double phi = 0; phi < 2 * Math.PI; phi += 0.02) {
                double cosPhi = Math.cos(phi), sinPhi = Math.sin(phi);

                // Parametric point on torus before rotation
                double x = (R2 + R1 * cosTheta) * cosPhi;
                double y = (R2 + R1 * cosTheta) * sinPhi;
                double z = R1 * sinTheta;

                // Applying rotations matrix by Z
                double xz = x * cosC - y * sinC;
                double yz = x * sinC + y * cosC;
                double zz = z;

                // Applying rotations matrix by Y
                double xy = xz * cosB + zz * sinB;
                double yy = yz;
                double zy = -xz * sinB + zz * cosB;

                // Applying rotations matrix by X
                double xr = xy;
                double yr = yy * cosA - zy * sinA;
                double zr = yy * sinA + zy * cosA;

                // Perspective projection
                double zTranslated = zr + K1 / zoom;
                double ooz = 1.0 / zTranslated;
                int xp = (int)(w / 2 + K2 * xr * ooz);
                int yp = (int)(h / 2 - K2 * yr * ooz);
                int idx = xp + w * yp;

                if (idx >= 0 && idx < w * h && ooz > zBuffer[idx]) {
                    zBuffer[idx] = ooz;
                    currentPixels.add(new Point(xp, yp));
                }
            }
        }

        // Render current frame with full opacity
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2.setColor(figureColor);
        for (Point p : currentPixels) {
            g2.fillRect(p.x, p.y, 2, 2);
        }

        // Add current frame pixels to the trail list
        previousFrames.addLast(currentPixels);
        while (previousFrames.size() > trailLength+1) {
            previousFrames.removeFirst();
        }
    }

    /**
     * Sets the base speed multiplier of the rotation.
     *
     * @param speed Rotation speed multiplier.
     */
    @Override
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Sets the color used to draw the figure.
     *
     * @param color New color for the figure.
     */
    @Override
    public void setFigureColor(Color color) {
        this.figureColor = color;
    }

    /**
     * Sets the background color behind the figure.
     *
     * @param color New background color.
     */
    @Override
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    /**
     * Sets the zoom level of the figure.
     *
     * @param zoom New zoom factor (1.0 = default).
     */
    @Override
    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    /**
     * Sets the transparency factor for fading trail effect.
     *
     * @param gamma Transparency gamma (0.0 - 1.0).
     */
    @Override
    public void setGamma(float gamma){
        this.gamma = gamma;
    }

    /**
     * Returns the current zoom value.
     *
     * @return Current zoom level.
     */
    public double getZoom() {
        return zoom;
    }

    /**
     * Returns the current rotation speed.
     *
     * @return Current speed multiplier.
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Toggles the animation between paused and running.
     */
    @Override
    public void togglePause() {
        paused = !paused;
    }

    /**
     * Sets the rotation rate around the X-axis.
     *
     * @param rotateX Rotation increment per update for X-axis.
     */
    @Override
    public void setRotateX(double rotateX){
        this.rotateX = rotateX;
    }

    /**
     * Sets the rotation rate around the Y-axis.
     *
     * @param rotateY Rotation increment per update for Y-axis.
     */
    @Override
    public void setRotateY(double rotateY){
        this.rotateY = rotateY;
    }

    /**
     * Sets the rotation rate around the Z-axis.
     *
     * @param rotateZ Rotation increment per update for Z-axis.
     */
    @Override
    public void setRotateZ(double rotateZ){
        this.rotateZ = rotateZ;
    }

    /**
     * Updates the rotation state based on current speed and rotation deltas.
     * Should be called each animation frame.
     */
    @Override
    public void update() {
        if (!paused) {
            A += rotateX * speed;
            B += rotateY * speed;
            C += rotateZ * speed;
        }
    }

    /**
     * Sets how many previous frames are remembered for trail rendering.
     *
     * @param trailLength Number of previous frames to retain.
     */
    @Override
    public void setTrailLength(int TrailLength) {
        this.trailLength = TrailLength;
    }
}
