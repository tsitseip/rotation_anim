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
import java.util.LinkedList;
import java.util.ArrayList;

/**
 * Represents a 3D rotating bridge rendered manually using simple rotation
 * and projection math. Implements animation with configurable parameters such as speed,
 * zoom, trail length, and color.
 */
public class Strange_Bridge3D implements Figure3D {
    // Rotation angles for X, Y, Z axes
    private double angleX = 0, angleY = 0, angleZ = 0;

    private double zoom = 1.0;

    // Transparency factor for motion trails
    private float gamma = 0.9f;

    // Colors for the figure and background
    private Color figureColor = Color.RED;
    private Color backgroundColor = Color.BLACK;

    // Rotation speeds for each axis
    private double rotateX = 0.1;
    private double rotateY = 0.1;
    private double rotateZ = 0.1;

    /**
     * The 3D coordinates of the bridge's vertices.
     */
    private double[][] vertices;

    /**
     * Defines edges between vertices to form a bridge structure.
     */
    private int[][] edges;

    private boolean paused = false;

    double speed = 1;
    double pause = 1;

    /**
     * Length of the motion trail.
     */
    private int trailLength = 10;

    /**
     * Keeps a history of rotation angles to create a motion trail.
     */
    private LinkedList<double[]> angleHistory = new LinkedList<>();

    /**
     * Initializes the 3D model by generating vertices and edges.
     * The shape consists of two rounded rectangle faces and a triangular prism element.
     */
    private void buildModel() {
        java.util.List<double[]> vList = new ArrayList<>();
        java.util.List<int[]> eList = new ArrayList<>();

        double width = 2.0, height = 1.2, depth = 0.2, radius = 0.3;
        int cornerSegments = 10;
        double[][] corners = {{-1, -1}, {1, -1}, {1, 1}, {-1, 1}};

        for (int face = 0; face < 2; face++) {
            double z = (face == 0) ? -depth : depth;
            for (int c = 0; c < 4; c++) {
                double cx = corners[c][0] * (width / 2 - radius);
                double cy = corners[c][1] * (height / 2 - radius);
                double startAngle = c * Math.PI / 2;
                for (int i = 0; i <= cornerSegments; i++) {
                    double theta = startAngle + i * Math.PI / 2 / cornerSegments;
                    double x = cx + radius * Math.cos(theta);
                    double y = cy + radius * Math.sin(theta);
                    vList.add(new double[]{x, y, z});
                }
            }
        }

        int vertsPerFace = 4 * (cornerSegments + 1);
        for (int i = 0; i < vertsPerFace; i++) {
            eList.add(new int[]{i, (i + 1) % vertsPerFace});
            eList.add(new int[]{i + vertsPerFace, (i + 1) % vertsPerFace + vertsPerFace});
            eList.add(new int[]{i, i + vertsPerFace});
        }

        double[][] tri = {
                {-0.4, -0.3, 0.01}, {0.5, 0.0, 0.01}, {-0.4, 0.3, 0.01},
                {-0.4, -0.3, 0.05}, {0.5, 0.0, 0.05}, {-0.4, 0.3, 0.05}
        };
        int startIdx = vList.size();
        for (double[] p : tri) vList.add(p);
        int[][] triEdges = {
                {0, 1}, {1, 2}, {2, 0}, {3, 4}, {4, 5}, {5, 3}, {0, 3}, {1, 4}, {2, 5}
        };
        for (int[] e : triEdges) eList.add(new int[]{startIdx + e[0], startIdx + e[1]});

        vertices = vList.toArray(new double[0][]);
        edges = eList.toArray(new int[0][]);
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
    public void drawFigure(Graphics g, int width, int height, double[] angles, float alpha) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        Point2D[] projected = new Point2D[vertices.length];
        double scale = Math.min(width, height) / 2.5 * zoom;

        double ax = angles[0], ay = angles[1], az = angles[2];

        for (int i = 0; i < vertices.length; i++) {
            double[] v = vertices[i];
            double x = v[0], y = v[1], z = v[2];

            // Rotate around Z
            double x1 = x * Math.cos(az) - y * Math.sin(az);
            double y1 = x * Math.sin(az) + y * Math.cos(az);
            x = x1; y = y1;

            // Rotate around Y
            double x2 = x * Math.cos(ay) + z * Math.sin(ay);
            double z2 = -x * Math.sin(ay) + z * Math.cos(ay);
            x = x2; z = z2;

            // Rotate around X
            double y3 = y * Math.cos(ax) - z * Math.sin(ax);
            double z3 = y * Math.sin(ax) + z * Math.cos(ax);
            y = y3; z = z3;

            // Perspective projection
            double perspective = 3 / (z + 5);
            int px = (int)(width / 2 + x * scale * perspective);
            int py = (int)(height / 2 + y * scale * perspective);
            projected[i] = new Point2D.Double(px, py);
        }

        g2d.setColor(figureColor);
        for (int[] edge : edges) {
            Point2D p1 = projected[edge[0]];
            Point2D p2 = projected[edge[1]];
            g2d.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
        }

        g2d.dispose();
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
        g.setColor(figureColor);

        // Draw trail first: from oldest (most faded) to newest (most opaque)
        int n = angleHistory.size();
        for (int i = 0; i < n; i++) {
            double[] a = angleHistory.get(i);
            float alpha;
            if (n == 1) {
                alpha = 1.0f;
            } else {
                alpha = Math.min( 0.1f + gamma * (i / (float)(n - 1)), 1.0f );
            }
            drawFigure(g, width, height, a, alpha);
        }
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
     * Sets the zoom level of the figure.
     *
     * @param zoom New zoom factor.
     */
    @Override
    public void setZoom(double zoom) {
        this.zoom = zoom;
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
     * Sets the base speed multiplier of the rotation.
     *
     * @param speed Rotation speed multiplier.
     */
    @Override
    public void setSpeed(double speed) {
        this.speed = speed;
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
     * Sets how many previous frames are remembered for trail rendering.
     *
     * @param trailLength Number of previous frames to retain.
     */
    @Override
    public void setTrailLength(int TrailLength) {
        this.trailLength = TrailLength;
    }

    public Strange_Bridge3D() {
        buildModel();
    }

    /**
     * Toggles the animation between paused and running.
     */
    @Override
    public void togglePause() {
        paused = !paused;
    }

    /**
     * Updates the current rotation angles and appends the new state to the trail history.
     */
    @Override
    public void update() {
        if (!paused) {
            angleX += rotateX * speed;
            angleY += rotateY * speed;
            angleZ += rotateZ * speed;
        }
        angleHistory.add(new double[]{angleX, angleY, angleZ});
        while (angleHistory.size() > trailLength) {
            angleHistory.removeFirst();
        }
    }
}
