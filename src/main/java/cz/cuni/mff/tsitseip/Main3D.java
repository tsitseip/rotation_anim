/**
 * Provides functionality for animating geometric figures and configure some parameters.
 *
 * This package contains classes responsible for GUI and controls
 *
 * Main3D class: Creates the user interface and handles user interaction with it;
 * figures/Figure3D class: Provides interface for all figures.
 * figures/* class: Handle figure animations, zooming, speed and other settings.
 *
 *
 * @author Tsitsei Pavlo
 */


package cz.cuni.mff.tsitseip;

import cz.cuni.mff.tsitseip.figures.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main3D class to initialize and run the 3D animation application.
 * Includes a GUI with controls to manipulate the displayed 3D figure.
 */
public class Main3D {
    // Configuration variables for animation and GUI state
    private static Color fgColor = Color.WHITE;
    private static double rotationSpeed = 1.0;
    private static int TrailLength = 1;
    private static float gamma = 0.9f;
    private static double xRotationSpeed = 0.01;
    private static double yRotationSpeed = 0.01;
    private static double zRotationSpeed = 0.01;
    private static boolean FPS = false;
    private static double zoomValue = 1.0;


    /**
     * Launches the 3D animation application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("3D Animation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1700, 800);


        // Available 3D figures
        Figure3D[] figures = {
                new Donut3D(),
                new Cube3D(),
                new Pyramid3D(),
                new Strange_Bridge3D(),
                new TorusCube3D()
        };
        String[] figureNames = {
                "Donut",
                "Cube",
                "Pyramid",
                "Strange Bridge",
                "Composition of Donut and Cube"
        };


        // UI controls
        JComboBox<String> selector = new JComboBox<>(figureNames);
        JButton fgColorButton = new JButton("Figure Color");
        JButton bgColorButton = new JButton("Background Color");


        // Sliders for configuration
        JSlider speedSlider = new JSlider(0, 100, 10);
        speedSlider.setMajorTickSpacing(20);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        JLabel speedLabel = new JLabel("Speed: 1.0");

        JSlider TrailSlider = new JSlider(0, 20, 1);
        TrailSlider.setMajorTickSpacing(5);
        TrailSlider.setPaintTicks(true);
        TrailSlider.setPaintLabels(true);
        JLabel TrailLabel = new JLabel("Trail: 1");

        JSlider GammaSlider = new JSlider(0, 100, 90);
        GammaSlider.setMajorTickSpacing(20);
        GammaSlider.setPaintTicks(true);
        GammaSlider.setPaintLabels(true);
        JLabel GammaLabel = new JLabel("Gamma: 90");

        JSlider xSpeedSlider = new JSlider(0, 100, 10);
        xSpeedSlider.setMajorTickSpacing(20);
        xSpeedSlider.setPaintTicks(true);
        xSpeedSlider.setPaintLabels(true);
        JLabel xSpeedLabel = new JLabel("X Speed: 0.1");

        JSlider ySpeedSlider = new JSlider(0, 100, 10);
        ySpeedSlider.setMajorTickSpacing(20);
        ySpeedSlider.setPaintTicks(true);
        ySpeedSlider.setPaintLabels(true);
        JLabel ySpeedLabel = new JLabel("Y Speed: 0.1");

        JSlider zSpeedSlider = new JSlider(0, 100, 10);
        zSpeedSlider.setMajorTickSpacing(20);
        zSpeedSlider.setPaintTicks(true);
        zSpeedSlider.setPaintLabels(true);
        JLabel zSpeedLabel = new JLabel("Z Speed: 0.1");

        JSlider zoomSlider = new JSlider(0, 4500, 100);
        zoomSlider.setMajorTickSpacing(1000);
        zoomSlider.setPaintTicks(true);
        zoomSlider.setPaintLabels(true);
        JLabel zoomLabel = new JLabel("Zoom: 1.00");


        // Checkbox for configuration
        JCheckBox FPS = new JCheckBox("Show FPS", false);


        DrawingCanvas canvas = new DrawingCanvas(figures[0]);
        figures[0].setFigureColor(fgColor);
        figures[0].setSpeed(rotationSpeed);
        figures[0].setTrailLength(TrailLength);
        figures[0].setGamma(gamma);


        // Top control panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(selector);
        topPanel.add(fgColorButton);
        topPanel.add(bgColorButton);
        topPanel.add(speedLabel);
        topPanel.add(speedSlider);
        topPanel.add(TrailLabel);
        topPanel.add(TrailSlider);
        topPanel.add(GammaLabel);
        topPanel.add(GammaSlider);
        topPanel.add(FPS);


        // Bottom control panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(xSpeedLabel);
        bottomPanel.add(xSpeedSlider);
        bottomPanel.add(ySpeedLabel);
        bottomPanel.add(ySpeedSlider);
        bottomPanel.add(zSpeedLabel);
        bottomPanel.add(zSpeedSlider);
        bottomPanel.add(zoomLabel);
        bottomPanel.add(zoomSlider);


        // Combine all parts of screen
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(canvas, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);


        // Set up new figure when changing it
        selector.addActionListener((ActionEvent e) -> {
            int idx = selector.getSelectedIndex();
            canvas.setFigure(figures[idx]);
            figures[idx].setFigureColor(fgColor);
            figures[idx].setSpeed(rotationSpeed);
            figures[idx].setTrailLength(TrailLength);
            figures[idx].setGamma(gamma);
            figures[idx].setRotateX(xRotationSpeed);
            figures[idx].setRotateY(yRotationSpeed);
            figures[idx].setRotateZ(zRotationSpeed);
            figures[idx].setZoom(zoomValue);
            canvas.setFigureColor(fgColor);
        });


        // Make option to change color of figure
        fgColorButton.addActionListener(e -> {
            JColorChooser chooser = new JColorChooser(fgColor);
            chooser.setPreviewPanel(new JPanel());

            JDialog dialog = JColorChooser.createDialog(
                    frame,
                    "Choose Figure Color",
                    true,
                    chooser,
                    ev -> {
                        Color newFg = chooser.getColor();
                        if (newFg != null) {
                            fgColor = newFg;
                            figures[selector.getSelectedIndex()].setFigureColor(fgColor);
                            canvas.setFigureColor(fgColor);
                            canvas.repaint();
                        }
                    },
                    null
            );

            dialog.setVisible(true);
        });



        // Make option to change color of background
        bgColorButton.addActionListener(e -> {
            JColorChooser chooser = new JColorChooser(canvas.getBackgroundColor());
            chooser.setPreviewPanel(new JPanel());

            JDialog dialog = JColorChooser.createDialog(
                    frame,
                    "Choose Background Color",
                    true,
                    chooser,
                    ev -> {
                        Color newBg = chooser.getColor();
                        if (newBg != null) {
                            canvas.setBackgroundColor(newBg);
                        }
                    },
                    null
            );

            dialog.setVisible(true);
        });


        // Configure sliders and change variables in figures depending on value
        speedSlider.addChangeListener(e -> {
            int val = speedSlider.getValue();
            rotationSpeed = val / 10.0;
            speedLabel.setText(String.format("Speed: %.1f", rotationSpeed));
            figures[selector.getSelectedIndex()].setSpeed(rotationSpeed);
        });

        TrailSlider.addChangeListener(e -> {
            int val = TrailSlider.getValue();
            TrailLength = val;
            TrailLabel.setText(String.format("Trail: %d", TrailLength));
            figures[selector.getSelectedIndex()].setTrailLength(TrailLength);
        });

        GammaSlider.addChangeListener(e -> {
            int val = GammaSlider.getValue();
            gamma = val / 100.0f;
            GammaLabel.setText(String.format("Gamma: %d", val));
            figures[selector.getSelectedIndex()].setGamma(gamma);
        });

        xSpeedSlider.addChangeListener(e -> {
            xRotationSpeed = xSpeedSlider.getValue() / 100.0;
            xSpeedLabel.setText(String.format("X Speed: %.2f", xRotationSpeed));
            figures[selector.getSelectedIndex()].setRotateX(xRotationSpeed);
        });

        ySpeedSlider.addChangeListener(e -> {
            yRotationSpeed = ySpeedSlider.getValue() / 100.0;
            ySpeedLabel.setText(String.format("Y Speed: %.2f", yRotationSpeed));
            figures[selector.getSelectedIndex()].setRotateY(yRotationSpeed);
        });

        zSpeedSlider.addChangeListener(e -> {
            zRotationSpeed = zSpeedSlider.getValue() / 100.0;
            zSpeedLabel.setText(String.format("Z Speed: %.2f", zRotationSpeed));
            figures[selector.getSelectedIndex()].setRotateZ(zRotationSpeed);
        });


        FPS.addActionListener(e -> {
            canvas.setShowFps(FPS.isSelected());
        });

        zoomSlider.addChangeListener(e -> {
            zoomValue = zoomSlider.getValue() / 100.0;
            zoomLabel.setText(String.format("Zoom: %.2f", zoomValue));
            figures[selector.getSelectedIndex()].setZoom(zoomValue);
            canvas.changeZoom(zoomValue);
        });

        // Synchronize zooming with mouse and slider
        canvas.setZoomChangeCallback(() -> {
            int zoomInt = (int)(canvas.getZoom() * 100);
            SwingUtilities.invokeLater(() -> {
                zoomSlider.setValue(zoomInt);
                zoomLabel.setText(String.format("Zoom: %.2f", zoomInt / 100.0));
            });
        });

        // Add everything on the screen
        frame.setContentPane(panel);
        frame.setVisible(true);
        canvas.setAxisRotation(xRotationSpeed, yRotationSpeed, zRotationSpeed);
        canvas.start();
    }

    /**
     * DrawingCanvas is the main display panel responsible for rendering 3D figures.
     */
    static class DrawingCanvas extends JPanel {
        private Figure3D figure;
        private double zoom = 1.0;
        private Color figureColor = Color.WHITE;
        private Color backgroundColor = Color.BLACK;
        private long lastFpsTime = System.currentTimeMillis();
        private int frameCount = 0;
        private int currentFps = 0;
        private boolean showFps = false;
        private Runnable zoomChangeCallback;

