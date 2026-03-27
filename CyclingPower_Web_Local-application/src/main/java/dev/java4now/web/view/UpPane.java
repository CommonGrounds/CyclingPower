package dev.java4now.web.view;

import dev.java4now.web.CyclingPower_Web_Local;
import dev.java4now.web.Settings;
import dev.java4now.web.custom_ui.MenuButtonGroup;
import dev.java4now.web.custom_ui.SimpleListView;
import dev.java4now.web.effects.Animations;
import dev.java4now.web.graph.CanvasChartPane;
import dev.java4now.web.http.SimpleBase64;
import dev.java4now.web.http.UploadImage;
import dev.java4now.web.icons.Feather_Icons;
import dev.java4now.web.icons.FontAwesomeSolid_Icons;
import dev.java4now.web.icons.Ionicons;
import dev.java4now.web.model.futures_fetcher;
import dev.java4now.web.util.Helper_svg;
import dev.java4now.web.util.WebFXUtil;
import dev.webfx.extras.filepicker.FilePicker;
import dev.webfx.extras.fonticons.IconFont;
import dev.webfx.extras.fonticons.IconPack;
import dev.webfx.extras.fonticons.feather.FeatherIcon;
import dev.webfx.extras.fonticons.feather.FeatherPack;
import dev.webfx.platform.ast.ReadOnlyAstArray;
import dev.webfx.platform.ast.json.Json;
import dev.webfx.platform.blob.Blob;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.fetch.Fetch;
import dev.webfx.platform.fetch.FetchOptions;
import dev.webfx.platform.fetch.Headers;
import dev.webfx.platform.file.File;
import dev.webfx.platform.os.OperatingSystem;
import dev.webfx.platform.util.Arrays;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import service.Service_impl;

import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static dev.java4now.web.CyclingPower_Web_Local.*;
import static dev.java4now.web.Settings.name_txt;
import static dev.java4now.web.graph.CanvasChartPane.graph_type;
import static dev.java4now.web.view.LeftPane.*;

public class UpPane {

    public enum Direction {
        DOWN,
        UP
    }

    public static Pane rootPane;
    static Pane transparentPane;
    static VBox pic_box;
    static Text close_btn;
    public static Button fetch_all_btn;
    private static LinkedList<ImageView> images_view = new LinkedList<>();
    private static LinkedList<ImageView> uploading_images_view = new LinkedList<>();
    public static SimpleListView<String> jsonFileListView = new SimpleListView<>();
    public static MenuButtonGroup month_btn,year_btn;
    public static boolean NO_IMAGE = true;
    public static boolean SVG_IMAGE = true;
    static Button upload_pic_btn;
    private static String current_file_name = "";
    private static String type = "image/png";

    static FilePicker filePicker = FilePicker.create(); // OpenJFX or GWT instance
    static AtomicReference<File> file = new AtomicReference<>();

    public static Pane get_pane() {
        boolean PASTAFARIAN_IS_HERE;

        ChangeListener<String> listener = (observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("No JSON files found")) {
//                Console.log("selected: " + newValue);
                var name = futures_fetcher.final_list.get(jsonFileListView.getSelectedItem());
                current_file_name = name;
//                Console.log("selected: " + name);
//                CyclingPower_Web.url.set("http://localhost:8880/api/download-json/" + name);
//                CyclingPower_Web.fetch_data();
                load_activity(futures_fetcher.linked_list.get(futures_fetcher.final_list.get(jsonFileListView.getSelectedItem())));
                fetchImageForJson(name);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        graph_type.set("Distance");                                                   // setujem na distance svaki put kad izaberem neki activity
                        CanvasChartPane.graph_menu.setSelectedMenuItem(0);                            // setujem distance ikonu kao selektovanu
                        if(CyclingPower_Web_Local.graphicon != null)CyclingPower_Web_Local.graphicon.refresh();   // refresh graph da bi pozvao draw jer ako ostane wind onda greska
                    }
                });
            }
        };

        jsonFileListView.selectedItemProperty().addListener(listener);
//        jsonFileListView.selectedItemProperty().removeListener(listener);

        upload_pic_btn = new Button("Upload Picture");
        upload_pic_btn.getStyleClass().add("my_button");
        create_FilePicker();
