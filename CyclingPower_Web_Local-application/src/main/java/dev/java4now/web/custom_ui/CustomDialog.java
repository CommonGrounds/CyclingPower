package dev.java4now.web.custom_ui;

import dev.java4now.web.Settings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;

public class CustomDialog {
    private final String message;
    private final EventHandler<ActionEvent> onResult;
    private BorderPane dialogBox;
    private Pane overlay;

    public CustomDialog(String message, EventHandler<ActionEvent> onResult) {
        this.message = message;
        this.onResult = onResult;
    }

    public void show(Pane parent) {
        // Overlay (semi-transparent background)
        overlay = new Pane();
//        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);"); // Gray overlay
        overlay.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.5), null, null)));
        overlay.setPrefSize(parent.getWidth(), parent.getHeight()); // zacrnjen screen

        // Dialog content
        Label messageLabel = new Label(message);
        messageLabel.setTextFill(Color.WHITE);
        Label lbl0 = new Label("Enter Name");
        lbl0.setTextFill(Color.WHITE);
        TextField name = new TextField();
        Label lbl1 = new Label("Enter Pass");
        TextField pass = new TextField();
        Button okButton = new Button("Ok");
//        okButton.getStyleClass().add("my_button");
        okButton.getStyleClass().add("dialog_button");
        okButton.setTextFill(Color.WHITE);

//        Line line = new Line(0, 0, 100, 0);
        dialogBox = new BorderPane();
        var top_content = new VBox(messageLabel/*, line */);
        top_content.setAlignment(Pos.CENTER);
        var top = new HBox(top_content);
        top.setAlignment(Pos.CENTER);
        dialogBox.setTop(top);
        var center = new VBox(10, lbl0, name);
        dialogBox.setCenter(center);
        center.setAlignment(Pos.CENTER);
        var bottom = new HBox(okButton);
        bottom.setAlignment(Pos.CENTER);
        dialogBox.setBottom(bottom);
        dialogBox.setPadding(new Insets(10, 20, 15, 20));

        Stop[] stops = new Stop[] { new Stop(0, Color.rgb(31, 30, 35)), new Stop(1, Color.rgb(83, 113, 149))};
        LinearGradient linearGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
        BackgroundFill fill = new BackgroundFill(linearGradient, CornerRadii.EMPTY, Insets.EMPTY);
        dialogBox.setBackground(new Background(fill));
//        dialogBox.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 1), null, null)));

        dialogBox.setMaxWidth(Math.max(300, parent.getWidth() / 6));
        dialogBox.setMaxHeight(parent.getHeight() / 3);
        shadowBorder(dialogBox);
//        dialogBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
//        dialogBox.getStyleClass().add("border-neon");
        okButton.setPrefWidth(dialogBox.getMaxWidth() * 0.3);
//        dialogBox.setMaxHeight(Region.USE_COMPUTED_SIZE);

        // Center the dialog
        StackPane dialogPane = new StackPane(overlay, dialogBox);
        dialogPane.setAlignment(Pos.CENTER);

        // Button action
        okButton.setOnAction(e -> {
            onResult.handle(new ActionEvent(name.getText(), null));
            Settings.saveState(name.getText(), "test@gmail.com", "test"/*pass.getText()*/);
            Settings.name_txt.set(name.getText());
            Settings.pass_txt.set(pass.getText());
            close(parent);
        });

        okButton.setOnMouseMoved(e ->
                okButton.setBackground(new Background(new BackgroundFill(Color.rgb(83, 113, 149, 1), null, null)))
        );

        okButton.setOnMouseExited(e ->
                okButton.setBackground(new Background(new BackgroundFill(Color.rgb(43, 42, 49, 1), null, null)))
        );

        okButton.setOnMousePressed(e ->
                okButton.setBackground(new Background(new BackgroundFill(Color.rgb(43, 42, 49, 1), null, null)))
        );

        name.setOnAction(e -> {
            onResult.handle(new ActionEvent(name.getText(), null));
            Settings.saveState(name.getText(), "test@gmail.com", "test"/*pass.getText()*/);
            Settings.name_txt.set(name.getText());
            Settings.pass_txt.set(pass.getText());
            close(parent);
        });

        // Add to parent and block interaction
        parent.getChildren().add(dialogPane);
        overlay.setOnMouseClicked(e -> e.consume()); // Prevent clicks on overlay
    }

    private void close(Pane parent) {
        parent.getChildren().remove(overlay.getParent());
    }


    // ========== 11. SHADOW EFFECT (Kombinovano sa border) ==========
    private static void shadowBorder(Pane pane) {
        pane.setBorder(new Border(new BorderStroke(
                Color.DARKGRAY,
                BorderStrokeStyle.SOLID,
                null,
                new BorderWidths(4, 4, 4, 4)
        )));

        // Dodaj shadow efekat
        javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(10);
        shadow.setOffsetY(4);
        pane.setEffect(shadow);
    }
}
