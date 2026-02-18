package dev.java4now.web.util;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

public class Helper_svg {

    static final String bike_path = "M238.601,134.975c-0.311,0-0.617,0.018-0.927,0.023L218.206,72.35c-0.975-3.137-3.877-5.274-7.162-5.274h-33.5" +
            "c-4.143,0-7.5,3.357-7.5,7.5s3.357,7.5,7.5,7.5h27.977l5.749,19H85.505l-29.293-35h7.333c4.143,0,7.5-3.357,7.5-7.5" +
            "s-3.357-7.5-7.5-7.5h-45c-4.143,0-7.5,3.357-7.5,7.5s3.357,7.5,7.5,7.5h17.99l53.793,63.354c1.483,1.747,3.595,2.646,5.721,2.646" +
            "c1.716,0,3.44-0.585,4.851-1.783c3.157-2.681,3.544-7.164,0.862-10.321l-3.52-3.896h98.437l-51.746,64.746l-40.428-0.165" +
            "c-3.404-25.676-25.425-45.619-52.015-45.619C23.546,135.037,0,158.552,0,187.494c0,28.941,23.546,52.472,52.488,52.472" +
            "c26.198,0,47.973-19.052,51.866-44.177l44.182,0.286c0.003,0,0.005,0,0.008,0c2.375,0,4.489-1.358,5.864-3.082l0.003,0.128" +
            "l61.438-77.046h0.081l6.793,21.61c-21.204,6.745-36.61,26.493-36.61,49.903c0,28.941,23.546,52.425,52.487,52.425" +
            "s52.487-23.577,52.487-52.519C291.088,158.552,267.542,134.975,238.601,134.975z M52.488,224.95" +
            "C31.817,224.95,15,208.134,15,187.463s16.817-37.488,37.488-37.488c18.297,0,33.564,13.18,36.832,30.542l-15.889-0.016" +
            "c-2.935-8.835-11.273-15.227-21.081-15.227c-12.248,0-22.213,9.964-22.213,22.212s9.965,22.213,22.213,22.213" +
            "c9.423,0,17.488-5.9,20.71-14.198l16.035,0.016C85.399,212.328,70.394,224.95,52.488,224.95z M238.601,224.95" +
            "c-20.671,0-37.487-16.816-37.487-37.487c0-16.688,10.962-30.858,26.062-35.702l4.516,14.533" +
            "c-9.005,2.836-15.554,11.262-15.554,21.191c0,12.248,9.965,22.213,22.213,22.213s22.212-9.965,22.212-22.213" +
            "c0-8.898-5.261-16.586-12.833-20.128l-5.342-17.192c18.899,1.905,33.701,17.905,33.701,37.297" +
            "C276.088,208.134,259.271,224.95,238.601,224.95z";

    public static StackPane get_svg(boolean half_size) {
        StackPane root = new StackPane();

        SVGPath svgPath = new SVGPath();
        svgPath.setContent(bike_path);      // bike_path
        svgPath.setStroke(Color.LIMEGREEN);
        svgPath.setStrokeWidth(2);
        svgPath.setFill(null); // No fill, like CSS
        svgPath.setStrokeLineCap(StrokeLineCap.ROUND); // Matches stroke-linecap: round
        if (half_size) {
            svgPath.setScaleX(0.4);
            svgPath.setScaleY(0.4);
        }

        // Set up stroke dash array (approximate path length)
        double pathLength = 1650; // Adjust based on path;  1650 za bike_path
        svgPath.getStrokeDashArray().addAll(pathLength, pathLength);
        svgPath.setStrokeDashOffset(pathLength); // Start with full offset

        // Create stroke animation
        Timeline timeline = new Timeline();
        KeyValue keyValue = new KeyValue(svgPath.strokeDashOffsetProperty(), 0);
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(4), keyValue);
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(1); // Run once, like CSS "forwards"
//        timeline.setCycleCount(10); // 10 puta ili Timeline.INDEFINITE

        // Apply glowing effect
        DropShadow glow = new DropShadow();
        glow.setColor(Color.LIMEGREEN);
        glow.setRadius(12); // Approximate the largest drop-shadow (12px)
        glow.setSpread(0.5); // Controls glow intensity
        svgPath.setEffect(glow);

        // Add to pane
        root.getChildren().add(svgPath);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE, null, null)));

        // Start animation
        timeline.play();

        return root;
    }
}