//        upload_pic_btn.setOnAction(e -> {
//            Console.log("Load Picture");
//        });

        IconPack iconPack = FeatherPack.getInstance();
        IconFont iconFont = iconPack.getFonts()[0];

        var lbl_device = new Label("Device");
        var lbl_device2 = new Label();
        lbl_device2.getStyleClass().add("label_graphic"); // moze font i razlicito od graphic ali mora posebno color uz .text selektor u css-u
        lbl_device2.textProperty().bind(device);
        graphic_icon.get().getStyleClass().addAll("label_graphic","font-ionicons","label_graphic_ionicons"); // mora zbog feather fonta i svog color-a
        lbl_device2.setGraphicTextGap(5);
        lbl_device2.graphicProperty().bind(new ObjectBinding<Node>() {
            { bind(graphic_icon); }
            @Override
            protected Node computeValue() {
                return graphic_icon.get();
            }
        });
        graphic_icon.get().textProperty().addListener((obs, old, newValue) -> {
//            Console.log("graphic_icon: " + graphic_icon.get().getText());
            lbl_device2.setGraphicTextGap(newValue.equals(Ionicons.getIcon("ION_NAVIGATE")) ? 5 : 10);
        });

        String refreshIconChar = Feather_Icons.getChar("REFRESH_CW"); // \e09c
        fetch_all_btn = new Button(refreshIconChar);
        fetch_all_btn.setFont(font);
        fetch_all_btn.setTextFill(Color.BLACK);   // IMPORTANT - za feather font color ne ide postavljanje color-a iz css-a
        fetch_all_btn.getStyleClass().addAll("font-icon-button_fe","font-icon-button"); // osim font color ostalo radi za feather
//        fetch_all.getStyleClass().add("my_button");
        fetch_all_btn.setOnAction(e -> {
//            CyclingPower_Web.fetch_list();
            futures_fetcher.fetchAllFiles();
            fetch_all_btn.setDisable(true);
            server_txt.set("Server OK ✅");
        });
        jsonFileListView.setIcon(FeatherIcon.ACTIVITY);
        jsonFileListView.setMaxWidth(screen_width / 1.9 );

        pic_box = new VBox(10);
//        var img_view = new ImageView(default_pic);
        StackPane img_view;
//        Console.log("screen_width: " + screen_width );
        if(screen_width<1000){
            img_view = Helper_svg.get_svg(true);
            if (screen_width<800){
                PASTAFARIAN_IS_HERE = false;
            }else {
                PASTAFARIAN_IS_HERE = true;
            }
        }else{
            img_view = Helper_svg.get_svg(false);
            PASTAFARIAN_IS_HERE = true;
        }
//        img_view.setEffect(Helper.lighting); // NO GWT
// --------- Apply lighting effect GWT compatibile --------------

        // Create overlay Rectangle
//        Rectangle overlay = new Rectangle(0, 0, 200, 200);
//        overlay.setOpacity(1); // Base opacity
        var overlay_pane = new StackPane(img_view/*,overlay*/);
//        overlay_pane.setAlignment(Pos.CENTER_LEFT);
        pic_box.getChildren().addAll(overlay_pane);
//        pic_box.setAlignment(Pos.CENTER_LEFT);
        // Apply lighting effect - moving light effect
//        Helper_light.setEffect(overlay);
//        Helper_light.startEffect();

