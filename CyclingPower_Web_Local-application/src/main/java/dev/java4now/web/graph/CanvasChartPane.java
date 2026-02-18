package dev.java4now.web.graph;

import dev.java4now.web.CyclingPower_Web_Local;
import dev.java4now.web.custom_ui.CustomMenuButton;
import dev.java4now.web.icons.Bootstrap_Icons;
import dev.java4now.web.model.CyclingActivity;
import dev.java4now.web.view.LeftPane;
import dev.webfx.extras.fonticons.FontIcons;
import dev.webfx.extras.fonticons.IconFont;
import dev.webfx.extras.fonticons.IconPack;
import dev.webfx.extras.fonticons.feather.FeatherIcon;
import dev.webfx.extras.fonticons.feather.FeatherPack;
import dev.webfx.platform.resource.Resource;
import dev.webfx.platform.useragent.UserAgent;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

import static dev.java4now.web.CyclingPower_Web_Local.activity;
import static dev.java4now.web.generator.SmoothElevationGenerator.generateSmoothHillyProfile;

public class CanvasChartPane extends Pane {

//    final ArrayList<Integer> random_data = CyclingElevationGenerator.generateHillyStageProfile(120, 166, 77, 300, 3);;
    final ArrayList<Integer> random_data = generateSmoothHillyProfile(120, 10, 77, 500, 3,1100);
    private final Canvas layer_1;
    private final Canvas layer_2;
    public static GraphicsContext context1;
    public static GraphicsContext context2;
    public static double start_position_x;                       // y osa
    public static double start_position_y;                      // x osa
    public static double step_vertical;
    public static double step_horizontale;
    public static int km_dist = 0;
    public static int max_alt = 0;
    public static int minElevation = 0;
    public static double graph_step;
    public static int max_power;
    public static double max_speed;
    public static int max_temperature;
    public static int min_temperature;
    public static Image image_tmp;
    public static Text icon = FontIcons.newText(FeatherIcon.CLOUD_OFF);
    static Pane icon_container = new Pane(icon);

    public static final StringProperty graph_type = new SimpleStringProperty("Distance");
    public static final StringProperty weather_image = new SimpleStringProperty("mm_api_symbols/wsymbol_0999_unknown.png");
    public static CustomMenuButton graph_menu = new CustomMenuButton("Graph");

    IconPack iconPack = FeatherPack.getInstance();
    IconFont iconFont = iconPack.getFonts()[0];
    public static Font default_font,bootstrap_font;

    static {
        bootstrap_font = Font.font(Resource.toUrl("fonts/bootstrap-icons.woff", LeftPane.class));
    }


    public CanvasChartPane(Canvas layer_1, Canvas layer_2) {
        super(layer_1, layer_2);
        this.layer_1 = layer_1;
        this.layer_2 = layer_2;
        context1 = this.layer_1.getGraphicsContext2D();
        context2 = this.layer_2.getGraphicsContext2D();
        layer_2.toFront();
        getChildren().add(graph_menu);

        FontIcons.applyFontCssClass(icon_container, iconFont);
        icon.getStyleClass().add("label_icon");
        getChildren().add(icon_container);
        default_font = icon.getFont();

//        generate_data();
        handleLayers();

        graph_type.addListener((observable, oldValue, newValue) -> {
            if ( ! newValue.equals(oldValue)){
                graph_menu.setText(newValue);
            }
        });
    }

    //-----------------------------------------------
    private void draw(double width, double height) {
        if (activity == null) {
            return;
        }

        if(GraphWind.is_visible){
            GraphWind.hide_wind();
        }

        switch (graph_type.get()) {
            case "Distance":
                GraphDistance.draw_distance(width,height,layer_1,layer_2,context1,context2);
                break;
            case "Speed":
                GraphSpeed.draw_speed(width,height,layer_1,layer_2,context1,context2);
                break;
            case "Power":
                GraphPower.draw_power(width,height,layer_1,layer_2,context1,context2);
                break;
            case "Temperature":
                GraphTemperature.draw_temperature(width,height,layer_1,layer_2,context1,context2);
                break;
            case "Wind":
//                GraphWind.wind_rotation.set(GraphWind.wind_rotation.get() + 30.0); // for debug
                context1.clearRect(0, 0, width, height);
                context2.clearRect(0, 0, width, height);
                GraphWind.draw_wind(width, height);
                break;
            default:
                GraphDistance.draw_distance(width,height,layer_1,layer_2,context1,context2);
                break;
        }
        draw_image(layer_1);
    }

