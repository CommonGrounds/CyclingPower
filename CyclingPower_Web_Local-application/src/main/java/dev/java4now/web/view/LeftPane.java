package dev.java4now.web.view;

import dev.java4now.web.CyclingPower_Web_Local;
import dev.java4now.web.charts.MovingChart;
import dev.java4now.web.charts.SummaryChart;
import dev.java4now.web.custom_ui.CustomTooltip;
import dev.java4now.web.icons.Feather_Icons;
import dev.webfx.platform.os.OperatingSystem;
import dev.webfx.platform.resource.Resource;
import dev.webfx.platform.util.Arrays;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.control.Button;

import java.util.List;
import java.util.stream.Collectors;

import static dev.java4now.web.CyclingPower_Web_Local.*;
import static dev.java4now.web.Settings.SHOW_COFFEE;


public class LeftPane {

    static VBox side;
    public static Font font;
    public static Font font_fa;
    public static Font font_ca;
    static Button menu_btn;
    static PasswordField pass_txt;


    static {
//        default_pic = new Image(Resource.toUrl("pics/bicycle-bike-svgrepo-com.webp", CyclingPower_Web_Local.class), true);
//        default_pic_2 = new Image(Resource.toUrl("pics/IMG_20240526_200404.jpg", CyclingPower_Web.class), true);
//        default_pic_3 = new Image(Resource.toUrl("pics/IMG_20240928_142159.jpg", CyclingPower_Web.class), true);
        font = Font.font(Resource.toUrl("fonts/feather.woff", CyclingPower_Web_Local.class));
        font_fa = Font.font(Resource.toUrl("fonts/fa-solid-900.woff", CyclingPower_Web_Local.class));
        font_ca = Font.font(Resource.toUrl("fonts/Carbon-Icons.woff", CyclingPower_Web_Local.class));
    }


    public static StackPane get_pane(Application app) {

        boolean PIE_CHART_IS_HERE;
// -------------- Left Pane ------------------


// ------------- Settings Pane ------------------
        String menuIconChar = Feather_Icons.getChar("MENU"); // \e09c
//        String menuIconChar = FontAwesomeSolid_Icons.getIcon("HOME"); // Vraća Unicode za home ikonu
//        Console.log("menuIconChar: " + menuIconChar);
        menu_btn = new Button(menuIconChar);
        menu_btn.setTextFill(Color.BLUEVIOLET);
//        var btn = new Button(null, new ImageView(menu));
        menu_btn.setFont(font);                             // font_fa za FontAwesomeSolid ikone
        menu_btn.getStyleClass().addAll("font-icon-button_fe","font-icon-button");  // font-icon-button_fa za FontAwesomeSolid ikone
//        Console.log("font: " + btn.getFont().getFamily());
        CustomTooltip menuTooltip = new CustomTooltip(/*"💔" + */"Expand menu"); // important mora da se doda kao node u parent
//        menuTooltip.setShowArrow(true);
//        menuTooltip.setFont(font);
//        menuTooltip.setFontStyle("font-icon-button_fe");
        menuTooltip.setPosition(CustomTooltip.TooltipPosition.RIGHT);
        menu_btn.setOnMouseEntered(event -> {
//            TooltipHelper.showQuickTooltip(menu_btn,"Expand menu");
            if(!OperatingSystem.isMobile()){
                menuTooltip.showForNode(menu_btn);  // important - Koristiti showForNode metodu jer je bolje pozicioniranje
            }
        });
        menu_btn.setOnMouseExited(event -> {
            menuTooltip.hide();
        });
        menu_btn.setOnAction(e -> {
            menuTooltip.hide();
            UpPane.rootPane.setVisible(true);
            UpPane.rootPane.toFront();
            UpPane.rotate_translate(UpPane.Direction.DOWN);
        });

        if (!OperatingSystem.isMobile() && pie_chart == null){
//            Console.log("pie_chart created in left pane - screen_width: " + screen_width);
            pie_chart = MovingChart.get_chart();
            bar_chart = SummaryChart.get_chart();
//            pie_chart.setAlignment(Pos.TOP_CENTER);
            PIE_CHART_IS_HERE = true;
        } else {
            PIE_CHART_IS_HERE = false;
        }

        /*
        var name_txt = new TextField();
        name_txt.getStyleClass().add("text-field");
        name_txt.setPromptText("Enter Name");
        var email_lbl = new Label("Email");
        var email_txt = new TextField();
        email_txt.getStyleClass().add("text-field");
        email_txt.setPromptText("Enter Email");
        var password_lbl = new Label("Password");
        var pass_group = password_group();
        var save_btn = new Button("Save");
        save_btn.getStyleClass().add("my_button");
        save_btn.setOnAction(e -> {
            User user = new User(name_txt.getText(), pass_txt.getText(), email_txt.getText());
            Object jsonObject = SerialCodecManager.encodeToJson(user);
            String str = Json.formatAny(jsonObject);
            Settings.saveState(name_txt.getText(), email_txt.getText(), pass_txt.getText()); // browser data base
            Console.log("To Json: " + str);
            SaveUser.saveUserData(str);  // server data base
        });
        var response_lbl = new Label();
        response_lbl.textProperty().bind(response_txt);
 */
        String coffeeIconChar = Feather_Icons.getChar("COFFEE"); // \e09c
        var coffee = new Button(coffeeIconChar);
        coffee.setTextFill(Color.BLUE);
        coffee.setFont(font);
        coffee.getStyleClass().add("font-icon-button_big");
        String coffee_url = "https://buymeacoffee.com/PoorCyclist";
        coffee.setOnAction(e -> {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    app.getHostServices().showDocument(coffee_url);
                }
            });
        });