//----------- css moving light effect -------------------------
/*
        pic_box.getChildren().addAll(img_view);
        pic_box.setAlignment(Pos.CENTER);
        // Apply lighting effect
        Helper_css.setEffect(img_view);
        Helper_css.startEffect();
*/
//-------------------------------------------------------------

        ScrollPane scroll_Pane = new ScrollPane(pic_box);
        scroll_Pane.setMaxHeight(screen_height / 2);
        scroll_Pane.setMaxWidth(screen_width / 2 );
        scroll_Pane.getStyleClass().add("pic_scroll");

        /*
        var btn = new Button("Fade in");
        btn.getStyleClass().add("my_button");
        btn.setOnAction(e -> {
           var out = new FadeIn(btn);
//         out.setSpeed(0.1);       // 1 je normalna brzina 0.1 je 10 puta sporije
           out.setOnFinished(f -> btn.setText("Finished"));        // actual close
           out.play();
        });
*/

        year_btn = new MenuButtonGroup("Year");
        month_btn = new MenuButtonGroup("Months");
        futures_fetcher.yearSelector = year_btn;
        futures_fetcher.monthSelector = month_btn;
        // Refresh list when selections change
        year_btn.selectedValueProperty().addListener((obs, old, newValue) -> futures_fetcher.updateListFromCache());
        month_btn.selectedValueProperty().addListener((obs, old, newValue) -> futures_fetcher.updateListFromCache());

        var datum_box = new HBox(5,year_btn,month_btn);
        datum_box.setAlignment(Pos.CENTER_LEFT);

        Pane pane = new Pane(Arrays.asList(
                lbl_device, lbl_device2, fetch_all_btn, datum_box, jsonFileListView, scroll_Pane,
                PASTAFARIAN_IS_HERE ? gnu_btn : null
        ).stream().filter(Objects::nonNull).toArray(Node[]::new)){
            @Override
            protected void layoutChildren() {
                // Ensure screen dimensions are updated
                if (screen_width == 0 || screen_height == 0) {
                    screen_width = CyclingPower_Web_Local.uber_root.getWidth();
                    screen_height = CyclingPower_Web_Local.uber_root.getHeight();
                }
//                setTranslateY(-(screen_height / 2));
                // Set max and preferred height to half screen height
//                setMaxHeight(screen_height / 2);
                if(OperatingSystem.isMobile() && screen_height < screen_width){
                    setPrefHeight(screen_height);
                }else {
                    setPrefHeight(screen_height / 2);
                }
                setPrefWidth(screen_width);
//                Console.log("Pane Height: " + getHeight() + " Pane Width: " + getWidth());
                super.layoutChildren();
                Console.log("Width: " + CyclingPower_Web_Local.screen_width + ", Height: " + CyclingPower_Web_Local.screen_height);
                // laptop - Width: 1366, Height: 573
                if (screen_width > 800 && screen_width < 900 && PASTAFARIAN_IS_HERE) {
                    gnu_btn.setLayoutX(getWidth()/2 - gnu_btn.getWidth()/2);
                    gnu_btn.setLayoutY(10);
                }else{
                    if (PASTAFARIAN_IS_HERE) {
                        gnu_btn.setLayoutX(getWidth() - gnu_btn.getWidth() - 5);
                        gnu_btn.setLayoutY(10);
                    }
                }
                if(screen_width < 900 ){
                    lbl_device.setLayoutX(getWidth()  - (lbl_device.getWidth() + 10) - ( jsonFileListView.getWidth()/2 - lbl_device.getWidth()/2));
                    lbl_device.setLayoutY(0);
                    lbl_device2.setLayoutX(getWidth()  - (lbl_device2.getWidth() + 10) - ( jsonFileListView.getWidth()/2 - lbl_device2.getWidth()/2));
                    lbl_device2.setLayoutY(20);
                    fetch_all_btn.setLayoutX(getWidth()  - (fetch_all_btn.getWidth() + 10) - ( jsonFileListView.getWidth()/2 - fetch_all_btn.getWidth()/2));
                    fetch_all_btn.setLayoutY(40);
                    datum_box.setLayoutX(getWidth()  - (datum_box.getWidth() + 10 ) - ( jsonFileListView.getWidth()/2 - datum_box.getWidth()/2));
                    datum_box.setLayoutY(95);
                    jsonFileListView.setLayoutX(getWidth()  - (jsonFileListView.getWidth() + 10));
                    jsonFileListView.setLayoutY(95 + datum_box.getHeight() + 10);
//                pic_box.setPrefWidth(getWidth()*0.8);
//                pic_box.setLayoutX(getWidth() - (pic_box.getWidth() / 2));
                }else if(screen_width < 1400 ){
                    lbl_device.setLayoutX(getWidth()/2  - (lbl_device.getWidth() / 2));
                    lbl_device.setLayoutY(0);
                    lbl_device2.setLayoutX(getWidth()/2  - (lbl_device2.getWidth() / 2));
                    lbl_device2.setLayoutY(20);
                    fetch_all_btn.setLayoutX(getWidth()/2  - (fetch_all_btn.getWidth() / 2));
                    fetch_all_btn.setLayoutY(50);
                    datum_box.setLayoutX(getWidth()/2  - (datum_box.getWidth() / 2));
                    datum_box.setLayoutY(110);
                    jsonFileListView.setLayoutX(getWidth()/2  - (jsonFileListView.getWidth() / 2));
                    jsonFileListView.setLayoutY(110 + datum_box.getHeight() + 10);
//                pic_box.setPrefWidth(getWidth()*0.8);
//                pic_box.setLayoutX(getWidth() - (pic_box.getWidth() / 2));
                }else{
                    lbl_device.setLayoutX(getWidth()/2  - (lbl_device.getWidth() / 2));
                    lbl_device.setLayoutY(0);
                    lbl_device2.setLayoutX(getWidth()/2  - (lbl_device2.getWidth() / 2));
                    lbl_device2.setLayoutY(40);
                    fetch_all_btn.setLayoutX(getWidth()/2  - (fetch_all_btn.getWidth() / 2));
                    fetch_all_btn.setLayoutY(90);
                    datum_box.setLayoutX(getWidth()/2  - (datum_box.getWidth() / 2));
                    datum_box.setLayoutY(150);
                    jsonFileListView.setLayoutX(getWidth()/2  - (jsonFileListView.getWidth() / 2));
                    jsonFileListView.setLayoutY(150 + datum_box.getHeight() + 10);
//                pic_box.setPrefWidth(getWidth()*0.8);
//                pic_box.setLayoutX(getWidth() - (pic_box.getWidth() / 2));
                }
                images_view.forEach(view -> {
//                    view.setFitWidth(pic_box.getWidth());
                    view.setFitWidth(getWidth() * 0.8);      // thumbnail imageview
                });
                scroll_Pane.setMinHeight(200); // TODO
                if(getHeight() < 600){
                    pic_box.setMaxWidth(getWidth() * 0.33);
                    scroll_Pane.setMaxWidth(getWidth() * 0.33);
                    scroll_Pane.setHvalue(scroll_Pane.getHmax() / 2); // set scroll bar to center
                    scroll_Pane.setPrefHeight(getHeight() * 0.97);
                    pic_box.setAlignment(Pos.CENTER_LEFT);
                    scroll_Pane.setLayoutX(0);
                    scroll_Pane.setLayoutY(0);
                }else{
                    scroll_Pane.setLayoutX(getWidth()/2  - (scroll_Pane.getWidth() / 2));
                    scroll_Pane.setLayoutY(600);
                    pic_box.setAlignment(Pos.CENTER);
                }
//                datum_box.setLayoutX(20);
//                datum_box.setLayoutY(year_btn.getLayoutY());
//                jsonFileListView.setLayoutY(year_btn.getLayoutY() + year_btn.getHeight() + 10);
            }
        };
        pane.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE, null, null)));
