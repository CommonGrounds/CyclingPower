package dev.java4now.web.graph;

import dev.java4now.web.CyclingPower_Web_Local;
import dev.java4now.web.util.Format;
import dev.java4now.web.util.Helper_light;
import dev.java4now.web.util.WebFXUtil;
import dev.webfx.platform.resource.Resource;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;

import static dev.java4now.web.CyclingPower_Web_Local.activity;
import static dev.java4now.web.graph.CanvasChartPane.*;

public class GraphWind {

    static Arc wind_arc;
    static Circle circle;
    static Rotate rotate;
    static double CIRCLE_SCALE = 1.0;
    public static Pane pane;
    static Label legend;
    public static boolean is_visible = false;
    static GraphicsContext context;
    static Image bike_img = new Image(Resource.toUrl("pics/bike_above.png", CyclingPower_Web_Local.class), true);
    static Image bike_img_small = new Image(Resource.toUrl("pics/bike_above_small.png", CyclingPower_Web_Local.class), true);
    static Image bike_img_final;
    static ImageView imageView = new ImageView();
    public static final DoubleProperty wind_rotation = new SimpleDoubleProperty(0.0);


    public static void create(double width, double height, Pane parent) {
        circle = new Circle();
        wind_arc = draw_arc();

        if(width>height){
            circle.setRadius((height / 2) * CIRCLE_SCALE);
        }else{
            circle.setRadius((width / 2) * CIRCLE_SCALE);
        }
        circle.setCenterX(width / 2);
        circle.setCenterY(height / 2);
        wind_arc.setCenterX(width / 2);
        wind_arc.setCenterY(height / 2 - circle.getRadius() + wind_arc.getRadiusY());
        rotate.setPivotX(width / 2); // tacka rotacije po x
        rotate.setPivotY(height / 2); // tacka rotacije po y
//                rotate.setAngle(270);
//                circle.setTranslateX(getWidth()/2 - circle.getRadius()/2);
//                circle.setTranslateY(getHeight()/2 - circle.getRadius()/2);
        circle.setStroke(Color.BLACK);


        if(CyclingPower_Web_Local.screen_width < 810 || CyclingPower_Web_Local.screen_height < 610){
            bike_img_final = bike_img_small;
        }else{
            bike_img_final = bike_img;
        }

        legend = new Label("Wind");
//        legend.setFont(Font.font("sans-serif", FontWeight.BOLD, FontPosture.REGULAR, 12));
        legend.setTextFill(Color.GRAY);
        rotate.setAngle(wind_rotation.get());
        pane = new Pane(circle, wind_arc, imageView,legend){
            @Override
            public void layoutChildren() {
                super.layoutChildren();
                if (is_visible) {
                    double x = circle.getCenterX() - imageView.getFitWidth()/2;
                    double y = circle.getCenterY() - imageView.getFitHeight()/2;
                    if(width>height){
                        imageView.relocate(x, y);
//                        legend_box.relocate(getWidth() - legend_box.getWidth()-10, getHeight()/2 - legend_box.getHeight()/2 );
                    }else{
                        imageView.relocate(x, y);
//                        legend_box.relocate(getWidth() - legend_box.getWidth()-10, getHeight()/2 - legend_box.getHeight()/2 );
                    }
                    if (CyclingPower_Web_Local.screen_width < 600){
                        circle.setVisible(false);
                    }else{
                        circle.setVisible(true);
                    }
                    legend.relocate(getWidth() - legend.getWidth()-10, getHeight() - legend.getHeight()-10 );
                }
            }
        };

        // important MODIFIKOVANO: Sačekajte da se slika učita pre dodavanja u parent
        WebFXUtil.onImageLoaded(bike_img_final, () -> {
            imageView.setImage(bike_img_final);
            imageView.setRotate(-90);

            // SAMO SADA dodajte pane u parent kada je slika sigurno učitanа
            parent.getChildren().add(pane);
        });

//        pane.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
        pane.setVisible(false);
        is_visible = false;
    }


    //--------------------------------------------------
    public static void draw_wind(double width, double height) {
        if(width>height){
            circle.setRadius((height / 2) * CIRCLE_SCALE);
        }else{
            CIRCLE_SCALE = 0.5;
            circle.setRadius((width / 2) * CIRCLE_SCALE);
        }

        circle.setCenterX(width / 2);
        circle.setCenterY(height / 2);
        wind_arc.setCenterX(width / 2);
        wind_arc.setCenterY(height / 2 - circle.getRadius() + wind_arc.getRadiusY());
        rotate.setPivotX(width / 2); // tacka rotacije po x
        rotate.setPivotY(height / 2); // tacka rotacije po y
//                rotate.setAngle(270);
//                circle.setTranslateX(getWidth()/2 - circle.getRadius()/2);
//                circle.setTranslateY(getHeight()/2 - circle.getRadius()/2);
        imageView.setFitWidth(bike_img_final.getWidth() );   // * CIRCLE_SCALE
        imageView.setFitHeight(bike_img_final.getHeight() ); // * CIRCLE_SCALE
        imageView.setX(circle.getCenterX() - imageView.getFitWidth()/2);
        imageView.setY(circle.getCenterY() - imageView.getFitHeight()/2);
        circle.setStroke(Color.BLACK);

//        circle.setFill(new ImagePattern(bike_img));   // ne radi za GWT ???
        rotate.setAngle(wind_rotation.get());

        pane.setVisible(true);
        pane.toBack();
        is_visible = true;
//        rotate.setAngle(wind_rotation.get());
    }


