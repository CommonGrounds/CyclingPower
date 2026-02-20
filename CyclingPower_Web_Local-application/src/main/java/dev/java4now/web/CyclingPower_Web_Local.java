package dev.java4now.web;

import dev.java4now.web.CityLocation.CityService;
import dev.java4now.web.charts.MovingChart;
import dev.java4now.web.charts.SummaryChart;
import dev.java4now.web.custom_ui.CustomDialog;
import dev.java4now.web.custom_ui.CustomTooltip;
import dev.java4now.web.custom_ui.TooltipHelper;
import dev.java4now.web.effects.Animations;
import dev.java4now.web.graph.CanvasChartPane;
import dev.java4now.web.graph.ProgressPane;
import dev.java4now.web.http.IPCodes;
import dev.java4now.web.http.SimpleBase64;
import dev.java4now.web.icons.Feather_Icons;
import dev.java4now.web.icons.FontAwesomeSolid_Icons;
import dev.java4now.web.icons.Ionicons;
import dev.java4now.web.maps.Leaflet;
import dev.java4now.web.model.CyclingActivity;
import dev.java4now.web.model.NavigableActivityLinkedList;
import dev.java4now.web.model.futures_fetcher;
import dev.java4now.web.util.Format;
import dev.java4now.web.view.LeftPane;
import dev.java4now.web.view.RightPane;
import dev.java4now.web.view.UpPane;
import dev.java4now.web.websocket.WebSocketClient;
import dev.webfx.platform.ast.ReadOnlyAstArray;
import dev.webfx.platform.ast.ReadOnlyAstObject;
import dev.webfx.platform.fetch.Fetch;
import dev.webfx.platform.ast.json.Json;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.resource.Resource;
import dev.webfx.platform.windowlocation.WindowLocation;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane; // IMPORTANT - mora explicite za scrollPane
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;

import dev.webfx.platform.os.OperatingSystem;
import dev.webfx.platform.fetch.FetchOptions;
import dev.webfx.platform.fetch.Headers;
import javafx.util.Duration;
import service.Service_impl;


import java.util.Optional;

import static dev.java4now.web.Settings.name_txt;
import static dev.java4now.web.util.Helper_light.formatTimestamp;
import static dev.java4now.web.view.LeftPane.*;
import static dev.java4now.web.view.UpPane.fetch_all_btn;
import static dev.java4now.web.view.UpPane.jsonFileListView;
import static dev.java4now.web.websocket.WebSocketClient.startWebSocket;

public class CyclingPower_Web_Local extends Application {

//##############################################################
    public static boolean PRODUCTION = true;
//##############################################################

    public static String BASE_URL = "http://localhost:8080";                                        // local - 8080 sa clone serverom , 8880 sa orig
    public static String websocket_url = "ws://localhost:8080/ws";                                  // local
    public static String IMAGE_UPLOAD_URL = "http://localhost:8080/api/upload-image";
//    public static final String IMAGE_UPLOAD_URL = "https://cyclingpower-server-1.onrender.com/api/upload-image";
//    public static String BASE_URL = "https://cyclingpower-server-1.onrender.com";                 // http://localhost:8880
//    public static String websocket_url = "wss://cyclingpower-server-1.onrender.com/ws";           // "ws://localhost:8880/ws";

    public static StackPane uber_root = new StackPane();
    public static VBox pictures_pane = new VBox();
    public static double screen_width, screen_height;
    public static dev.java4now.web.model.CyclingActivity activity;
    NavigableActivityLinkedList.ActivityEntry entry;
    public static Leaflet map;
    double side_ratio = (double) 1 / 5;
    double center_ratio = (double) 3 / 5;
    Canvas layer_1;
    Canvas layer_2;
    public static CanvasChartPane graphicon;
    public static Image default_pic;
    public static VBox pie_chart;
    public static VBox bar_chart;
    public static CityService cityService;

    //--------- Left Pane Controls --------------
    public static Label name_lbl;
    public static HBox name_group;
    public static Label server_lbl;
    public static Label path_lbl;
    //--------- Up Pane Controls --------------
    public static Button pastafarian;
    //-----------------------------------------

    public static final BooleanProperty IS_TOTAL_TIME = new SimpleBooleanProperty(false);

    public static final StringProperty device = new SimpleStringProperty("Waiting...");
    public static final StringProperty server_txt = new SimpleStringProperty("---");
    public static final StringProperty path_txt = new SimpleStringProperty("---");

