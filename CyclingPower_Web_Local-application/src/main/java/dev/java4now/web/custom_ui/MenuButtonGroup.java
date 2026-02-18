package dev.java4now.web.custom_ui;

import dev.java4now.web.icons.CarbonIcons;
import dev.java4now.web.icons.Feather_Icons;
import dev.webfx.platform.console.Console;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static dev.java4now.web.view.LeftPane.font;
import static dev.java4now.web.view.LeftPane.font_ca;
import static dev.java4now.web.view.UpPane.month_btn;
//import static dev.java4now.web.view.LeftPane.month_btn;

public class MenuButtonGroup extends VBox {

    private final StringProperty selectedValue = new SimpleStringProperty();

    private final ButtonPane menuButton;
    private final ContextMenu contextMenu;
    private final List<MenuItem> menuItems;
    private double layout_x,layout_y,width;

    public MenuButtonGroup(String text) {
        this.menuButton = new ButtonPane( text , CarbonIcons.getIcon("CARET_SORT_DOWN"),true);
        menuButton.setIconFont(font_ca);                // CarbonIcons
//        menuButton.setTextAlignment(TextAlignment.CENTER);
//        this.menuButton.setOnAction(event -> toggleMenu());
        this.menuButton.setOnMouseClicked(event -> toggleMenu());
        this.contextMenu = new ContextMenu();
        this.menuItems = new ArrayList<>();
        setAlignment(Pos.CENTER);   // center u ovom vbox-u a on ce biti postavljen na aligment poziciji parenta

        setupUI();
        if ("Months".equals(text)) {
            setupMonthsToCurrent(); // Add months for "Months" button
//            setupAllMonths();
        }else if ("Year".equals(text)) {
            setupYears();
        } else {
//            setupBehavior(); // Default behavior for other buttons
        }
    }

    private void setupUI() {
        menuButton.getStyleClass().addAll("font-icon-carbon", "custom-menu-button");
        menuButton.setIconStyle("font-icon-carbon", "caret_sign");

        // Add to layout
        getChildren().add(menuButton);
    }

    public StringProperty selectedValueProperty() {
        return selectedValue;
    }

    public String getSelectedValue() {
        return selectedValue.get();
    }

    public void addMenuItem(String text, String iconName, Runnable action) {
        MenuItem item = new MenuItem();
        String iconChar = Feather_Icons.getChar(iconName);
        if ("Invalid".equals(iconChar)) {
            Console.log("Icon not found for : " + iconName);
            iconChar = Feather_Icons.getChar("HELP_CIRCLE"); // Fallback icon
        }
// Use HBox for consistent icon-text alignment in menu items
        var txt_icon = new Text(iconChar);
        txt_icon.setFont(font);
        txt_icon.getStyleClass().addAll("feather-menu-item","context-menu-icon");
//        lbl_icon.setTextFill(Color.LAWNGREEN);
        var lbl_text = new Label(text);
        lbl_text.setFont(font);
        HBox itemLabelContainer = new HBox(txt_icon, lbl_text);
        HBox.setMargin(txt_icon, new javafx.geometry.Insets(0, 5, 0, 0));
        itemLabelContainer.setAlignment(Pos.CENTER_LEFT);
        itemLabelContainer.setSpacing(5); // Gap between icon and text
        item.setGraphic(itemLabelContainer);
        item.setOnAction(event -> {
//            selectedValue.set(text); // Update on selection
            action.run();
            contextMenu.hide();
        });
        item.getStyleClass().add("custom-small-menu-item");
        menuItems.add(item);
    }

    private void toggleMenu() {
        if (!contextMenu.isShowing()) {
// Get the button's bottom-left corner in scene coordinates
            var point = menuButton.localToScene(0, 0/*menuButton.getHeight()*/);
            Console.log("Showing menu at scene coords: x=" + point.getX() + ", y=" + point.getY());
            contextMenu.show(menuButton, point.getX(), point.getY());
//          contextMenu.show(menuButton, Side.BOTTOM,adjustedX, adjustedY);
        } else {
            Console.log("menu hide");
            contextMenu.hide();
        }
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        // Update layoutX and layoutY based on current position
        this.layout_x = getLayoutX();
        this.layout_y = getLayoutY();
//        menuButton.setPrefWidth(width);
//        Console.log("Updated position - layoutX: " + layoutX + ", layoutY: " + layoutY);
    }