//        String smileIconChar = Feather_Icons.getChar("SMILE"); // \e09c
//        var coffee_lbl0 = new Label("Buy me coffee " + smileIconChar);
        var coffee_lbl0 = new Label("Buy me coffee");
//        coffee_lbl0.setFont(font);
        coffee_lbl0.getStyleClass().add("font-label");
        var coffee_lbl1 = new Label("if you like it");
//        coffee_lbl1.setFont(font);
        coffee_lbl1.getStyleClass().add("font-label");

//        side = new VBox(back, name_lbl, name_txt, email_lbl, email_txt, password_lbl, pass_group, save_btn, response_lbl,coffee_lbl0,coffee_lbl1,coffee);

        // Create the VBox with non-null nodes only
        List<Node> nodes = Arrays.asList(
                menu_btn ,
                menuTooltip ,
                PIE_CHART_IS_HERE ? name_lbl : null,
                PIE_CHART_IS_HERE ? name_group : null,
                PIE_CHART_IS_HERE ? server_lbl : null,
                PIE_CHART_IS_HERE ? path_lbl : null,
                PIE_CHART_IS_HERE ? pie_chart : null,
                PIE_CHART_IS_HERE ? bar_chart : null,
                SHOW_COFFEE ? coffee_lbl0 : null,
                SHOW_COFFEE ? coffee_lbl1 : null,
                SHOW_COFFEE ? coffee : null
        ).stream().filter(node -> node != null).collect(Collectors.toList());
        side = new VBox(nodes.toArray(new Node[0]));
        side.setAlignment(Pos.TOP_CENTER);
        side.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 1), null, null)));
        side.widthProperty().addListener((obs, oldVal, newVal) -> {
            if(PIE_CHART_IS_HERE){
                MovingChart.resize( newVal.doubleValue());
                SummaryChart.resize( newVal.doubleValue());
            }
//            chart.setPrefWidth(newVal.doubleValue());
//            root.setPrefWidth(newVal.doubleValue());
//            chart.requestLayout();
//            chart.layout();
        });

