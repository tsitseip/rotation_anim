# Rotating Animation User Documentation

This document provides manual on how to use **Rotating Animation** app.

The application allows to choose different figures and configure some settings.

### System requirements:

- Java 17 or newer.

### How to launch

- From a folder where the **pom.xml** file is in run `mvn compile exec:java -Dexec.mainClass="cz.cuni.mff.tsitseip.Main3D"`

### Main features

- You can choose one of the following figures: **Donut**, **Cube**, **Pyramid**, **Strange Bridge**, **Composition of Donut and Cube**. (The last one is just to figures near each other)
- You can change color of figure and/or background by clicking on respected buttons and choosing color there.
- By clicking space, you can stop animation. If you click one more time, it will resume.
- You can change the speed of the animation by changing the slider labeled **Speed**.
- You can change the trail of the animation by changing the slider labeled **Trail**.
- You can change the brightness of the trail by changing the slider labeled **Gamma**.
- You can change the proporsion of speed of rotating of the figure by changing the slider labeled **X Speed**, **Y Speed**, **Z Speed**.
- You can change the distance to the figure by using the mouse’s middle button — drag up to zoom in and drag down to zoom out — or by adjusting the slider labeled **Zoom**.
- You can show FPS on the screen by pressing on checkbox labeled **Show FPS**.
# rotation_anim