package dev.java4now.web.custom_ui;

import dev.java4now.web.CyclingPower_Web_Local;
import dev.java4now.web.icons.Bootstrap_Icons;
import dev.java4now.web.icons.CarbonIcons;
import dev.webfx.platform.console.Console;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

import static dev.java4now.web.graph.CanvasChartPane.bootstrap_font;
import static dev.java4now.web.graph.CanvasChartPane.graph_type;
import static dev.java4now.web.view.LeftPane.font;
import static dev.java4now.web.view.LeftPane.font_ca;

public class CustomMenuButton extends VBox {

    private final ButtonPane menuButton;
    private final ContextMenu contextMenu;
    private final List<MenuItem> menuItems;
    private double layout_x,layout_y,width;
    Text txt_icon;

    // Dodajte promenljive za praćenje trenutno selektovane stavke
    private MenuItem currentlySelectedItem;
    private final String DEFAULT_ICON = "BAR_CHART_LINE";
    private final String SELECTED_ICON = "HAND_INDEX_THUMB_FILL";

    public CustomMenuButton(String text) {
//        this.menuButton = new Button(" " + text + " " + CarbonIcons.getIcon("CARET_SORT_DOWN") + " "); // Dropdown arrow icon
        this.menuButton = new ButtonPane( text , CarbonIcons.getIcon("CARET_SORT_DOWN"),false);
        menuButton.setIconFont(font_ca);
//        menuButton.setTextAlignment(TextAlignment.CENTER);
//        this.menuButton.setOnAction(event -> toggleMenu());
        this.menuButton.setOnMouseClicked(event -> toggleMenu());
        this.contextMenu = new ContextMenu();
        this.contextMenu.getStyleClass().add("context-menu");
        this.menuItems = new ArrayList<>();
        setAlignment(Pos.CENTER);   // center u ovom vbox-u a on ce biti postavljen na aligment poziciji parenta

        setupUI();
        setupBehavior();
    }

    private void setupUI() {
        // Button styling via CSS class
        menuButton.getStyleClass().addAll("font-icon-carbon", "custom-menu-button");
        menuButton.setIconStyle("font-icon-carbon", "caret_sign");

        // Add to layout
        getChildren().add(menuButton);
    }

    private void setupBehavior() {
        // Example menu items with Feather Icons and actions
        addMenuItem("Distance Graph", "BAR_CHART_LINE", () -> {
            graph_type.set("Distance");
            if(CyclingPower_Web_Local.graphicon != null)CyclingPower_Web_Local.graphicon.refresh(); // force layoutChildren and draw method
            contextMenu.hide();
        });
        addMenuItem("Speed Graph", "BAR_CHART_LINE", () -> {
//            txt_icon.setText(Bootstrap_Icons.getIcon("BAR_CHART_LINE_FILL"));
            graph_type.set("Speed");
            if(CyclingPower_Web_Local.graphicon != null)CyclingPower_Web_Local.graphicon.refresh();
            contextMenu.hide();
        });
        addMenuItem("Power Graph", "BAR_CHART_LINE", () -> {
            graph_type.set("Power");
            if(CyclingPower_Web_Local.graphicon != null)CyclingPower_Web_Local.graphicon.refresh();
            contextMenu.hide();
        });
        addMenuItem("Temp Graph", "BAR_CHART_LINE", () -> {
            graph_type.set("Temperature");
            if(CyclingPower_Web_Local.graphicon != null)CyclingPower_Web_Local.graphicon.refresh();
            contextMenu.hide();
        });
        addMenuItem("Wind Graph", "BAR_CHART_LINE", () -> {
            graph_type.set("Wind");
            if(CyclingPower_Web_Local.graphicon != null)CyclingPower_Web_Local.graphicon.refresh();
            contextMenu.hide();
        });

        // Populate context menu
        contextMenu.getItems().setAll(menuItems);
        setSelectedMenuItem(0);
    }

