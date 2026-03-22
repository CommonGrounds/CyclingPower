package dev.java4now.web.graph;

import dev.java4now.web.CyclingPower_Web_Local;
import dev.java4now.web.util.Format;
import dev.java4now.web.util.Helper_light;
import dev.java4now.web.util.WebFXUtil;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.os.OperatingSystem;
import dev.webfx.platform.resource.Resource;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.scene.transform.Rotate;

import static dev.java4now.web.CyclingPower_Web_Local.activity;
import static dev.java4now.web.graph.CanvasChartPane.*;

public class GraphWind {

    static Group north;
    static Rotate north_rotate;
    static Group south;
    static Rotate south_rotate;
    static Group east;
    static Rotate east_rotate;
    static Group west;
    static Rotate west_rotate;
    static Arc wind_arc;
    static Circle circle_out,circle_inner;
    static Rotate rotate;
    static double CIRCLE_SCALE = 0.9;
    public static Pane pane;
    static Label legend;
    public static boolean is_visible = false;
    static GraphicsContext context;
    static Image bike_img = new Image(Resource.toUrl("pics/bike_above.png", CyclingPower_Web_Local.class), true);
    static Image bike_img_small = new Image(Resource.toUrl("pics/bike_above_small.png", CyclingPower_Web_Local.class), true);
    static Image bike_img_final;
    static ImageView imageView = new ImageView();
    public static final DoubleProperty wind_rotation = new SimpleDoubleProperty(0.0);
    public static final DoubleProperty north_angle = new SimpleDoubleProperty(0.0);
    public static final DoubleProperty south_angle = new SimpleDoubleProperty(180.0);
    public static final DoubleProperty east_angle = new SimpleDoubleProperty(90.0);
    public static final DoubleProperty west_angle = new SimpleDoubleProperty(270.0);
    static final DoubleProperty gap = new SimpleDoubleProperty(12.0);