    public static final StringProperty moving_time = new SimpleStringProperty("0.0");
    public static final StringProperty avg_speed = new SimpleStringProperty("0.0");
    public static final StringProperty total_distance = new SimpleStringProperty("0.0");
    public static final StringProperty avg_power = new SimpleStringProperty("0.0");
    public static final StringProperty minimum_alt = new SimpleStringProperty("0.0");
    public static final StringProperty maximum_alt = new SimpleStringProperty("0.0");
    public static final StringProperty total_cal = new SimpleStringProperty("0.0");
    public static final StringProperty avg_cad = new SimpleStringProperty("0");

    //    public static final StringProperty url = new SimpleStringProperty("http://localhost:8880/api/download-json/test_20250202134629_1741252852840.json");
    public static final StringProperty url = new SimpleStringProperty("");
    public static final StringProperty user_url = new SimpleStringProperty("http://localhost:8880/api/download-json/test_20250202134629_1741252852840.json");
    public static final BooleanProperty data_is_ready = new SimpleBooleanProperty(false);
    public static final BooleanProperty left_button_disabled = new SimpleBooleanProperty(false);
    public static final BooleanProperty right_button_disabled = new SimpleBooleanProperty(false);
    public static final BooleanProperty have_cadence = new SimpleBooleanProperty(false);
    public static final SimpleObjectProperty<Text> graphic_icon = new SimpleObjectProperty<>(new Text(Ionicons.getIcon("ION_NAVIGATE")));

    static int counter;

    static Image title_icon_dark = new Image(Resource.toUrl("pics/favicon_dark.ico", CyclingPower_Web_Local.class), false);
    static Image title_icon_light = new Image(Resource.toUrl("pics/favicon_light.ico", CyclingPower_Web_Local.class), false);
//    public static Image pane_border = new Image(Resource.toUrl("pics/brown-square-texture_10.png", CyclingPower_Web_Local.class), false);
    static String tooltip_css = Resource.toUrl("css/tooltip.css", CyclingPower_Web_Local.class);
    boolean pane_managed = false;