    public void addMenuItem(String text, String iconName, Runnable action) {
        MenuItem item = new MenuItem();
        String iconChar = Bootstrap_Icons.getIcon(iconName);
        if ("Invalid".equals(iconChar)) {
            Console.log("Icon not found for: " + iconName);
            iconChar = Bootstrap_Icons.getIcon("BAR_CHART_LINE"); // Fallback icon
        }

        // Use HBox for consistent icon-text alignment in menu items
        txt_icon = new Text(iconChar);
        txt_icon.setFont(bootstrap_font);
        txt_icon.getStyleClass().addAll("bootstrap-menu-item","context-menu-icon");
//        txt_icon.setFill(Color.LAWNGREEN);
        var lbl_text = new Label(text);
        lbl_text.setFont(font);
        lbl_text.getStyleClass().add("feather-menu-item");
        HBox itemLabelContainer = new HBox(txt_icon, lbl_text);
        HBox.setMargin(txt_icon, new javafx.geometry.Insets(0, 10, 0, 0));
        itemLabelContainer.setAlignment(Pos.CENTER_LEFT);
//        itemLabelContainer.setSpacing(5); // Gap between icon and text
        itemLabelContainer.getStyleClass().add("hbox-container"); // Dodaj CSS class za HBox
        item.setGraphic(itemLabelContainer);

        // Modifikovana akcija za ažuriranje ikona
        item.setOnAction(event -> {
            // Vratite prethodnu selektovanu stavku na običnu ikonu
            if (currentlySelectedItem != null) {
                updateMenuItemIcon(currentlySelectedItem, DEFAULT_ICON);
            }

            // Postavite novu selektovanu stavku i ažurirajte njenu ikonu
            currentlySelectedItem = item;
            updateMenuItemIcon(currentlySelectedItem, SELECTED_ICON);

            // Izvršite originalnu akciju
            action.run();
            contextMenu.hide();

//            menuButton.setCaption(graph_type.get()/*.substring(0,5)*/);
        });

        item.getStyleClass().add("custom-menu-item");
        menuItems.add(item);
    }

    // Pomoćna metoda za ažuriranje ikone MenuItem-a
    private void updateMenuItemIcon(MenuItem menuItem, String iconName) {
        HBox graphicContainer = (HBox) menuItem.getGraphic();
        if (graphicContainer != null && graphicContainer.getChildren().size() > 0) {
            Text iconText = (Text) graphicContainer.getChildren().get(0);
            String newIconChar = Bootstrap_Icons.getIcon(iconName);
            if (!"Invalid".equals(newIconChar)) {
                iconText.setText(newIconChar);
                if(iconName.equals("HAND_INDEX_THUMB_FILL")){
                    iconText.rotateProperty().set(90);
                }else {
                    iconText.rotateProperty().set(0);
                }
            }
        }
    }

    private void toggleMenu() {
        if (!contextMenu.isShowing()) {
            // Forsiraj layout menu item-a pre prikazivanja , resava problem preklapanja pri inicijalnoj aktivaciji
            for (MenuItem item : menuItems) {
                if (item.getGraphic() instanceof HBox) {
                    HBox hbox = (HBox) item.getGraphic();
//                    hbox.applyCss(); // nema za GWT
                    hbox.layout();
                }
            }
            // Convert button's local coordinates to screen coordinates
            double adjustedX = menuButton.localToScene(0, 0).getX() + menuButton.getScene().getWindow().getX();
            double adjustedY = menuButton.localToScene(0, menuButton.getHeight()).getY() + menuButton.getScene().getWindow().getY();
            Console.log("Showing menu at scene coords: x=" + adjustedX + ", y=" + adjustedY);
            contextMenu.show(menuButton, adjustedX, adjustedY);
        } else {
            contextMenu.hide();
        }
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        // Update layoutX and layoutY based on current position
    }

    public void setPosition(double layout_x,double layout_y){
        this.layout_x = layout_x;
        this.layout_y = layout_y;
    }

    public double getActualWidth(){
        return width;
    }

    public void setActualWidth(double width){
        menuButton.setPrefWidth(width);
        menuButton.setMaxWidth(width);
        this.width = width;
    }

    // Dodatna metoda za postavljanje inicijalne selektovane stavke
    public void setSelectedMenuItem(int index) {
        if (index >= 0 && index < menuItems.size()) {
            MenuItem item = menuItems.get(index);
            if (currentlySelectedItem != null) {
                updateMenuItemIcon(currentlySelectedItem, DEFAULT_ICON);
            }
            currentlySelectedItem = item;
            updateMenuItemIcon(currentlySelectedItem, SELECTED_ICON);
        }
    }

    public void setText(String text){
        menuButton.setCaption(text);
    }
}