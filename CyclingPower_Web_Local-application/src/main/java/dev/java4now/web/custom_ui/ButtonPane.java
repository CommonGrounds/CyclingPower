package dev.java4now.web.custom_ui;

import dev.webfx.platform.console.Console;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ButtonPane extends Pane {

    Label caption;
    Text icon;

    public ButtonPane(String name, String icon_name,Boolean small) {
        super();
        this.caption = new Label(name);
        this.icon = new Text(icon_name);
        this.getChildren().addAll(icon, caption);
        if(!small){
            caption.getStyleClass().add("font-icon-label");
            setPadding(new Insets(2, 0, 2, 0)); // top, right, bottom, left
        } else {
            caption.getStyleClass().add("font-icon-label-small");
            setPadding(new Insets(2, 0, 2, 0));
        }
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        double y = getHeight() / 2 - caption.getLayoutBounds().getHeight() / 2;
        caption.setLayoutY(y);
        caption.setLayoutX(15);
        icon.setLayoutX(caption.getLayoutX() + caption.getLayoutBounds().getWidth() + 5);
        icon.setLayoutX(getWidth() - icon.getLayoutBounds().getWidth() - 5);
        icon.setLayoutY(y);
        // Dinamički izračunaj širinu na osnovu sadržaja
        double contentWidth = caption.getLayoutBounds().getWidth() +
                icon.getLayoutBounds().getWidth() + 25;
        setPrefWidth(contentWidth);
        setMaxWidth(contentWidth);
        setMinWidth(contentWidth);
//        Console.log("ButtonPane contentWidth: " + contentWidth);
    }

    public void setIconFont(Font font){
        icon.setFont(font);
    }

    public void setIconStyle(String style1, String style2){
        this.icon.getStyleClass().addAll(style1, style2);
    }

    public void setCaption(String txt){
        this.caption.setText(txt);
    }
    public void setIconText(String txt){
        this.icon.setText(txt);
    }
}