    //--------------------------------------
    protected void layoutChildren() {
        super.layoutChildren();
        layer_1.setWidth(getWidth());
        layer_1.setHeight(getHeight());
        layer_1.setLayoutX(0);
        layer_1.setLayoutY(0);
        layer_2.setWidth(getWidth());
        layer_2.setHeight(getHeight());
        layer_2.setLayoutX(0);
        layer_2.setLayoutY(0);
        if(GraphWind.pane == null){
            GraphWind.create(layer_1.getWidth(),layer_1.getHeight(),this);
        }else{
            GraphWind.pane.setPrefWidth(layer_1.getWidth());
            GraphWind.pane.setLayoutX(layer_1.getLayoutX());
            GraphWind.pane.setLayoutY(0);
        }
        if(ProgressPane.root == null){
            ProgressPane.create(getWidth(),getHeight(),this);
        }else{
            if(ProgressPane.root.isVisible()){
                ProgressPane.root.setPrefWidth(getWidth());
                ProgressPane.root.setPrefHeight(getHeight());
                ProgressPane.root.setLayoutX(getWidth()/2 - ProgressPane.root.getPrefWidth()/2);
                if(CyclingPower_Web_Local.screen_height < 700){
                    if(UserAgent.isFireFox()){
//                        Console.log("firefox");
                        ProgressPane.root.setLayoutY(20);
                    }else{
                        ProgressPane.root.setLayoutY(0);
                    }
                }else{
                    ProgressPane.root.setLayoutY(getHeight() * 0.2);
                }
//                ProgressPane.root.setLayoutY(getHeight()/2 - ProgressPane.root.getPrefHeight()/2);
            }
        }
        graph_menu.setMaxWidth(getWidth() * 0.25);
        graph_menu.setLayoutX(0 /*layer_2.getWidth()/2 - graph_menu.getWidth()/2*/);
        graph_menu.setLayoutY(0);
//        Console.log("width: " + getWidth());
        if(activity != null){
            draw(getWidth(), getHeight());
        }
        icon_container.setLayoutX(getWidth() - icon.getLayoutBounds().getWidth() - 10);
        icon_container.setLayoutY(0);
    }


    //-------------------------------
    public void refresh(){
        this.layoutChildren(); // forsira layoutChildren() i u njemu draw
    }


