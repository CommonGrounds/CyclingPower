package dev.java4now.web.util;

import dev.webfx.platform.console.Console;
import javafx.animation.AnimationTimer;
import javafx.scene.image.ImageView;

public class Helper_light_css {
    private static double angle;
    private static long lastTimerCall;
    private static AnimationTimer anim_timer;
    private static ImageView imageView;
    private static String currentStyleClass;

    public static void setEffect(ImageView view) {
        imageView = view;
        imageView.getStyleClass().add("image-view"); // Base CSS class
        currentStyleClass = "";

        angle = 0;
        lastTimerCall = System.nanoTime();
        anim_timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now > lastTimerCall + 50_000_000L) { // 50ms delay
                    updateLighting(angle);
                    angle = (angle + 1) % 360; // Increment angle
                    lastTimerCall = now;
                }
            }
        };
    }

    private static void updateLighting(double angleDeg) {
        // Compute light position (circular path, radius 25px, center at 50,50)
        double rad = Math.toRadians(angleDeg);
        double x = 50 + 25 * Math.cos(rad); // x: 25–75
        double y = 50 + 25 * Math.sin(rad); // y: 25–75

        // Map position to style class (0–99)
        int index = (int) (x / 100 * 50 + y / 100 * 50); // Simplified mapping
        index = Math.min(99, Math.max(0, index)); // Clamp to 0–99
        String newStyleClass = "light-" + index;

        // Update style class
        if (!newStyleClass.equals(currentStyleClass)) {
            if (!currentStyleClass.isEmpty()) {
                imageView.getStyleClass().remove(currentStyleClass);
            }
            imageView.getStyleClass().add(newStyleClass);
            currentStyleClass = newStyleClass;
            Console.log("Styles: " + imageView.getStyleClass());
        }
    }

    public static void startEffect() {
        if (anim_timer != null) {
            anim_timer.start();
        }
    }

    public static void stopEffect() {
        if (anim_timer != null) {
            anim_timer.stop();
        }
    }
}