    public static void create(double width, double height, Pane parent) {
        circle_out = new Circle();
        circle_inner = new Circle();
        wind_arc = draw_arc();

//                rotate.setAngle(270);
//                circle.setTranslateX(getWidth()/2 - circle.getRadius()/2);
//                circle.setTranslateY(getHeight()/2 - circle.getRadius()/2);
        circle_out.setStroke(Color.BLACK);
        circle_inner.setStroke(Color.BLACK);

        setup_compass_direction_name();


        if(CyclingPower_Web_Local.screen_width < 810 || CyclingPower_Web_Local.screen_height < 610){
            bike_img_final = bike_img_small;
        }else{
            bike_img_final = bike_img;
        }

        legend = new Label("Wind");
//        legend.setFont(Font.font("sans-serif", FontWeight.BOLD, FontPosture.REGULAR, 12));
        legend.setTextFill(Color.GRAY);
        rotate.setAngle(wind_rotation.get());

        pane = new Pane(circle_out,circle_inner, wind_arc, imageView,legend,north,east,south,west){
            @Override
            public void layoutChildren() {
                super.layoutChildren();
                if (is_visible) {
//                    Console.log("width: " + getWidth() + ", height: " + getHeight() + ", width: " + width + ", height: " + height);
                    double width;
                    double height;
                    if(!OperatingSystem.isMobile()){
                        width = getWidth();
                        height = getHeight();
                    }else{
                        width = getHeight();
                        height = getWidth();
                    }

                    gap.set(0.1 * height);
                    circle_out.setRadius((height / 2) * CIRCLE_SCALE);
                    circle_inner.setRadius((height / 2) * CIRCLE_SCALE - gap.get());

                    wind_arc.setRadiusX(2*gap.get());
                    wind_arc.setRadiusY(2*gap.get());
                    circle_out.setCenterX(width / 2);
                    circle_out.setCenterY(height / 2);
                    circle_inner.setFill(new ImagePattern(imageView.getImage()));
                    wind_arc.setCenterX(width / 2);
                    wind_arc.setCenterY(height/ 2 - circle_out.getRadius() + wind_arc.getRadiusY());
                    rotate.setPivotX(width / 2); // tacka rotacije po x
                    rotate.setPivotY(height / 2); // tacka rotacije po y

                    north.setLayoutX(width/2 - north.getLayoutBounds().getWidth() / 2);
                    north.setLayoutY(height / 2 - circle_out.getRadius() + ( gap.get()/2 - north.getLayoutBounds().getHeight() )/2 );
                    north_rotate.setPivotX( 0 );
                    north_rotate.setPivotY( circle_out.getRadius() -  ( north.getLayoutBounds().getHeight() )/2 );
                    south.setLayoutX(width/2 - south.getLayoutBounds().getWidth() / 2);
                    south.setLayoutY(height/2  - circle_out.getRadius() + ( gap.get()/2 - south.getLayoutBounds().getHeight() )/2  );
                    south_rotate.setPivotX( 0 );
                    south_rotate.setPivotY(circle_out.getRadius() -  ( south.getLayoutBounds().getHeight() )/2 );
                    east.setLayoutX(width/2 - east.getLayoutBounds().getWidth() / 2 );
                    east.setLayoutY(height / 2  - circle_out.getRadius() + (gap.get()/2 - east.getLayoutBounds().getHeight() )/2);
                    east_rotate.setPivotX( 0 );
                    east_rotate.setPivotY( circle_out.getRadius() -  ( east.getLayoutBounds().getHeight() )/2 );
                    west.setLayoutX(width / 2 - west.getLayoutBounds().getWidth() / 2);
                    west.setLayoutY( height / 2  - circle_out.getRadius() + (gap.get()/2 - west.getLayoutBounds().getHeight() )/2 );
                    west_rotate.setPivotX( 0 );
                    west_rotate.setPivotY( circle_out.getRadius() -  ( west.getLayoutBounds().getHeight() )/2 );

                        circle_out.setVisible(true);
                        circle_inner.setVisible(true);

                    legend.relocate(width - legend.getWidth()-10, height - legend.getHeight()-10 );
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

        circle_inner.centerYProperty().bind(circle_out.centerYProperty());
        circle_inner.centerXProperty().bind(circle_out.centerXProperty());

//        pane.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
        pane.setVisible(false);
        is_visible = false;
    }


    //--------------------------------------------------
    public static void draw_wind(double width, double height) {
        Console.log("draw_wind - width: " + width + ", height: " + height);
        boolean redraw_height = false;
        if(width > height){
            circle_out.setRadius((height / 2) * CIRCLE_SCALE);
        }else{
            CIRCLE_SCALE = 0.5;
            circle_out.setRadius((width / 2) * CIRCLE_SCALE);
            redraw_height = true;
        }

        circle_out.setCenterX(width / 2);
        circle_out.setCenterY(height / 2);
        wind_arc.setCenterX(width / 2);
        wind_arc.setCenterY(height / 2 - circle_out.getRadius() + wind_arc.getRadiusY());
        rotate.setPivotX(width / 2); // tacka rotacije po x
        rotate.setPivotY(height / 2); // tacka rotacije po y
//                rotate.setAngle(270);
//                circle.setTranslateX(getWidth()/2 - circle.getRadius()/2);
//                circle.setTranslateY(getHeight()/2 - circle.getRadius()/2);
        imageView.setFitWidth(bike_img_final.getWidth() );   // * CIRCLE_SCALE
        imageView.setFitHeight(bike_img_final.getHeight() ); // * CIRCLE_SCALE
        imageView.setX(circle_out.getCenterX() - imageView.getFitWidth()/2);
        imageView.setY(circle_out.getCenterY() - imageView.getFitHeight()/2);
        circle_out.setStroke(Color.BLACK);

        north.setLayoutX(width/2 - north.getLayoutBounds().getWidth() / 2);
        south.setLayoutX(width/2 - south.getLayoutBounds().getWidth() / 2);
        east.setLayoutX(width/2 - east.getLayoutBounds().getWidth() / 2 );
        west.setLayoutX(width / 2 - west.getLayoutBounds().getWidth() / 2);
        if (redraw_height) {
            north.setLayoutY(height / 2 - circle_out.getRadius() + ( gap.get()/2 - north.getLayoutBounds().getHeight() - 15 )/2 );
            south.setLayoutY(height/2  - circle_out.getRadius() + ( gap.get()/2 - south.getLayoutBounds().getHeight() - 15 )/2  );
            east.setLayoutY(height / 2  - circle_out.getRadius() + (gap.get()/2 - east.getLayoutBounds().getHeight() - 15 )/2);
            west.setLayoutY( height / 2  - circle_out.getRadius() + (gap.get()/2 - west.getLayoutBounds().getHeight() - 15 )/2 );
        }

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
        north_angle.set(- heading_direction_angle( activity.getRecords().get((int) (graph_step * (pos-start_position_x)) - 1).getHeading() ));
        south_angle.set(180 - heading_direction_angle( activity.getRecords().get((int) (graph_step * (pos-start_position_x)) - 1).getHeading() ));
        east_angle.set(90 - heading_direction_angle( activity.getRecords().get((int) (graph_step * (pos-start_position_x)) - 1).getHeading() ));
        west_angle.set(270 - heading_direction_angle( activity.getRecords().get((int) (graph_step * (pos-start_position_x)) - 1).getHeading() ));
//        rotate.setAngle(heading_direction_angle(activity.getRecords().get((int) (graph_step * (pos-start_position_x)) - 1).getDirection()) -
//                heading_direction_angle( activity.getRecords().get((int) (graph_step * (pos-start_position_x)) - 1).getHeading() ));
//        Console.log("rotate.getAngle(): " + rotate.getAngle());
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


    //---------------------------------------------------
    private static Arc draw_arc() {
        Arc arc = new Arc();
        arc.setCenterX(0);
        arc.setCenterY(0);
        arc.setRadiusX(32);
        arc.setRadiusY(32);
        arc.setStartAngle(55);    // pocetni ugao desno - 55 + 35 = 90 + 35 = 125 ( 55 + 70 ) - da bi bio centar na 12 sati
        arc.setLength(70);        // od pocetnog ugla
        arc.setType(ArcType.ROUND);
        arc.setFill(Color.rgb(255,0,0,0.7)); // pocetna boja

        rotate = new Rotate();

        //setting properties for the rotate object.
        rotate.setAngle(0);
        rotate.setPivotX(0);     // tacka rotacije po x
        rotate.setPivotY(0);     // tacka rotacije po y
        arc.getTransforms().add(rotate);

        return arc;
    }


    //---------------------------------------------------
    private static void setup_compass_direction_name() {
        Text text_n = new Text("N");
        north = new Group(text_n);
//        north.setFont(Font.font("Arial", FontWeight.BOLD, 10));
//        north.setStyle("-fx-font-family: 'Roboto'; -fx-font-size: 9pt; -fx-font-weight: bold;");
        north_rotate = new Rotate();
        //setting properties for the rotate object.
        north_rotate.setPivotX(0);     // tacka rotacije po x
        north_rotate.setPivotY(0);     // tacka rotacije po y
        north.getTransforms().add(north_rotate);
        north.layoutXProperty().addListener((observable, oldValue, newValue) -> {
            text_n.setTranslateX(- text_n.getBoundsInLocal().getWidth() / 2);
        });

        Text text_s = new Text("S");
        south = new Group(text_s);
//        south.setFont(Font.font("Arial", FontWeight.BOLD, 10));
//        south.setStyle("-fx-font-family: 'Roboto'; -fx-font-size: 9pt; -fx-font-weight: bold;");
        south_rotate = new Rotate();
//        south_rotate.setAngle(0);
        south_rotate.setPivotX(0);     // tacka rotacije po x
        south_rotate.setPivotY(0);     // tacka rotacije po y
        south.getTransforms().add(south_rotate);
        south.layoutXProperty().addListener((observable, oldValue, newValue) -> {
            text_s.setTranslateX(- text_s.getBoundsInLocal().getWidth() / 2);
        });

        Text text_e = new Text("E");
        east = new Group(text_e);
//        east.setFont(Font.font("Arial", FontWeight.BOLD, 10));
//        east.setStyle("-fx-font-family: 'Roboto'; -fx-font-size: 9pt; -fx-font-weight: bold;");
        east_rotate = new Rotate();
//        east_rotate.setAngle(0);
        east_rotate.setPivotX(0);     // tacka rotacije po x
        east_rotate.setPivotY(0);     // tacka rotacije po y
        east.getTransforms().add(east_rotate);
        east.layoutXProperty().addListener((observable, oldValue, newValue) -> {
            text_e.setTranslateX(- text_e.getBoundsInLocal().getWidth() / 2);
        });

        Text text_w = new Text("W");
        west = new Group(text_w);
//        west.setFont(Font.font("Arial", FontWeight.BOLD, 10));
//        west.setStyle("-fx-font-family: 'Roboto'; -fx-font-size: 9pt; -fx-font-weight: bold;");
        west_rotate = new Rotate();
//        west_rotate.setAngle(0);
        west_rotate.setPivotX(0);     // tacka rotacije po x
        west_rotate.setPivotY(0);     // tacka rotacije po y
        west.getTransforms().add(west_rotate);
        west.layoutXProperty().addListener((observable, oldValue, newValue) -> {
            text_w.setTranslateX(- text_w.getBoundsInLocal().getWidth() / 2);
        });

        north_rotate.angleProperty().bind(north_angle);
        south_rotate.angleProperty().bind(south_angle);
        east_rotate.angleProperty().bind(east_angle);
        west_rotate.angleProperty().bind(west_angle);

        gap.addListener((obs, oldVal, newVal) -> {
            text_n.setFont(Font.font(newVal.doubleValue() * 0.8));
            text_s.setFont(Font.font(newVal.doubleValue() * 0.8));
            text_e.setFont(Font.font(newVal.doubleValue() * 0.8));
            text_w.setFont(Font.font(newVal.doubleValue() * 0.8));
        });
    }


    //-----------------------------------------------------
    static String[] dirs = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
    static int[] angles = {0, 45, 90, 135, 180, 225, 270, 315};

    public static int heading_direction_angle(String direction) {
        if (direction.isEmpty()){
            return 0;
        }
        for (int i = 0; i < dirs.length; i++) {
            if (dirs[i].equals(direction)) {
                return angles[i];
            }
        }
        // Ako smjer nije pronađen, možeš baciti izuzetak ili vratiti -1
//        throw new IllegalArgumentException("Nepoznat smjer: " + direction);
        return 0;
    }
}