    @Override
    public void start(Stage primaryStage) {

        if (PRODUCTION){
            BASE_URL = "https://cyclingpower-server-1.onrender.com";
            websocket_url = "wss://cyclingpower-server-1.onrender.com/ws";
            IMAGE_UPLOAD_URL = "https://cyclingpower-server-1.onrender.com/api/upload-image";
        }

//        Console.info("Logging info");
//        Console.log("Logging log");
//        Console.debug("Logging debug");
//        Console.warn("Logging warn");
//        Console.error("Logging error");
//        Console.logNative("Logging native");

//        Console.log(CyclingPower_Web.class.getName() + "/data/cities1000.txt");
//        Console.log("%s".formatted("Hello"));
        Settings.loadState();
        Settings.getScreenSize();

        // important kada se dobije message from server da ima novi update ( novi json )
        data_is_ready.addListener((obs, ov, nv) -> {
            if (nv) {
                //  in javaFX only the FX thread can modify the UI elements
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        fetch_data();
                        fetch_all_btn.setDisable(false);
                        data_is_ready.set(false);
                    }
                });
            }
        });

        if (OperatingSystem.isMobile()) {
            Console.log("Mobile device detected");
//            side_ratio = 0;
//            center_ratio = 1;
        }
        map = new Leaflet();
        layer_1 = new Canvas();
        layer_2 = new Canvas();
        graphicon = new CanvasChartPane(layer_1, layer_2);
        graphicon.widthProperty().addListener((obs, ov, nv) -> {
            ProgressPane.graphicon_width = nv.doubleValue();
        });
        graphicon.heightProperty().addListener((obs, ov, nv) -> {
            ProgressPane.graphicon_height = nv.doubleValue();
        });

        //--------- Left Pane Controls --------------
        name_lbl = new Label("log Name");
        var name_lbl_2 = new Label();
        name_lbl_2.setTextFill(Color.BLUEVIOLET);
        name_lbl_2.textProperty().bind(name_txt);
        String nameIconChar = Feather_Icons.getChar("REFRESH_CW");
        var name_btn = new Text(nameIconChar);
        name_btn.setFont(font);
        name_btn.getStyleClass().addAll("font-icon-button_fe", "name_btn");
        name_btn.setOnMouseEntered( e -> TooltipHelper.showQuickTooltip(name_btn,"Change name", CustomTooltip.TooltipPosition.RIGHT));
        name_btn.setOnMouseClicked(e -> {
            // Custom dialog setup
            CustomDialog dialog = new CustomDialog("Change name", input -> {
                Console.log("Dialog result: " + input.toString());
                WindowLocation.assignHref(WindowLocation.getHref()); // important - reload
            });
            dialog.show(uber_root);
        });
        name_group = new HBox(10,name_btn,name_lbl_2);
        name_group.setAlignment(Pos.CENTER);
        server_lbl = new Label();
        server_lbl.textProperty().bind(server_txt);
        path_lbl = new Label();
        path_lbl.textProperty().bind(path_txt);
        //--------- Up Pane Controls --------------
        String menuIconChar = FontAwesomeSolid_Icons.getIcon("PASTAFARIANISM");
        pastafarian = new Button(menuIconChar);
        pastafarian.setTextFill(Color.BLUEVIOLET);
        pastafarian.setFont(font_fa);                             // font_fa
        pastafarian.getStyleClass().addAll("font-icon-button_fa", "font-icon-button");   // font-icon-button_fa za FontAwesomeSolid ikone
        String pastafarian_url = "https://www.spaghettimonster.org";
        pastafarian.setOnAction(e -> {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    getHostServices().showDocument(pastafarian_url);
                }
            });
        });
        //-----------------------------------------

        String rightIconChar = Feather_Icons.getChar("CHEVRON_RIGHT"); // \e09c
        var right_btn = new Button(rightIconChar);
        right_btn.setTextFill(Color.RED);
        right_btn.setFont(font);
        right_btn.getStyleClass().add("font-chevron-button");
        right_btn.disableProperty().bind(right_button_disabled);
        right_btn.setOnAction(e -> {
            futures_fetcher.navigateNext();
//            jsonFileListView.selectItem(futures_fetcher.final_list.get());
        });

        String leftIconChar = Feather_Icons.getChar("CHEVRON_LEFT"); // \e09c
        var left_btn = new Button(leftIconChar);
        left_btn.setTextFill(Color.RED);
        left_btn.setFont(font);
        left_btn.getStyleClass().add("font-chevron-button");
        left_btn.disableProperty().bind(left_button_disabled);
        left_btn.setOnAction(e -> {
            futures_fetcher.navigatePrevious();
        });

        var stack = new StackPane(map.webView, right_btn, left_btn) {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                map.webView.setLayoutX(0);
                map.webView.setLayoutY(0);
                right_btn.setLayoutX(getWidth() - right_btn.getWidth());
                right_btn.setLayoutY(getHeight() / 2 - right_btn.getHeight() / 2);
                left_btn.setLayoutX(0);
                left_btn.setLayoutY(getHeight() / 2 - left_btn.getHeight() / 2);
            }
        };

        Slider slider = new Slider(0, 100, 0);
        slider.getStyleClass().add("custom-slider");
        slider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