    private void setupMonthsToCurrent() {
        contextMenu.getItems().clear();
        menuItems.clear();
        LocalDate now = LocalDate.now(); // April 6, 2025
        int currentYear = now.getYear(); // 2025
        Month currentMonth = now.getMonth(); // APRIL
        for (Month month : Month.values()) {
            if (month.getValue() <= currentMonth.getValue()) {
                String monthName = month.toString().substring(0, 1).toUpperCase() + month.toString().substring(1).toLowerCase(); // 1. slovo veliko, ostale malo
                addMenuItem(monthName, "CALENDAR", () -> {
                    Console.log("Selected month: " + monthName);
                    // Add your action here, e.g., filter data by month
//                    menuButton.setText(" " + monthName + " " + CarbonIcons.getIcon("CARET_SORT_DOWN") + " ");
                    menuButton.setCaption(monthName);
                    menuButton.setIconText(CarbonIcons.getIcon("CARET_SORT_DOWN"));
                    selectedValue.set(" " + monthName + " " + CarbonIcons.getIcon("CARET_SORT_DOWN") + " ");
//                    contextMenu.hide();
                });
            }
        }
        contextMenu.getItems().setAll(menuItems);
        String text = " " + currentMonth + " " + CarbonIcons.getIcon("CARET_SORT_DOWN") + " ";
//        menuButton.setText(" " + currentMonth + " " + CarbonIcons.getIcon("CARET_SORT_DOWN") + " ");
        menuButton.setCaption(currentMonth.toString());
        menuButton.setIconText(CarbonIcons.getIcon("CARET_SORT_DOWN"));
        selectedValue.set(text);
    }


    private void setupAllMonths() {
        contextMenu.getItems().clear();
        menuItems.clear();
        for (Month month : Month.values()) {
            String monthName = month.toString().substring(0, 1).toUpperCase() + month.toString().substring(1).toLowerCase();
            addMenuItem(monthName, "CALENDAR", () -> {
                Console.log("Selected month: " + monthName);
//                menuButton.setText(" " + monthName + " " + CarbonIcons.getIcon("CARET_SORT_DOWN") + " ");
                menuButton.setCaption(monthName);
                menuButton.setIconText(CarbonIcons.getIcon("CARET_SORT_DOWN"));
                selectedValue.set(" " + monthName + " " + CarbonIcons.getIcon("CARET_SORT_DOWN") + " ");
//                contextMenu.hide();
            });
        }
        contextMenu.getItems().setAll(menuItems);
    }


    private void setupYears() {
        LocalDate now = LocalDate.now(); // April 6, 2025
        int currentYear = now.getYear(); // 2025
        for (int i = 0;i<8;i++) {
            int year = currentYear-i;
            addMenuItem(String.valueOf(year), "CALENDAR", () -> {
                Console.log("Selected year: " + year);
                if(year == currentYear){
                    month_btn.setupMonthsToCurrent();
                }else{
                    month_btn.setupAllMonths();
                }
//                menuButton.setText(" " + year + " " + CarbonIcons.getIcon("CARET_SORT_DOWN") + " ");
                menuButton.setCaption(String.valueOf(year));
                menuButton.setIconText(CarbonIcons.getIcon("CARET_SORT_DOWN"));
                selectedValue.set(" " + year + " " + CarbonIcons.getIcon("CARET_SORT_DOWN") + " ");
                // Immediately update and show the month menu
                month_btn.toggleMenu(); // Close if open
                month_btn.toggleMenu(); // Open with updated items
//                contextMenu.hide();
            });
        }
        contextMenu.getItems().setAll(menuItems);
        String text = " " + currentYear + " " + CarbonIcons.getIcon("CARET_SORT_DOWN") + " ";
//        menuButton.setText(" " + currentYear + " " + CarbonIcons.getIcon("CARET_SORT_DOWN") + " ");
        menuButton.setCaption(String.valueOf(currentYear));
        menuButton.setIconText(CarbonIcons.getIcon("CARET_SORT_DOWN"));
        selectedValue.set(text);
    }
}
