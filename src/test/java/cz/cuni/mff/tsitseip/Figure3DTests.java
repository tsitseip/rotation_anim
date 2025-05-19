package cz.cuni.mff.tsitseip;

import cz.cuni.mff.tsitseip.figures.Donut3D;
import cz.cuni.mff.tsitseip.figures.Figure3D;
import cz.cuni.mff.tsitseip.figures.TorusCube3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

public class Figure3DTests {

    private Figure3D donut;
    private Figure3D torusCube;

    @BeforeEach
    public void setUp() {
        donut = new Donut3D();
        torusCube = new TorusCube3D();
    }

    @Test
    public void testZoomSetting() {
        donut.setZoom(2.5);
        assertEquals(2.5, ((Donut3D) donut).getZoom(), 0.0001);
    }

    @Test
    public void testSpeedSetting() {
        donut.setSpeed(3.5);
        assertEquals(3.5, ((Donut3D) donut).getSpeed(), 0.0001);
    }

    @Test
    public void testPauseToggle() {
        donut.update();
        double before = ((Donut3D) donut).getSpeed();
        donut.togglePause();
        donut.update();
        double after = ((Donut3D) donut).getSpeed();
        assertEquals(before, after, 0.0001);
    }

    @Test
    public void testSetColors() {
        Color color = Color.RED;
        donut.setFigureColor(color);
        donut.setBackgroundColor(Color.BLUE);

        Graphics g = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB).getGraphics();
        donut.render(g, 800, 600);  // Should not crash
    }

    @Test
    public void testTorusCubeInstantiation() {
        assertNotNull(torusCube);
    }

    @Test
    public void testRotationSetters() {
        torusCube.setRotateX(0.01);
        torusCube.setRotateY(0.02);
        torusCube.setRotateZ(0.03);
        torusCube.update(); // Should not crash
    }

    @Test
    public void testRenderWithoutException() {
        Graphics g = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB).getGraphics();
        torusCube.render(g, 800, 600);  // Should not crash
    }
}