//            Console.log("newValue.intValue(): " + newValue.intValue());
            graphicon.handle_slider(slider, newValue, layer_2);
        });

        var slider_box = new HBox(slider) {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                slider.setPrefWidth(graphicon.getWidth());
                slider.setMin(CanvasChartPane.start_position_x);
                slider.setMax(graphicon.getWidth());
            }
        };
        slider_box.setAlignment(Pos.TOP_CENTER);

        var right_pane = RightPane.getPane();
        var middle_box = new VBox(stack, graphicon, slider_box);
        StackPane left_pane = LeftPane.get_pane(this);

        var content_vbox = new VBox();
        var scroll = new ScrollPane(); // Controls.createVerticalScrollPane(content_vbox);
        var content_box = new HBox(left_pane, middle_box, right_pane);
        VBox root = new VBox() {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
//                screen_width = getWidth();
//                screen_height = getHeight();

                // Mobile layout
                if (screen_width < 800 || screen_height < 600) {
                    if (!pane_managed) {
                        pane_managed = true;
                        getChildren().removeAll(content_box);
                        content_vbox.getChildren().addAll(left_pane, middle_box, right_pane);
                        scroll.setContent(content_vbox);
                        getChildren().addAll(scroll);
                        content_vbox.setAlignment(Pos.CENTER);
                        content_vbox.setSpacing(10);
                        content_vbox.setPadding(new Insets(10, 10, 10, 10));
                        content_vbox.setPrefWidth(getWidth());
                        content_vbox.setPrefHeight(getHeight());
//                        content_vbox.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200, .7), null, null)));
                        content_vbox.setMinWidth(getWidth());
//                        right_pane.setPrefWidth(getWidth());
//                        MovingChart.resize(getWidth()/3);
                        if (right_pane.getChildren().contains(pie_chart)) {
                            right_pane.getChildren().remove(pie_chart);
                            pie_chart = null;
                            pie_chart = MovingChart.get_chart();
                            right_pane.getChildren().add(2, pie_chart);
    //                        Console.log("pie_chart index: " + right_pane.getChildren().indexOf(pie_chart) + ", size: " + right_pane.getChildren().size());
                            pie_chart.setPrefWidth(getWidth() / 2);
                            pie_chart.setPrefHeight(getHeight() / 3);
                            pie_chart.setMinWidth(getWidth() / 2);
                            pie_chart.setMinHeight(getHeight() / 3);
                            pie_chart.setMaxWidth(getWidth() / 2);
                            pie_chart.setMaxHeight(getHeight() / 3);
                            pie_chart.setAlignment(Pos.CENTER);
                            //                           pie_chart.setPadding(new Insets(10, 0, 10, 40)); // left korekcija jer chart nije simetrican zbog axes - ako je 1. el.
                            pie_chart.setPadding(new Insets(0, 0, 0, screen_width < 500 ? 30 : 40)); // left korekcija ako nije 1. el.
//                            Console.log("chart size: " + pie_chart.getWidth() + "," + pie_chart.getHeight() + ", x: " + pie_chart.getLayoutX() + ", y: " + pie_chart.getLayoutY());
//                            pie_chart.setLayoutX(right_pane.getWidth()/2 - pie_chart.getWidth()/2);
                            int index = right_pane.getChildren().indexOf(server_lbl);
//                            Console.log("index: " + index);
                            right_pane.getChildren().remove(index);
                            right_pane.getChildren().add(index, server_lbl);
                            index = right_pane.getChildren().indexOf(path_lbl);
                            right_pane.getChildren().remove(index);
                            right_pane.getChildren().add(index, path_lbl);
                            index = right_pane.getChildren().indexOf(name_lbl);
                            right_pane.getChildren().remove(index);
                            right_pane.getChildren().add(index, name_lbl);
                            index = right_pane.getChildren().indexOf(name_group);
                            right_pane.getChildren().remove(index);
                            right_pane.getChildren().add(index, name_group);
//                            if (right_pane.getChildren().contains(bar_chart)){
                            index = right_pane.getChildren().indexOf(bar_chart);
                            right_pane.getChildren().remove(index);
                            bar_chart = null;
                            bar_chart = SummaryChart.get_chart();
                            right_pane.getChildren().add(index, bar_chart);
                            bar_chart.setPrefWidth(getWidth());
                            bar_chart.setPrefHeight(getHeight() / 3);
                            bar_chart.setMinHeight(getHeight() / 3);
                            if (screen_width < 600) {
                                bar_chart.setMinWidth(getWidth());
                                bar_chart.setMaxWidth(getWidth());
                            }else{
                                bar_chart.setMinWidth(getWidth()/2);
                                bar_chart.setMaxWidth(getWidth()/2);
                            }
                            bar_chart.setMaxHeight(getHeight() / 3);
                            bar_chart.setAlignment(Pos.CENTER);
                            bar_chart.setPadding(new Insets(0, 0, 0, screen_width < 500 ? 30 : 40)); // left korekcija ako nije 1. el.
//                            }
                            if (right_pane.getChildren().contains(pastafarian)) {
                                index = right_pane.getChildren().indexOf(pastafarian);
                                right_pane.getChildren().remove(index);
                                right_pane.getChildren().add(index, pastafarian); // last
                            }
                        }
                    }
                    //                   left_pane.setVisible(false);
                    //                   right_pane.setVisible(false);
                    if (middle_box.getHeight() < 400) {
//                          stack.setPrefHeight(middle_box.getHeight()/2);
                        graphicon.setPrefHeight(getHeight());
                    }
                    //                   chart.setPrefHeight((middle_box.getHeight() - slider_box.getHeight()) / 2); // TODO
                    Console.log("middle_box-height:width - " + middle_box.getHeight() + ":" + middle_box.getWidth());
                    middle_box.setMinHeight(getHeight());
                    middle_box.setPrefWidth(getWidth());
                }
                // Desktop layout
                else {
                    left_pane.setVisible(true);
                    right_pane.setVisible(true);
                    left_pane.setPrefWidth(getWidth() * side_ratio);
                    right_pane.setPrefWidth(getWidth() * side_ratio);
                    middle_box.setPrefWidth(getWidth() * center_ratio);
//                    middle_box.setPrefHeight(getHeight()*0.9);
                    graphicon.setPrefHeight((middle_box.getHeight() - slider_box.getHeight()) / 2);
//                    stack.setPrefHeight((middle_box.getHeight() - slider_box.getHeight()) / 2);
                }
            }
        };

        root.getChildren().addAll(content_box);