//        shadowBorder(pane);
        pane.getStyleClass().add("border-3d");


        // Bind height to half of uber_root's height
//        pane.prefHeightProperty().bind(CyclingPower_Web.uber_root.heightProperty().divide(2));
//        pane.maxHeightProperty().bind(CyclingPower_Web.uber_root.heightProperty().divide(2));
        // nema za GWT ovaj bind pa mora :
//        pane.setPrefHeight(CyclingPower_Web.uber_root.getHeight() / 2);
//        pane.setMaxHeight(screen_height / 2);

/*
        VBox.setMargin(lbl_device2, new Insets(0, 0, 20, 0));
        VBox.setMargin(server_lbl, new Insets(0, 0, 20, 0));
        VBox.setMargin(fetch_all_btn, new Insets(0, 0, 20, 0));
        VBox.setMargin(datum_box, new Insets(0, 0, 5, 20));
        VBox.setMargin(jsonFileListView, new Insets(0, 20, 20, 20)); // Mora ovako da se odredi width
        VBox.setMargin(graph_menu, new Insets(0, 0, 40, 0));
        VBox.setMargin(back_button, new Insets(10, 0, 20, 0));
*/

        close_btn = new Text(FontAwesomeSolid_Icons.getIcon("PLUS_CIRCLE"));
        close_btn.setFont(font_fa);
        close_btn.getStyleClass().addAll("font-icon-button_fa","font-rotate-button");
        close_btn.setOnMouseClicked(e -> {
            rotate_translate( Direction.UP);
        });

        transparentPane = new Pane(close_btn){
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                if(OperatingSystem.isMobile() && screen_height < screen_width){
                    close_btn.setLayoutX(getWidth()/2 - close_btn.getLayoutBounds().getWidth()/2);
                    close_btn.setLayoutY(10);
                }else {
                    if(screen_width < 800 ){
                        close_btn.setLayoutX(getWidth()/2 - close_btn.getLayoutBounds().getWidth()/2);
                    }else {
                        close_btn.setLayoutX(10);
                    }
                    close_btn.setLayoutY(10);
                }
            }
        };
        transparentPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0), null, null)));
        transparentPane.setOnMouseClicked(e -> {
            rotate_translate( Direction.UP);
        });
        rootPane = new Pane(pane,transparentPane){
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                if(OperatingSystem.isMobile() && screen_height < screen_width){
//                    transparentPane.setLayoutY(getHeight()/2 + 8);
                    transparentPane.setPrefWidth(getWidth());
                    transparentPane.setPrefHeight(0);
                } else {
                    transparentPane.setLayoutY(getHeight()/2 + 8);
                    transparentPane.setPrefWidth(getWidth());
                    transparentPane.setPrefHeight(getHeight()/2 - 8);
                }
            }
        };
        return rootPane;
    }


    // ========== 11. SHADOW EFFECT (Kombinovano sa border) ==========
    private static void shadowBorder(Pane pane) {
        pane.setBorder(new Border(new BorderStroke(
                Color.DARKGRAY,
                BorderStrokeStyle.SOLID,
                null,
                new BorderWidths(0, 0, 4, 0)
        )));

        // Dodaj shadow efekat
        javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(10);
        shadow.setOffsetY(4);
        pane.setEffect(shadow);
    }



    // Updated fetchImageForJson for WebFX Response API
    public static void fetchImageForJson(String jsonFile) {

        AtomicInteger counter = new AtomicInteger();

//        String imageUrl = "http://localhost:8880/api/image-for-json/" + jsonFile;
        String imageUrl = BASE_URL + "/api/image-for-json/" + jsonFile;
        Console.log("Fetching images for: " + jsonFile);
        images_view.clear();

        FetchOptions options = new FetchOptions();
        Headers headers = Headers.create();
        String auth = name_txt.get() + ":" + Settings.pass_txt.get();
        headers.set("Authorization", "Basic " + SimpleBase64.encode(auth));
        options.setMethod("GET");
        options.setHeaders(headers);

        Fetch.fetch(imageUrl, options)
                .onSuccess(response -> response.text()
                        .onComplete(text -> {
                            String responseText = text.result();
                            Console.log("Response text: " + responseText);

                            if (response.status() >= 400) {
                                Console.log("Error response: " + responseText);
//                                images_view.add(new ImageView(default_pic));
                                set_PictureBox();
                                return;
                            }

                            ReadOnlyAstArray signedUrls = Json.parseArray(responseText);
                            Console.log("Image filenames count: " + signedUrls.size());
                            if (!signedUrls.isEmpty()) {
                                NO_IMAGE = false;
                            }

                            if (signedUrls.size() == 0) {
                                Console.log("No images found for " + jsonFile);
                                NO_IMAGE = true;
//                                images_view.add(new ImageView(default_pic));
                                set_PictureBox();
                                return;
                            }

                            for (int i = 0; i < signedUrls.size(); i++) {
                                String signedUrl = signedUrls.getString(i);
                                String filename = signedUrl.substring(signedUrl.lastIndexOf('/') + 1, signedUrl.indexOf("?token="));
                                Console.log("Loading image: " + signedUrl);

                                Image image = new Image(signedUrl, true);
                                ImageView imageView = new ImageView(image);
                                imageView.setFitWidth(300);
                                imageView.setFitHeight(200);
                                imageView.setPreserveRatio(true);

                                WebFXUtil.onImageLoaded(image, () -> {
                                    Console.log("Image loaded: " + filename);
                                    images_view.add(imageView);
                                    counter.getAndIncrement();
                                    if(counter.get() >= signedUrls.size()-1){
                                        set_PictureBox();
                                    }
                                });

//                                images_view.add(imageView);
                            }
                        }))
                .onFailure(error -> {
                    Console.log("Failed to fetch image list: " + error.getMessage());
//                    images_view.add(new ImageView(default_pic));
                    set_PictureBox();
                });
    }


    private static void create_FilePicker() {
        AtomicReference<AtomicInteger> image_counter = new AtomicReference<>(new AtomicInteger());
        filePicker.setGraphic( upload_pic_btn );
//        filePicker.getGraphic().getStyleClass().clear();
//        filePicker.getGraphic().getStyleClass().add("my_button");
        filePicker.getSelectedFiles().addListener((InvalidationListener) obs -> {
            // Getting the selected files
//            List<File> fileList = filePicker.getSelectedFiles();    // lista fajlova
            file.set(filePicker.getSelectedFiles().get(0));           // Samo 1 ( ili 1. ) fajl
            // Your code treating these files
            ImageView imageView = new ImageView(new Image(file.get().getObjectURL()));
            imageView.setFitWidth(pic_box.getWidth() * 0.8);
            imageView.setPreserveRatio(true);
            dev.webfx.platform.console.Console.log(file.get().getName());
            String lowerName = file.get().getName().toLowerCase();
            if ( lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") ) {
                if(lowerName.endsWith(".png")){
                    type = "image/png";
                }else{
                    type = "image/jpg";
                }
                if(SVG_IMAGE){
                    pic_box.getChildren().clear();
                    pic_box.getChildren().add(filePicker.getView());
                    VBox.setMargin(filePicker.getView(), new Insets(0, 0, 20, 20));
                    SVG_IMAGE = false;
                }
                pic_box.getChildren().add(imageView);
//                UploadImage.upload(current_file_name,file.get(),image_counter.get().getAndIncrement());
                // Resize and upload asynchronously
                resizeAndUpload(file.get(), current_file_name, image_counter.get().getAndIncrement());
            } else {
//                root.getChildren().add(imageView); // samo za image node
            }
        });
    }


    private static void resizeAndUpload(File originalFile, String json_name, int image_counter) {
        // Load original image (JavaFX Image for sizing)
        Image originalImage = new Image(originalFile.getObjectURL(), true);

        boolean no_resize = false;
        if(no_resize){
            uploadBlob(json_name, originalFile, originalFile.getName(), image_counter);
            return;
        }

        WebFXUtil.onImageLoaded(originalImage, () -> {
            double originalHeight = originalImage.getHeight();
            double originalWidth = originalImage.getWidth();
            Console.log("Original image size: " + originalWidth + "x" + originalHeight + "px");

            if (originalHeight <= 600) {
                Console.log("Image already <= 600px height; uploading original as Blob.");
                // Convert original File to Blob for consistency (or upload File directly)
                uploadBlob(json_name, originalFile, originalFile.getName(), image_counter);
                return;
            }

            // Compute new dimensions (preserve ratio)
            double newHeight = 600;
            double newWidth = originalWidth * (newHeight / originalHeight);

            // Create platform Canvas and draw
            Canvas canvas = new Canvas(newWidth, newHeight);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.drawImage(originalImage, 0, 0, newWidth, newHeight);  // Draw scaled

            // Export to data URL using interop
            String dataURL = Service_impl.toDataURL(canvas, type,originalFile.getObjectURL());  // PNG; or "image/jpeg", 0.9 for quality
            if (dataURL == null) {
                Console.log("Failed to generate data URL; uploading original.");
//                uploadBlob(json_name, originalFile, originalFile.getName(), image_counter);
                return;
            }

            Console.log("Resized to " + newWidth + "x" + newHeight + "px; data URL generated.");

            // Fetch data URL as Blob
            FetchOptions options = new FetchOptions();
            options.setMethod("GET");
            Fetch.fetch(dataURL, options)
                    .onSuccess(response -> {
                        if (response.status() == 200) {
                            response.blob()
                                    .onSuccess(blob -> {
                                        String resizedName = originalFile.getName().replaceFirst("\\.[^.]+$", "_resized.png");  // e.g., image.jpg -> image_resized.png
                                        Console.log("Resized Blob created (size: " + blob.length() + " bytes)");
                                        uploadBlob(json_name, blob, resizedName, image_counter);
                                    })
                                    .onFailure(error -> Console.log("Failed to get resized Blob: " + error.getMessage()));
                        } else {
                            Console.log("Failed to fetch data URL (status: " + response.status() + ")");
                        }
                    })
                    .onFailure(error -> Console.log("Error fetching resized data URL: " + error.getMessage()));
        });

        // Handle load error
        originalImage.errorProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Console.log("Failed to load image: " + newValue);
            }
        });
    }


    // New helper method to upload Blob (avoids File creation)
    private static void uploadBlob(String json_name, Blob resizedBlob, String filename, int image_counter) {
        // Call modified UploadImage (see below)
        UploadImage.uploadBlob(json_name, resizedBlob, filename, image_counter);
    }


    //--------------------------------------------
    private static void set_PictureBox() {

        pic_box.getChildren().clear();

        if(images_view.size() == 0){
            StackPane img_view;
            if(screen_width<900){
                img_view = Helper_svg.get_svg(true);
            }else{
                img_view = Helper_svg.get_svg(false);
            }
            var overlay_pane = new StackPane(img_view/*,overlay*/);
            if(NO_IMAGE){
                pic_box.getChildren().addAll(overlay_pane, filePicker.getView());
                VBox.setMargin(filePicker.getView(), new Insets(0, 0, 20, 20));
            }else {
                pic_box.getChildren().add(overlay_pane);
            }
            SVG_IMAGE = true;
        }

        images_view.forEach(view -> {
            pic_box.getChildren().add(view);
            view.setPreserveRatio(true);
            view.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    Console.log("click on: " + view.getImage().getUrl());
                    ImageView copy = new ImageView(view.getImage());      // orig image
                    pictures_pane.getChildren().clear();
                    pictures_pane.getChildren().add(copy);
                    pictures_pane.setVisible(true);
//                    copy.setFitWidth(screen_width * 0.9);
                    if (screen_width > screen_height) {
                        copy.setFitHeight(screen_height);
                    } else {
                        copy.setFitWidth(screen_width);
                    }
                    copy.setPreserveRatio(true);
                    copy.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            var out = Animations.fadeOut(pictures_pane,Duration.millis(1000));
                            out.setOnFinished(f -> {
                                pictures_pane.toBack();
                                pictures_pane.setVisible(false);
                            });        // actual close
                            out.playFromStart();
                        }
                    });
                    pictures_pane.toFront();
                    pictures_pane.setAlignment(Pos.CENTER);
                    var out = Animations.fadeIn(pictures_pane,Duration.millis(1000));
