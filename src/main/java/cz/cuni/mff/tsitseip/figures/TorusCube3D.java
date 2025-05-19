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
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a 3D rotating torus and cube rendered manually using simple rotation
 * and projection math. Implements animation with configurable parameters such as speed,
 * zoom, trail length, and color.
 */
public class TorusCube3D implements Figure3D {
    // Rotation angles for X, Y, Z axes
    private double torusAngleX = 0, torusAngleY = 0, torusAngleZ = 0;
    private double cubeAngleX = 0, cubeAngleY = 0, cubeAngleZ = 0;

    private double zoom = 1.0;
    private double speed = 1.0;

    // Transparency factor for motion trails
    private float gamma = 0.9f;

    // Colors for the figure and background
    private Color figureColor = Color.WHITE;
    private Color backgroundColor = Color.BLACK;

    private boolean paused = false;
    private int trailLength = 10;

    private LinkedList<double[]> torusAngles = new LinkedList<>();
    private LinkedList<double[]> cubeAngles = new LinkedList<>();

    // Rotation speeds for each axis
    private double torusRotateX = 0.05, torusRotateY = 0.03, torusRotateZ = 0.02;
    private double cubeRotateX = 0.01, cubeRotateY = 0.04, cubeRotateZ = 0.06;

    /**
     * The 3D coordinates of the cube's vertices.
     */
    private double[][] cubeVertices;

    /**
     * Defines edges between vertices to form a cube structure.
     */
    private int[][] cubeEdges;

    /**
     * The 3D coordinates of the torus's vertices.
     */
    private double[][] torusVertices;

    /**
     * Defines edges between vertices to form a torus structure.
     */
    private int[][] torusEdges;

    public TorusCube3D() {
        buildCube();
        buildTorus();
    }

    /**
     * Initializes the 3D cube by generating vertices and edges.
     */
    private void buildCube() {
        double size = 1.0;
        cubeVertices = new double[][]{
                {-size, -size, -size}, {size, -size, -size}, {size, size, -size}, {-size, size, -size},
                {-size, -size, size}, {size, -size, size}, {size, size, size}, {-size, size, size}
        };
        cubeEdges = new int[][]{
                {0, 1}, {1, 2}, {2, 3}, {3, 0},
                {4, 5}, {5, 6}, {6, 7}, {7, 4},
                {0, 4}, {1, 5}, {2, 6}, {3, 7}
        };
    }


    /**
     * Initializes the 3D torus by generating vertices and edges.
     */
    private void buildTorus() {
        List<double[]> verts = new ArrayList<>();
        List<int[]> edges = new ArrayList<>();

        int rSteps = 20;
        int cSteps = 20;
        double R = 1.5;
        double r = 0.5;

        for (int i = 0; i < rSteps; i++) {
            double theta = 2 * Math.PI * i / rSteps;
            for (int j = 0; j < cSteps; j++) {
                double phi = 2 * Math.PI * j / cSteps;
                double x = (R + r * Math.cos(phi)) * Math.cos(theta);
                double y = (R + r * Math.cos(phi)) * Math.sin(theta);
                double z = r * Math.sin(phi);
                verts.add(new double[]{x, y, z});
            }
        }

        for (int i = 0; i < rSteps; i++) {
            for (int j = 0; j < cSteps; j++) {
                int curr = i * cSteps + j;
                int nextC = i * cSteps + (j + 1) % cSteps;
                int nextR = ((i + 1) % rSteps) * cSteps + j;
                edges.add(new int[]{curr, nextC});
                edges.add(new int[]{curr, nextR});
            }
        }

        torusVertices = verts.toArray(new double[0][]);
        torusEdges = edges.toArray(new int[0][]);
    }


    /**
     * Updates the current rotation angles and appends the new state to the trail history.
     */
    @Override
    public void update() {
        if (!paused) {
            torusAngleX += torusRotateX * speed;
            torusAngleY += torusRotateY * speed;
            torusAngleZ += torusRotateZ * speed;

            cubeAngleX += cubeRotateX * speed;
            cubeAngleY += cubeRotateY * speed;
            cubeAngleZ += cubeRotateZ * speed;
        }

        torusAngles.add(new double[]{torusAngleX, torusAngleY, torusAngleZ});
        cubeAngles.add(new double[]{cubeAngleX, cubeAngleY, cubeAngleZ});

        while (torusAngles.size() > trailLength) torusAngles.removeFirst();
        while (cubeAngles.size() > trailLength) cubeAngles.removeFirst();
    }