        // Setters and getters

        /**
         *
         * @param callback to synchronize mouse and GUI zooming
         */
        void setZoomChangeCallback(Runnable callback) {
            this.zoomChangeCallback = callback;
        }

        /**
         *
         * @return zoom
         */
        double getZoom() {
            return zoom;
        }


        /**
         *
         * @param zoom
         */
        void changeZoom(double zoom) {
            this.zoom = zoom;
        }


        /**
         *
         * @param show
         */
        void setShowFps(boolean show) {
            this.showFps = show;
        }


        // The constructor
        DrawingCanvas(Figure3D fig) {
            this.figure = fig;
            setDoubleBuffered(true);
            setBackground(backgroundColor);
            addMouseWheelListener(e -> {
                zoom *= Math.pow(1.1, -e.getPreciseWheelRotation());
                zoom = Math.min(zoom, 45.0);
                figure.setZoom(zoom);
                if (zoomChangeCallback != null) {
                    zoomChangeCallback.run();
                }
                repaint();
            });
            figure.setZoom(zoom);
            setupKeyBindings();
        }

        private void setupKeyBindings() {
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "spacePressed");
            getActionMap().put("spacePressed", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    figure.togglePause();
                }
            });
        }

        /**
         * Function that sets figure fig and put initial settings, such as zoom, figure color and background
         * @param fig
         */
        void setFigure(Figure3D fig) {
            this.figure = fig;
            figure.setZoom(zoom);
            figure.setFigureColor(figureColor);
            figure.setBackgroundColor(backgroundColor);
            repaint();
        }

        /**
         *
         * @param color
         */
        void setFigureColor(Color color) {
            this.figureColor = color;
            figure.setFigureColor(color);
            repaint();
        }

        /**
         *
         * @param color
         */
        void setBackgroundColor(Color color) {
            this.backgroundColor = color;
            setBackground(color);
            figure.setBackgroundColor(color);
            repaint();
        }

        /**
         * Put initial rotation by x, y, z to figure.
         * @param rx
         * @param ry
         * @param rz
         */
        void setAxisRotation(double rx, double ry, double rz) {
            figure.setRotateX(rx);
            figure.setRotateY(ry);
            figure.setRotateZ(rz);
        }

        /**
         *
         * @return background color
         */
        Color getBackgroundColor() {
            return backgroundColor;
        }

        /**
         * Function that count FPS and update each frame
         */
        void start() {
            Timer timer = new Timer(16, e -> {
                figure.update();
                repaint();
                frameCount++;
                long now = System.currentTimeMillis();
                if (now - lastFpsTime >= 1000) {
                    currentFps = frameCount;
                    frameCount = 0;
                    lastFpsTime = now;
                }
            });
            timer.start();
        }

        /**
         * Function that render figure by calling render and outputs FPS
         * @param g
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            figure.render(g, getWidth(), getHeight());

            if (showFps){
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.GREEN);
                g2.setFont(new Font("Monospaced", Font.PLAIN, 16));
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth("FPS: " + currentFps);
                int x = (getWidth() - textWidth) / 2;
                int y = getHeight() - 10;
                g2.drawString("FPS: " + currentFps, x, y);
            }

        }
    }
}