    //---------------------------------------
    private void handleLayers() {

        layer_2.addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                switch (graph_type.get()) {
                    case "Distance":
                        GraphDistance.handle_distance(layer_2,event);
                        break;
                    case "Speed":
                        GraphSpeed.handle_speed(layer_2,event);
                        break;
                    case "Power":
                        GraphPower.handle_power(layer_2,event);
                        break;
                    case "Temperature":
                        GraphTemperature.handle_temperature(layer_2,event);
                        break;
                    default:
//                        GraphDistance.handle_distance(layer_2,event);
                        break;
                }
                draw_image(layer_1);
            }
        });
    }



    //----------------------------------------------
    public void handle_slider(Slider slider,Number newValue, Canvas layer_2){
        switch (graph_type.get()) {
            case "Distance":
                GraphDistance.handle_slider_distance(slider,newValue,layer_2);
                break;
            case "Speed":
                GraphSpeed.handle_slider_speed(slider,newValue,layer_2);
                break;
            case "Power":
                GraphPower.handle_slider_power(slider,newValue,layer_2);
                break;
            case "Temperature":
                GraphTemperature.handle_slider_temperature(slider,newValue,layer_2);
                break;
            case "Wind":
//                GraphWind.wind_rotation.set(GraphWind.wind_rotation.get() + 30.0); // for debug
//                context1.clearRect(0, 0, width, height);
//                context2.clearRect(0, 0, width, height);
                GraphWind.handle_slider_wind(slider,newValue,layer_2);
                break;
            default:
                GraphDistance.handle_slider_distance(slider,newValue,layer_2);
                break;
        }
        draw_image(layer_1);
    }



    //----------------------------------------------
    public void set_graph_data(CyclingActivity activity) {
        km_dist = (int) (( activity.getSession().getTotalDistance() + 500 ) / 1000);       // u metrima je record
        max_alt = (int) activity.getSession().getMaxAltitude();
        minElevation = (int) activity.getSession().getMinAltitude();
        max_power = findMaxPower(activity.getRecords());
        max_speed = findMaxSpeed(activity.getRecords());
        max_temperature = findMaxTemperature(activity.getRecords());
        min_temperature = findMinTemperature(activity.getRecords());
//        Console.log(km_dist + ", " + max_alt + ", " + minElevation);
        this.layoutChildren();
    }

    //----------------------------------------------
    public int findMaxPower(List<CyclingActivity.RecordData> records ) {
        return records.stream()
                .mapToInt(CyclingActivity.RecordData::getPower)
                .max()
                .orElse(0); // Default value if list is empty
    }

    //----------------------------------------------
    public double findMaxSpeed(List<CyclingActivity.RecordData> records ) {
        return records.stream()
                .mapToDouble(CyclingActivity.RecordData::getSpeed)
                .max()
                .orElse(0.0); // Default value if list is empty
    }

    //----------------------------------------------
    public int findMaxTemperature(List<CyclingActivity.RecordData> records ) {
        return records.stream()
                .mapToInt(CyclingActivity.RecordData::getTemperature)
                .max()
                .orElse(0); // Default value if list is empty
    }

    //----------------------------------------------
    public int findMinTemperature(List<CyclingActivity.RecordData> records ) {
        return records.stream()
                .mapToInt(CyclingActivity.RecordData::getTemperature)
                .min()
                .orElse(0); // Default value if list is empty
    }


    /*
    // old java way
    public int findMaxPower(List<RecordData> records) {
    if (records == null || records.isEmpty()) {
        return 0; // Or throw an exception, depending on your needs
    }
    int maxPower = records.get(0).getPower();
    for (RecordData record : records) {
        if (record.getPower() > maxPower) {
            maxPower = record.getPower();
        }
    }
    return maxPower;
}
     */


    public static void draw_image(Canvas layer_1){
        /*
        // za upotrebu WebFXUtil backgroundLoading mora biti true da bi imali image.progressProperty() ( radi i za openjfx i za GWT )
        image_tmp = new Image(Resource.toUrl(weather_image.get(), CyclingPower_Web.class), true);
        WebFXUtil.onImageLoaded(image_tmp, () -> {
            context1.drawImage(image_tmp, layer_1.getWidth() - image_tmp.getWidth() - 10, 0);
        });

         */
    }


    public static void draw_font_image(String icon_char){

        if (icon == null) {
            icon = new Text();
            icon.getStyleClass().addAll("label_icon","font-icon-bootstrap");
            icon_container.getChildren().add(icon);
        }else{
            icon.getStyleClass().clear();
            icon.getStyleClass().addAll("label_icon","font-icon-bootstrap");
        }
        icon.setFont(bootstrap_font);
        icon.setText(icon_char);
    }


    public static void draw_font_image(FeatherIcon featherIcon){
        /*
        icon_container.getChildren().clear();
        icon = FontIcons.newText(featherIcon);
        icon.getStyleClass().add("label_icon");
        icon_container.getChildren().add(icon);
         */
        if (icon == null) {
            icon = new Text();
            icon.getStyleClass().add("label_icon");
            icon_container.getChildren().add(icon);
        }else {
            icon.getStyleClass().clear();
            icon.getStyleClass().add("label_icon");
        }
        icon.setFont(default_font);
        icon.setText(String.valueOf(featherIcon.getChar()));
    }


    // ------------------ WMO CODE DESCRIPTION -----------------
    public static void Weather_Code_Description(int num) {
        switch (num) {
            case 0:
                weather_image.set("mm_api_symbols/wsymbol_0001_sunny.png");
                draw_font_image(FeatherIcon.SUN);
                break;
            case 1:
                weather_image.set("mm_api_symbols/wsymbol_0001_sunny.png");
                draw_font_image(FeatherIcon.SUN);
                break;
            case 2:
                weather_image.set("mm_api_symbols/wsymbol_0002_sunny_intervals.png");
                draw_font_image(FeatherIcon.SUN);
                break;
            case 3:
                weather_image.set("mm_api_symbols/wsymbol_0003_white_cloud.png");
                draw_font_image(FeatherIcon.CLOUD);
                break;
            case 45:
                weather_image.set("mm_api_symbols/wsymbol_0007_fog.png");
//                draw_font_image(FeatherIcon.CLOUD);
                draw_font_image(Bootstrap_Icons.getIcon("CLOUD_FOG"));
                break;
            case 48:
                weather_image.set("mm_api_symbols/wsymbol_0007_fog.png");
//                draw_font_image(FeatherIcon.CLOUD);
                draw_font_image(Bootstrap_Icons.getIcon("CLOUD_FOG"));
                break;
            case 51:
                weather_image.set("mm_api_symbols/wsymbol_0009_light_rain_showers.png");
                draw_font_image(FeatherIcon.CLOUD_RAIN);
                break;
            case 53:
                weather_image.set("mm_api_symbols/wsymbol_0048_drizzle.png");
                draw_font_image(FeatherIcon.CLOUD_DRIZZLE);
                break;
            case 55:
                weather_image.set("mm_api_symbols/wsymbol_0018_cloudy_with_heavy_rain.png");
                draw_font_image(FeatherIcon.CLOUD_RAIN);
                break;
            case 56:
                weather_image.set("mm_api_symbols/wsymbol_0013_sleet_showers.png");
                draw_font_image(FeatherIcon.CLOUD_RAIN);
                break;
            case 57:
                weather_image.set("mm_api_symbols/wsymbol_0050_freezing_rain.png");
                draw_font_image(FeatherIcon.CLOUD_RAIN);
                break;
            case 61:
                weather_image.set("mm_api_symbols/wsymbol_0009_light_rain_showers.png");
                draw_font_image(FeatherIcon.CLOUD_RAIN);
                break;
            case 63:
                weather_image.set("mm_api_symbols/wsymbol_0048_drizzle.png");
                draw_font_image(FeatherIcon.CLOUD_DRIZZLE);
                break;
            case 65:
                weather_image.set("mm_api_symbols/wsymbol_0018_cloudy_with_heavy_rain.png");
                draw_font_image(FeatherIcon.CLOUD_RAIN);
                break;
            case 66:
                weather_image.set("mm_api_symbols/wsymbol_0013_sleet_showers.png");
                draw_font_image(FeatherIcon.CLOUD_RAIN);
                break;
            case 67:
                weather_image.set("mm_api_symbols/wsymbol_0050_freezing_rain.png");
                draw_font_image(FeatherIcon.CLOUD_RAIN);
                break;
            case 71:
                weather_image.set("mm_api_symbols/wsymbol_0011_light_snow_showers.png");
                draw_font_image(FeatherIcon.CLOUD_SNOW);
                break;
            case 73:
                weather_image.set("mm_api_symbols/wsymbol_0020_cloudy_with_heavy_snow.png");
                draw_font_image(FeatherIcon.CLOUD_SNOW);
                break;
            case 75:
                weather_image.set("mm_api_symbols/wsymbol_0020_cloudy_with_heavy_snow.png");
                draw_font_image(FeatherIcon.CLOUD_SNOW);
                break;
            case 77:
                weather_image.set("mm_api_symbols/wsymbol_0020_cloudy_with_heavy_snow.png");
                draw_font_image(FeatherIcon.CLOUD_SNOW);
                break;
            case 80:
                weather_image.set("mm_api_symbols/wsymbol_0009_light_rain_showers.png");
                draw_font_image(FeatherIcon.CLOUD_RAIN);
                break;
            case 81:
                weather_image.set("mm_api_symbols/wsymbol_0048_drizzle.png");
                draw_font_image(FeatherIcon.CLOUD_DRIZZLE);
                break;
            case 82:
                weather_image.set("mm_api_symbols/wsymbol_0018_cloudy_with_heavy_rain.png");
                draw_font_image(FeatherIcon.CLOUD_RAIN);
                break;
            case 85:
                weather_image.set("mm_api_symbols/wsymbol_0011_light_snow_showers.png");
                draw_font_image(FeatherIcon.CLOUD_SNOW);
                break;
            case 86:
                weather_image.set("mm_api_symbols/wsymbol_0020_cloudy_with_heavy_snow.png");
                draw_font_image(FeatherIcon.CLOUD_SNOW);
                break;
            case 95:
                weather_image.set("mm_api_symbols/wsymbol_0024_thunderstorms.png");
                draw_font_image(FeatherIcon.CLOUD_LIGHTNING);
                break;
            case 96:
                weather_image.set("mm_api_symbols/wsymbol_0024_thunderstorms.png");
                draw_font_image(FeatherIcon.CLOUD_LIGHTNING);
                break;
            case 99:
                weather_image.set("mm_api_symbols/wsymbol_0024_thunderstorms.png");
                draw_font_image(FeatherIcon.CLOUD_LIGHTNING);
                break;
            // Thunderstorm forecast with hail is only available in Central Europe
            default:
                weather_image.set("mm_api_symbols/wsymbol_0999_unknown.png");
                draw_font_image(FeatherIcon.CLOUD_OFF);
//                draw_font_image(Bootstrap_Icons.getIcon("CLOUD_FOG")); // zbog testiranja
                break;
        }
    }
}