//        blocker1.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200, .7), null, null)));

        uber_root.getChildren().addAll(pictures_pane, root, UpPane.get_pane()); // TODO UpPane
        // Update screen dimensions dynamically
        uber_root.widthProperty().addListener((obs, old, newVal) -> {
            screen_width = newVal.doubleValue();
            UpPane.rootPane.requestLayout(); // Trigger UpPane layout
//            pie_chart.setAlignment(Pos.CENTER);
        });
        uber_root.heightProperty().addListener((obs, old, newVal) -> {
            screen_height = newVal.doubleValue();
            UpPane.rootPane.requestLayout(); // Trigger UpPane layout
        });
        pictures_pane.setVisible(false);
        pictures_pane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.7), null, null)));
        pictures_pane.setOnMouseClicked(f -> {   // Da bi zatvorio ovaj vbox bilo gde da se klikne
            var out = Animations.fadeOut(pictures_pane, Duration.millis(1000));
            out.setOnFinished(s -> {
                pictures_pane.toBack();
                pictures_pane.setVisible(false);
            });
            out.playFromStart();
        });
        VBox.setMargin(slider_box, new Insets(0, 0, 20, 0));
        Scene scene = new Scene(uber_root, 1500, 900);
        scene.getStylesheets().add(tooltip_css); // prebaciti index.css u resource root folder kao i fonts folder
        primaryStage.setScene(scene);
        primaryStage.show();

        startWebSocket(websocket_url);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                LeftPane.setSideScreen();
//                fetch_data();

                WebEngine webEngine = new WebEngine();
                webEngine.executeScript("console.log('GWT version: ' + frames[0].$gwt_version)");
                IPCodes.getIPCodes();

                if (Settings.pass_txt.get() == null || name_txt.get() == null) {
                    // Custom dialog setup
                    CustomDialog dialog = new CustomDialog("Log In", input -> {
                        Console.log("Dialog result: " + input.toString());
                        WindowLocation.assignHref(WindowLocation.getHref()); // important - reload
                    });
                    dialog.show(uber_root);
                }
//                Console.log("user:pass - " + name_txt.get() + ":" + Settings.pass_txt);
                if(Service_impl.isDarkModeEnabled()){
//                    if (UserAgent.isFireFox()){
//                        primaryStage.getIcons().add(0, title_icon_dark);
//                    }else{
                        primaryStage.getIcons().add(0, title_icon_light);
 //                   }
//                    Console.log("Dark mode enabled");
                }else{
                    primaryStage.getIcons().add(0, title_icon_dark);
//                    Console.log("Light mode enabled");
                }
//                Helper.startEffect(); // no GWT
                if (CyclingPower_Web_Local.graphicon != null)
                    CyclingPower_Web_Local.graphicon.refresh();  // important zbog inicijalnog razmestanja menu itema

                // pre fetch files
                if (Settings.AUTO_LOAD) {
                    futures_fetcher.fetchAllFiles();
                    fetch_all_btn.setDisable(true);
                    server_txt.set("Waiting Server ⏳");
                }

                cityService = new CityService();
                // Učitaj gradove
                cityService.loadCitiesWithCache(
                        cities -> {
                            Console.info("Cities loaded successfully!");
                        },
                        error -> {
                            Console.log("Error loading cities: " + error.getMessage());
                        }
                );


                // Koristi TooltipHelper
                TooltipHelper.attachTooltip(pastafarian, "Go to\nSpaghetti Monster",
                        CustomTooltip.TooltipPosition.LEFT,
                        CustomTooltip.TooltipType.DEFAULT,
                         true);
