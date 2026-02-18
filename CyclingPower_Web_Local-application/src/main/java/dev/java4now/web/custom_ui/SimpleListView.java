package dev.java4now.web.custom_ui;

import dev.webfx.extras.fonticons.FontIcons;
import dev.webfx.extras.fonticons.IconFont;
import dev.webfx.extras.fonticons.IconPack;
import dev.webfx.extras.fonticons.feather.FeatherIcon;
import dev.webfx.extras.fonticons.feather.FeatherPack;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import static dev.java4now.web.CyclingPower_Web_Local.screen_height;
import static dev.java4now.web.CyclingPower_Web_Local.screen_width;

public class SimpleListView<T> extends ScrollPane {

    private final VBox content;
    private final ObservableList<T> items;
    private final ObjectProperty<T> selectedItem;
    private FeatherIcon current_icon;
    private boolean icon_is_set = false;
    public static ProgressBar progressBar;
    public static final StringProperty progress_msg = new SimpleStringProperty("Upload 0 %");
    public static ProgressBar progress = progress_dialog();

    IconPack iconPack = FeatherPack.getInstance();
    IconFont iconFont = iconPack.getFonts()[0];

    public SimpleListView() {
        content = new VBox(5); // Spacing between items
        content.setPadding(new Insets(10, 0, 10, 5));
        items = FXCollections.observableArrayList();
        selectedItem = new SimpleObjectProperty<>();

        // Set up the content area
        var root = new StackPane(progress,content){
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                progress.setLayoutY(0);
                progress.setMaxWidth(getWidth()*0.8);
//                progress.setLayoutX(getWidth()*0.1);
            }
        };
        progress.setVisible(false);
        root.setAlignment(Pos.CENTER);
        setContent(root);
        setFitToWidth(true); // Ensure content fits the width
        if(screen_height < 600 ){
            setPrefHeight(135);  // Fixed height for scrolling
            setMinHeight(135);   // IMPORTANT - Da ne bi bilo kolapsa od controla ispod
        }else if(screen_height < 700 ){
            setPrefHeight(160);
            setMinHeight(160);
        }else{
            setPrefHeight(200);
            setMinHeight(200);
        }
        if(screen_width < 1100 ){
            setPrefWidth(300);   // Optional: Default width (adjust as needed)
        }else if(screen_width < 1300 ){
            setPrefWidth(400);
        }else{
            setPrefWidth(450);
        }


        // Apply CSS class to the ScrollPane
        getStyleClass().add("simple_list_container");

        // Listen for changes in the items list
        items.addListener((javafx.beans.Observable observable) -> updateItems());
    }

    public void setIcon(FeatherIcon current_icon) {
        this.current_icon = current_icon;
        icon_is_set = true;
    }

    // Get the items list
    public ObservableList<T> getItems() {
        return items;
    }

    // Selected item property
    public ObjectProperty<T> selectedItemProperty() {
        return selectedItem;
    }

    public T getSelectedItem() {
        return selectedItem.get();
    }


    // New method to programmatically select an item
    public void selectItem(T item) {
        if (item == null || !items.contains(item)) return;

        selectedItem.set(item);
        updateHighlighting();
    }


    private void updateItems() {
        content.getChildren().clear();
        for (T item : items) {
            Label label;
            if (icon_is_set) {
                HBox itemContainer = new HBox(5); // 5px spacing between icon and text
//                itemContainer.getStyleClass().add("list-item");

                // Icon - mora Text ako koristim webfx font icon
                Text icon = FontIcons.newText(current_icon);         // css color Works
                icon.getStyleClass().add("list-icon");
                FontIcons.applyFontCssClass(icon, iconFont);

                // Text Label
                Label textLabel = new Label(item.toString());
//                textLabel.setFont(font);
                FontIcons.applyFontCssClass(textLabel, iconFont);       // mora
                textLabel.getStyleClass().add("simple_list");

                // Add both labels to the HBox
                itemContainer.getChildren().addAll(icon, textLabel);

                // Handle selection
                itemContainer.setOnMouseClicked(event -> {
                    selectedItem.set(item);
                    updateHighlighting();
                });

                content.getChildren().add(itemContainer);
            } else {
                label = new Label(item.toString());

//            label.setStyle("-fx-padding: 5px; -fx-background-color: transparent; -fx-text-fill: black;");   // for javaFX
                label.getStyleClass().add("simple_list");
//            label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
//            label.setPrefWidth(200);
                label.setPrefWidth(Double.MAX_VALUE); // Stretch to fit width

                // Add click handler for selection
                label.setOnMouseClicked(event -> {
                    selectedItem.set(item);
                    updateHighlighting();
                });
                content.getChildren().add(label);
            }
        }
        updateHighlighting(); // Ensure initial selection is highlighted
    }


    // Helper method to update highlighting based on selectedItem
    private void updateHighlighting() {
        T selected = selectedItem.get();
        for (Node node : content.getChildren()) {
            node.getStyleClass().remove("simple_list_selected");
            if (selected != null) {
                if (node instanceof Label) {
                    Label label = (Label) node;
                    if (label.getText().equals(selected.toString())) {
                        label.getStyleClass().add("simple_list_selected");
                    }
                } else if (node instanceof HBox) {
                    HBox hbox = (HBox) node;
                    Label textLabel = (Label) hbox.getChildren().get(1); // Assuming textLabel is second child
                    if (textLabel.getText().equals(selected.toString())) {
                        hbox.getStyleClass().add("simple_list_selected");
                    }
                }
            }
        }
    }


    //-----------------------------------------------------
    public static ProgressBar progress_dialog(){
        Label msg = new Label();
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);                // initial
        progressBar.setMaxHeight(10);
        progressBar.getStyleClass().addAll("custom-progress-bar", "glowing");
//        msg.textProperty().bind(progress_msg);
//        var box = new VBox(progressBar);
//        box.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20, 1), null, null)));
//        box.setAlignment(Pos.CENTER);
//        box.setMaxWidth(250);
//        box.setMaxHeight(50);

        return progressBar;
    }
}