    /**
     * Renders the figure with a fading trail effect.
     *
     * @param g the Graphics context to draw into
     * @param width width of the drawing area
     * @param height height of the drawing area
     */
    @Override
    public void render(Graphics g, int width, int height) {
        g.setColor(backgroundColor);
        g.fillRect(0, 0, width, height);

        int n = Math.min(torusAngles.size(), cubeAngles.size());
        for (int i = 0; i < n; i++) {
            float alpha = (n == 1) ? 1.0f : Math.min(0.1f + gamma * (i / (float)(n - 1)), 1.0f);
            drawFigure(g, width, height, torusVertices, torusEdges, torusAngles.get(i), alpha, -3);
            drawFigure(g, width, height, cubeVertices, cubeEdges, cubeAngles.get(i), alpha, 3);
        }
    }

    /**
     * Projects and draws the 3D shape using provided rotation angles and transparency.
     *
     * @param g the Graphics context
     * @param width canvas width
     * @param height canvas height
     * @param angles rotation angles (x, y, z)
     * @param alpha transparency factor (0.0 to 1.0)
     */
    private void drawFigure(Graphics g, int width, int height, double[][] vertices, int[][] edges,
                            double[] angles, float alpha, double offsetX) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.setColor(figureColor);

        Point2D[] projected = new Point2D[vertices.length];
        double scale = Math.min(width, height) / 4.0 * zoom;
        double ax = angles[0], ay = angles[1], az = angles[2];

        for (int i = 0; i < vertices.length; i++) {
            double x = vertices[i][0];
            double y = vertices[i][1];
            double z = vertices[i][2];

            // Rotate around own center
            double y1 = y * Math.cos(ax) - z * Math.sin(ax);
            double z1 = y * Math.sin(ax) + z * Math.cos(ax);
            y = y1; z = z1;

            double x1 = x * Math.cos(ay) + z * Math.sin(ay);
            double z2 = -x * Math.sin(ay) + z * Math.cos(ay);
            x = x1; z = z2;

            double x2 = x * Math.cos(az) - y * Math.sin(az);
            double y2 = x * Math.sin(az) + y * Math.cos(az);
            x = x2; y = y2;

            // Shift the figure after rotation
            x += offsetX;

            double perspective = 3 / (z + 6);
            int px = (int)(width / 2 + x * scale * perspective);
            int py = (int)(height / 2 + y * scale * perspective);
            projected[i] = new Point2D.Double(px, py);
        }

        for (int[] edge : edges) {
            Point2D p1 = projected[edge[0]];
            Point2D p2 = projected[edge[1]];
            g2d.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
        }

        g2d.dispose();
    }

    /**
     * Sets the zoom level of the figure.
     *
     * @param zoom New zoom factor.
     */
    @Override public void setZoom(double zoom) { this.zoom = zoom; }

    /**
     * Sets the base speed multiplier of the rotation.
     *
     * @param speed Rotation speed multiplier.
     */
    @Override public void setSpeed(double speed) { this.speed = speed; }

    /**
     * Sets the color used to draw the figure.
     *
     * @param color New color for the figure.
     */
    @Override public void setFigureColor(Color color) { this.figureColor = color; }

    /**
     * Sets the background color behind the figure.
     *
     * @param color New background color.
     */
    @Override public void setBackgroundColor(Color color) { this.backgroundColor = color; }

    /**
     * Toggles the animation between paused and running.
     */
    @Override public void togglePause() { this.paused = !paused; }

    /**
     * Sets how many previous frames are remembered for trail rendering.
     *
     * @param trailLength Number of previous frames to retain.
     */
    @Override public void setTrailLength(int TrailLength) { this.trailLength = TrailLength; }

    /**
     * Sets the transparency factor for fading trail effect.
     *
     * @param gamma Transparency gamma (0.0 - 1.0).
     */
    @Override public void setGamma(float gamma) { this.gamma = gamma; }

    /**
     * Sets the rotation rate around the X-axis.
     *
     * @param rotateX Rotation increment per update for X-axis.
     */
    @Override public void setRotateX(double rotateX) {
        this.torusRotateX = rotateX;
        this.cubeRotateX = rotateX;
    }

    /**
     * Sets the rotation rate around the Y-axis.
     *
     * @param rotateY Rotation increment per update for Y-axis.
     */
    @Override public void setRotateY(double rotateY) {
        this.torusRotateY = rotateY;
        this.cubeRotateY = rotateY;
    }

    /**
     * Sets the rotation rate around the Z-axis.
     *
     * @param rotateZ Rotation increment per update for Z-axis.
     */
    @Override public void setRotateZ(double rotateZ) {
        this.torusRotateZ = rotateZ;
        this.cubeRotateZ = rotateZ;
    }
}