//         out.setSpeed(0.1);       // 1 je normalna brzina 0.1 je 10 puta sporije
//                    out.setOnFinished(f -> btn.setText("Finished"));        // actual close
                    out.play();
                }
            });
        });
    }



    //----------------------------------------------------------------
    public static void rotate_translate(Direction direction){
        Timeline prvaAnimacija = new Timeline(/* ... KeyFrames ... */);
        Timeline drugaAnimacija = new Timeline(/* ... KeyFrames ... */);
        double angle = 0f;
        double transition_value = 0;
        if(direction == Direction.UP){
            angle = 0;
            transition_value = -rootPane.getHeight();
        }else{
            angle = 45;  // 135 za vise rotiranja ( 3 x 45 )
            transition_value = 0;
        }

        KeyValue rotation = new KeyValue(close_btn.rotateProperty(), angle, Interpolator.SPLINE(0.25, 0.1, 0.25, 1));
        KeyFrame rotation_frame = new KeyFrame(direction == Direction.UP ? Duration.seconds(.2) : Duration.seconds(.3), rotation);
        KeyValue translation = new KeyValue(rootPane.translateYProperty(), transition_value, Interpolator.SPLINE(0.25, 0.1, 0.25, 1));
        KeyFrame translation_frame = new KeyFrame(Duration.seconds(.5), translation);

        prvaAnimacija.getKeyFrames().add(direction == Direction.UP ? rotation_frame : translation_frame );
        drugaAnimacija.getKeyFrames().add( direction == Direction.UP ? translation_frame : rotation_frame );

        prvaAnimacija.setOnFinished(t -> {
            if(direction == Direction.UP) {
                side.setVisible(true);
                transparentPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0), null, null)));
            }
            if(direction == Direction.DOWN) {
                transparentPane.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 0.7), null, null)));
            }
            drugaAnimacija.play();
        });
        drugaAnimacija.setOnFinished(t -> {
//            Console.log("drugaAnimacija finished");
            if(direction == Direction.UP) side.toFront();
        });
        prvaAnimacija.play();
        if(direction == Direction.DOWN) {
            side.setVisible(false);
        }
    }
}
