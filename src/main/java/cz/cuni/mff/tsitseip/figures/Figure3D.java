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

public interface Figure3D {
    void update();
    void render(Graphics g, int width, int height);
    void setZoom(double zoom);
    void setSpeed(double speed);
    void setFigureColor(Color color);
    void setBackgroundColor(Color color);
    void togglePause();
    void setTrailLength(int TrailLength);
    void setGamma(float gamma);
    void setRotateX(double rotateX);
    void setRotateY(double rotateY);
    void setRotateZ(double rotateZ);
}
