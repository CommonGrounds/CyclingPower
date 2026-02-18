package dev.java4now.web.util;

import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class Helper_light {

    private static double angle;
    private static long lastTimerCall;
    private static AnimationTimer anim_timer;
    private static Rectangle overlay; // Semi-transparent overlay

    public static void setEffect(Rectangle overlayRect) {
        overlay = overlayRect;

        angle = 0;
        lastTimerCall = System.nanoTime();
        anim_timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now > lastTimerCall + 20_000_000L) { // 20ms delay
                    updateLighting(angle);
                    angle = (angle + 1) % 360; // Increment angle, reset at 360
                    lastTimerCall = now;
                }
            }
        };
    }

    private static void updateLighting(double azimuth) {
        double rad = Math.toRadians(azimuth);
        double x = 100 + 50 * Math.cos(rad);
        double y = 100 + 50 * Math.sin(rad);
        double brightnessFactor = 0.5 + 0.5 * Math.cos(rad); // Range [0, 1]

        overlay.setFill(new Color(1.0, 1.0, 1.0, brightnessFactor * 0.8)); // 80%
//        overlay.setTranslateX(x - 100); // Center at x
//        overlay.setTranslateY(y - 100); // Center at y
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

    /*
    //-----------------------------------------------
    public static String formatTimestamp(long seconds) {
        // Define the base date (UTC 1989-12-31 00:00:00).
        LocalDateTime baseDate = LocalDateTime.of(1989, 12, 31, 0, 0, 0);
        ZoneOffset utcOffset = ZoneOffset.UTC; // Use UTC timezone

        // Calculate the LocalDateTime by adding the provided seconds.
        LocalDateTime dateTime = baseDate.plusSeconds(seconds);

        // Format the LocalDateTime to the desired pattern (00:00:00).
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return dateTime.format(formatter);
    }
 */
// You could also use the Instant class. It represents a point in time. It can be easily converted to a LocalDateTime.
//      Time - unix same as fit
    public static String formatTimestamp(long seconds) {
        Instant baseInstant = LocalDateTime.of(1989, 12, 31, 0, 0, 0).toInstant(ZoneOffset.UTC);
        Instant targetInstant = baseInstant.plusSeconds(seconds);
        LocalDateTime dateTime = LocalDateTime.ofInstant(targetInstant, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return dateTime.format(formatter);
    }


//    Date - standard unix
    /*
    public static LocalDate formatDate(long seconds){
        try {
            // Handle seconds vs milliseconds (guess based on magnitude)
            if (seconds > 1_000_000_000_000L) { // Likely milliseconds (post-2001)
                return Instant.ofEpochMilli(seconds).atZone(ZoneId.systemDefault()).toLocalDate();
            } else { // Likely seconds
                return Instant.ofEpochSecond(seconds).atZone(ZoneId.systemDefault()).toLocalDate();
            }
        } catch (NumberFormatException e) {
            return LocalDate.now(); // Fallback
        }
    }
*/
    // Garmin Fit Date
public static LocalDate formatDate(long seconds) {
    try {
        // FIT epoch: 1989-12-31 00:00:00 UTC
        final long FIT_EPOCH_SECONDS = 631065600L;
        // Assume seconds < 1_000_000_000_000 are FIT timestamps (seconds since 1989)
        if (seconds < 1_000_000_000_000L) {
            // Convert FIT timestamp to Unix timestamp
            seconds += FIT_EPOCH_SECONDS;
            return Instant.ofEpochSecond(seconds).atZone(ZoneId.systemDefault()).toLocalDate();
        } else {
            // Handle milliseconds (post-2001 Unix timestamps)
            return Instant.ofEpochMilli(seconds).atZone(ZoneId.systemDefault()).toLocalDate();
        }
    } catch (Exception e) {
        return LocalDate.now(); // Fallback
    }
}

//-------------------------------------------------------------------------------------
// Map one number range into another // the formula is // f(x) = (x - input_start) / (input_end - input_start) * (output_end - output_start) + output_start

    public static long map(long x, long in_min, long in_max, long out_min, long out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
//----------------------------------------------------------------------

}