    //-----------------------------------------
    public static void hide_wind() {
        pane.setVisible(false);
        is_visible = false;
    }


    //---------------------------------------------------
    private static Arc draw_arc() {
        Arc arc = new Arc();
        arc.setCenterX(0);
        arc.setCenterY(0);
        arc.setRadiusX(24);
        arc.setRadiusY(24);
        arc.setStartAngle(65);    // pocetni ugao desno
        arc.setLength(50);        // od pocetnog ugla
        arc.setType(ArcType.ROUND);
        arc.setFill(Color.RED);

        rotate = new Rotate();

        //setting properties for the rotate object.
        rotate.setAngle(0);
        rotate.setPivotX(0);     // tacka rotacije po x
        rotate.setPivotY(0);     // tacka rotacije po y
        arc.getTransforms().add(rotate);

        return arc;
    }


    //--------------------------------------------------------------
    public static void handle_slider_wind(Slider slider, Number newValue, Canvas layer_2) {
        //            Console.log("slider: " + newValue);

        if (activity == null) {
            return;
        }

        var pos = Helper_light.map((long) newValue.doubleValue(),
                (long)(slider.getMin()+0L),
                (long)(slider.getMax()+0L),
                (long) (start_position_x),
                (long) (layer_2.getWidth()));

        // important - ako je ispod 1 dobijamo exeption - brisemo levo
        if((graph_step * (pos-start_position_x)) < 1.0){
            context2.clearRect(0, 0, layer_2.getWidth(), layer_2.getHeight());
            return;
        }

        double x = layer_2.getLayoutX() + pos + 20; // pozicija pocetka box-a po x
        // right side
        if ((pos + 20 + 105) > layer_2.getWidth()) {
            x = layer_2.getLayoutX() + pos - 20 - 105; // pomeranje pocetka box-a u levo
        }

        double box_y = layer_2.getLayoutY() + layer_2.getHeight() - 50;
        context2.clearRect(0, 0, layer_2.getWidth(), layer_2.getHeight()); // brisanje
        context2.beginPath();
        // fill box background
        context2.setFill(Color.rgb(255, 0, 100));
        context2.fillRect(x, box_y, 105, 42);
        context2.setStroke(Color.rgb(0, 0, 0));
        // box boundary lines
        context2.setLineWidth(1);
        context2.setGlobalAlpha(0.6);
        context2.strokeRect(x,box_y , 105, 42);
        context2.setGlobalAlpha(1);
        // text u box-u
        Font font = Font.font("sans-serif", FontWeight.SEMI_BOLD, FontPosture.REGULAR, 12);
        context2.setFont(font);
        context2.setTextAlign(TextAlignment.LEFT);
//        var str = formatTimestamp(activity.getRecords().get((int) (graph_step * (pos-start_position_x)) - 1).getTimestamp());
//        context2.fillText(str, x + 2, 2 + font.getSize());
        context2.setFill(Color.rgb(255, 255, 255));
        var speed = activity.getRecords().get((int) (graph_step * (pos-start_position_x)) - 1).getSpeed() * 3.6;    // podatak je u m/s - prebacujemo u km ( speed )
        String speed_str = Format.formatDouble_GWT(speed, 1);
        context2.fillText("Speed: " + speed_str + " km/h", x + 2, box_y + 2 + font.getSize());
        context2.fillText("Power: " + activity.getRecords().get((int) (graph_step * (pos-start_position_x)) - 1).getPower()+ " W", x + 2, box_y + 2 + (font.getSize() * 2));
        context2.fillText("Grade: " + activity.getRecords().get((int) (graph_step * (pos-start_position_x)) - 1).getGrade() + " %", x + 2, box_y + 2 + (font.getSize() * 3));

        rotate.setAngle(activity.getRecords().get((int) (graph_step * (pos-start_position_x)) - 1).getRotation());
        context2.setFill(Color.rgb(0, 0, 0));
        // Formatiranje na 1 decimalu
        speed = activity.getRecords().get((int) (graph_step * (pos-start_position_x)) - 1).getWindSpeed();    // podatak je u km/h ( wind speed )
        speed_str = Format.formatDouble_GWT(speed, 1);
        if (speed > 25.0){
            context2.setFill(Color.rgb(255, 0, 0));
        }
        context2.fillText( "Wind Speed: " + speed_str + " km/h",0,60);
        context2.setFill(Color.rgb(0, 0, 0));
        context2.fillText("Wind dir: " + activity.getRecords().get((int) (graph_step * (pos-start_position_x)) - 1).getDirection(),0,80);
        context2.fillText("Heading: " + activity.getRecords().get((int) (graph_step * (pos-start_position_x)) - 1).getHeading(),0,100);
        context2.closePath();

        // brisemo desno
        if (pos >= layer_2.getLayoutX() + layer_2.getWidth() ) {
//            Console.log("newValue.intValue(): " + newValue.intValue() + ", pos: " + pos);
            context2.clearRect(0, 0, layer_2.getWidth(), layer_2.getHeight());
        }

        pos = Helper_light.map((long) newValue.doubleValue(),
                (long)(slider.getMin()+0L),
                (long)(slider.getMax()+0L),
                (long) (0L),
                (long) (layer_2.getWidth()));

        CyclingPower_Web_Local.map.plot_cursor_marker(activity.getRecords().get((int) (graph_step * (pos-start_position_x/2)) - 1).getLatitude(),
                activity.getRecords().get((int) (graph_step * (pos-start_position_x/2))).getLongitude());
//                Console.log("getX: " + ( event.getX() - start_position_x));
    }
}