//----------------- Final Layout -----------------
        StackPane side_root = new StackPane( side) {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
//                name_txt.setMaxWidth(side.getWidth() * 0.8);
//                email_txt.setMaxWidth(side.getWidth() * 0.8);
//                pass_group.setMaxWidth(side.getWidth() * 0.8);
                menu_btn.setLayoutX(10);
//                save_btn.setPrefWidth(side.getWidth() * 0.25);
            }
        };

        if(PIE_CHART_IS_HERE) {
            VBox.setMargin(menu_btn, new Insets(10, 0, 10, 0));
            VBox.setMargin(name_group, new Insets(0, 0, 30, 0));
            VBox.setMargin(server_lbl, new Insets(0, 0, 0, 0));
            VBox.setMargin(path_lbl, new Insets(0, 0, 30, 0));
            VBox.setMargin(pie_chart, new Insets(20, 0, 0, 0));
            if (screen_height < 700){
                VBox.setMargin(bar_chart, new Insets(20, 0, 0, screen_width < 1365 ? 0 : 10));
            }else{
                VBox.setMargin(bar_chart, new Insets(60, 0, 0, screen_width < 1366 ? 0 : 10));
            }
        }
//        VBox.setMargin(name_txt, new Insets(0, 0, 20, 0));
//        VBox.setMargin(email_txt, new Insets(0, 0, 20, 0));
//        VBox.setMargin(pass_group, new Insets(0, 0, 60, 0));
//        VBox.setMargin(save_btn, new Insets(0, 0, 30, 0));
//        VBox.setMargin(response_lbl, new Insets(0, 0, 30, 0));
        if (SHOW_COFFEE) {
            VBox.setMargin(coffee_lbl1, new Insets(0, 0, 15, 0));
        }

        return side_root;
    }


    public static void setSideScreen() {
        UpPane.rootPane.toBack();
        UpPane.rootPane.setVisible(false);
        side.toFront();
//        side.setTranslateY(-UpPane.pane.getHeight());
//        side.setVisible(false);
    }


    //------------------------------------------------------------------
    // Alternativno resenje za Android
    public static HBox password_group() {
        // text field to show password as unmasked
        final TextField textField = new TextField();
        textField.getStyleClass().add("text-field");
        // Set initial state
        textField.setPromptText("pass( 6-16 )");
        textField.setVisible(false);

        // Actual password field
        pass_txt = new PasswordField();
        pass_txt.setPromptText("pass( 6-16 )");
//        pass_txt.getStyleClass().add("pass-field");             //TODO standard html css za sada jedino radi - input[type=password]

        // Bind the textField and passwordField text values bidirectionally.
        pass_txt.textProperty().bindBidirectional(textField.textProperty());

        var stack_box = new StackPane(textField, pass_txt);
//        hbox.setMinWidth(200);
//        hbox.setMaxWidth(200);

//        FontIcon featherIcon_on = FeatherIcon.EYE; //FontIcons.newText("EYE");
//        FontIcon featherIcon_off = FeatherIcon.EYE_OFF;

        var rightBtn = new Button(Feather_Icons.getChar("EYE_OFF"));
        rightBtn.setFont(font);
        rightBtn.getStyleClass().add("list-icon");
        rightBtn.setCursor(Cursor.HAND);
//        rightBtn.getStyleClass().addAll(Styles.BUTTON_ICON);
        rightBtn.setOnAction(e -> {
                    if (rightBtn.getText().equals(Feather_Icons.getChar("EYE_OFF"))) {
                        rightBtn.setText(Feather_Icons.getChar("EYE"));
                        pass_txt.setVisible(false);
                        textField.setVisible(true);
                        textField.setText(pass_txt.getText());
                    } else {
                        rightBtn.setText(Feather_Icons.getChar("EYE_OFF"));
                        pass_txt.setVisible(true);
                        textField.setVisible(false);
                        pass_txt.setText(textField.getText());
                    }
                }
        );
        var group = new HBox(stack_box, rightBtn);
        HBox.setHgrow(stack_box, Priority.ALWAYS);
//        group.setMaxWidth(250);
        group.setAlignment(Pos.CENTER);

        return group;
    }
}