//                TooltipHelper.detachTooltip(pastafarian);
            }
        });
    }


    //-----------------------------------------------------
    // fetch samo novi json fajl
    public static void fetch_data() {

        if (activity != null) {
            map.delete_plot();
        }

        if(url.get() == null || url.get().isEmpty()){
            return;
        }

        if (name_txt.get() == null) {
//            Console.log("Define User name and Password");
            WebEngine webEngine = new WebEngine();
            webEngine.executeScript("console.error('Error: Define User name and Password')");
            return;
        }

        Console.log("name:pass ( " + name_txt.get() + ":" + Settings.pass_txt.get() + " )");

        FetchOptions options = new FetchOptions();
        Headers headers = Headers.create();
        String auth = name_txt.get() + ":" + Settings.pass_txt.get();
        String encodedAuth = SimpleBase64.encode(auth);
        Console.log("Sending Authorization: Basic " + encodedAuth); // Debug
        headers.set("Authorization", "Basic " + encodedAuth); // Use WebFX Base64
        options.setMethod("GET");
        options.setHeaders(headers);

        // Fetch JSON from Spring Boot server
//            String url = "http://localhost:8880/api/download-json/cycling_activity_1740710061847.json"; // Update with actual URL and filename
        Console.log("New json file url: " + url.get());
        Fetch.fetch(url.get(), options)
                .onSuccess(response -> {
                    if (response.status() == 200) {
                        response.text()
                                .onSuccess(jsonText -> {
                                    ProgressPane.root.setVisible(false);
                                    Console.log("Success");
                                    ReadOnlyAstObject json = Json.parseObject(jsonText);               // Parse JSON using WebFX
                                    activity = dev.java4now.web.model.CyclingActivity.fromJson(json);
//                                        Console.log(activity.toString());
                                    map.plot_route(activity);
                                    graphicon.set_graph_data(activity);
//                                    Console.log("distance: " + activity.getSession().getTotalDistance());
                                    total_distance.set(Format.formatDouble_GWT(activity.getSession().getTotalDistance() / 1000, 1));
                                    if ((long) activity.getSession().getTotalMovingTime() > 0) {
                                        moving_time.set(formatTimestamp((long) activity.getSession().getTotalMovingTime()));
                                        IS_TOTAL_TIME.set(false);
                                    } else {
                                        moving_time.set(formatTimestamp((long) activity.getSession().getTotalElapsedTime()));
                                        IS_TOTAL_TIME.set(true);
                                    }
                                    avg_speed.set(Format.formatDouble_GWT(activity.getSession().getAvgSpeed() * 3.6, 1));
                                    avg_power.set(Format.formatDouble_GWT(activity.getSession().getAvgPower(), 1));
                                    maximum_alt.set(String.valueOf(activity.getSession().getMaxAltitude()));
                                    minimum_alt.set(String.valueOf(activity.getSession().getMinAltitude()));
                                    total_cal.set(String.valueOf(activity.getSession().getTotalCalories()));
                                    avg_cad.set(String.valueOf((int) (activity.getSession().getAvgCadence() + 0.5)));
                                    have_cadence.set(activity.getSession().getAvgCadence() > 0.0);
                                    int weather = Optional.of(activity.getSession().getWeather())
                                            .orElse(100); // or any other default value
                                    CanvasChartPane.Weather_Code_Description(weather);

//                                    device.set(activity.getDeviceInfo().getProductName());
                                    if (activity.getDeviceInfo() == null) {
                                        device.set("Gps device");
                                        graphic_icon.get().setText(Ionicons.getIcon("ION_NAVIGATE"));
                                    } else {
                                        device.set(activity.getDeviceInfo().getProductName());
                                        graphic_icon.get().setText( Ionicons.getIcon("ION_IPHONE"));
//                                      graphic_icon.set( new Text(Ionicons.getIcon("SMARTPHONE")));  // uvek novi text
//                                      graphic_icon.get().getStyleClass().clear();
//                                      graphic_icon.get().getStyleClass().add("num_label_right_feather"); // important mora ako uvek dodajem new Text()
                                    }
                                })
                                .onFailure(error -> System.err.println("Failed to parse JSON: " + error.getMessage()));
                    } else {
                        System.err.println("Failed to fetch JSON. Status: " + response.status());
                    }
                })
                .onFailure(error -> System.err.println("Fetch error: " + error.getMessage()));
    }


    //--------------------------------------------
    public static void fetch_list() {
        // Fetch the list of JSON files from the Spring Boot server
        String listJsonUrl = "https://cyclingpower-server-1.onrender.com/api/list-json";

        FetchOptions options = new FetchOptions();
        Headers headers = Headers.create();
        String auth = name_txt.get() + ":" + Settings.pass_txt.get();
        headers.set("Authorization", "Basic " + SimpleBase64.encode(auth));
        options.setMethod("GET");
        options.setHeaders(headers);

        Fetch.fetch(listJsonUrl, options)  // ,new FetchOptions().setMode(CorsMode.NO_CORS)
                .onSuccess(response -> {
                    if (response.status() == 200) {
                        response.text()
                                .onSuccess(jsonText -> {
                                    ReadOnlyAstArray jsonFiles = Json.parseArray(jsonText);
                                    jsonFileListView.getItems().clear();
                                    counter = 0;
                                    for (int i = 0; i < jsonFiles.size(); i++) {
                                        String fileName = jsonFiles.getString(i);
                                        String str = "Pancevo " + i;
                                        jsonFileListView.getItems().add(str);
//                                        Console.log("fileName: " + fileName);
                                    }
                                    if (jsonFiles.isEmpty()) {
                                        jsonFileListView.getItems().add("No JSON files found");
                                    }
                                })
                                .onFailure(error -> Console.log("Failed to parse JSON file list: " + error.getMessage()));
                    } else {
                        Console.log("Failed to fetch JSON file list. Status: " + response.status());
                    }
                })
                .onFailure(error -> Console.log("Fetch error: " + error.getMessage()));
    }


    //--------------------------------------------
    public static void load_activity(CyclingActivity current_activity) {

        if (activity != null) {
            map.delete_plot();
        }
        ProgressPane.root.setVisible(false);

        activity = current_activity;
//        Console.log(activity.toString());

        map.plot_route(activity);
        graphicon.set_graph_data(activity);
//        Console.log("distance: " + activity.getSession().getTotalDistance());
        total_distance.set(Format.formatDouble_GWT(activity.getSession().getTotalDistance() / 1000, 1));
        if ((long) activity.getSession().getTotalMovingTime() > 0) {
            moving_time.set(formatTimestamp((long) activity.getSession().getTotalMovingTime()));
            IS_TOTAL_TIME.set(false);
        } else {
            moving_time.set(formatTimestamp((long) activity.getSession().getTotalElapsedTime()));
            IS_TOTAL_TIME.set(true);
        }
        MovingChart.update_data(activity.getSession().getTotalElapsedTime(), activity.getSession().getTotalMovingTime());
//        total_time.set(formatTimestamp((long) activity.getSession().getTotalElapsedTime()));
//        Console.log("moving_time: " + (long) activity.getSession().getTotalMovingTime());
        avg_speed.set(Format.formatDouble_GWT(activity.getSession().getAvgSpeed() * 3.6, 1));
        avg_power.set(Format.formatDouble_GWT(activity.getSession().getAvgPower(), 1));
        maximum_alt.set(String.valueOf(activity.getSession().getMaxAltitude()));
        minimum_alt.set(String.valueOf(activity.getSession().getMinAltitude()));
        total_cal.set(String.valueOf(activity.getSession().getTotalCalories()));
        avg_cad.set(String.valueOf((int) (activity.getSession().getAvgCadence() + 0.5)));
        have_cadence.set(activity.getSession().getAvgCadence() > 0.0);
//        Console.log("Weather: " + activity.getSession().getWeather());
        int weather = Optional.of(activity.getSession().getWeather())
                .orElse(100); // or any other default value
        CanvasChartPane.Weather_Code_Description(weather);

        if (activity.getDeviceInfo() == null) {
            device.set("Gps device");
            graphic_icon.get().setText(Ionicons.getIcon("ION_NAVIGATE"));
        } else {
            device.set(activity.getDeviceInfo().getProductName());
            graphic_icon.get().setText( Ionicons.getIcon("ION_IPHONE"));    // Ostaje isti Text Node
//          graphic_icon.set( new Text(Ionicons.getIcon("SMARTPHONE")));              // uvek novi text
//          graphic_icon.get().getStyleClass().clear();
//          graphic_icon.get().getStyleClass().add("num_label_right_feather");             // important mora ako uvek dodajem new Text()
        }
        server_txt.set(activity.getSession().getDate().toString() + " ✅");
        WebSocketClient.stopCounting();
        path_txt.set(activity.getDescriptiveName());
    }
}