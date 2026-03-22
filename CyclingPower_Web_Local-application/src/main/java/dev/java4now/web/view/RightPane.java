package dev.java4now.web.view;

import dev.java4now.web.CyclingPower_Web_Local;
import dev.java4now.web.charts.MovingChart;
import dev.java4now.web.charts.SummaryChart;
import dev.java4now.web.custom_ui.CustomTooltip;
import dev.java4now.web.custom_ui.TooltipHelper;
import dev.java4now.web.util.SustainabilityCalculator;
import dev.webfx.extras.webtext.HtmlText;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.os.OperatingSystem;
import dev.webfx.platform.util.Arrays;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

import static dev.java4now.web.CyclingPower_Web_Local.*;

public class RightPane {

    public static ScrollPane getPaneScroll(){

        boolean PIE_CHART_IS_HERE;
        boolean PASTAFARIAN_IS_HERE;
        if ((screen_width < 800 || screen_height < 600) && pie_chart == null){
//            Console.log("pie_chart created in right pane");
            pie_chart = MovingChart.get_chart();
            bar_chart = SummaryChart.get_chart();
//            pie_chart.setAlignment(Pos.BOTTOM_CENTER);
            PIE_CHART_IS_HERE = true;
            PASTAFARIAN_IS_HERE = true;
        } else {
            PIE_CHART_IS_HERE = false;
            PASTAFARIAN_IS_HERE = false;
        }

        var lbl_time = new Label("Moving Time");
        lbl_time.textProperty().bind(new StringBinding() {
            { bind(IS_TOTAL_TIME); }
            @Override
            protected String computeValue() {
//                Console.log(IS_TOTAL_TIME.get() ? "Total Time" : "Moving Time");
                return IS_TOTAL_TIME.get() ? "Total Time" : "Moving Time";
            }
        });
        var lbl_time2 = new Label();
        lbl_time2.getStyleClass().add("num_label");
        lbl_time2.textProperty().bind(moving_time);
        var lbl_speed = new Label("Avg Speed ( Km/h )");
        var lbl_speed2 = new Label();
        lbl_speed2.getStyleClass().add("num_label");
        lbl_speed2.textProperty().bind(avg_speed);
        var lbl_distance = new Label("Tot Distance ( Km )");
        var lbl_distance2 = new Label();
        lbl_distance2.getStyleClass().add("num_label");
        lbl_distance2.textProperty().bind(total_distance);
        var lbl_power = new Label("Avg Power ( W )");
        var lbl_power2 = new Label();
        lbl_power2.getStyleClass().add("num_label");
        lbl_power2.textProperty().bind(avg_power);
        var lbl_max_alt = new Label("Max Altitude ( m )");
        var lbl_max_alt2 = new Label();
        lbl_max_alt2.getStyleClass().add("num_label");
        lbl_max_alt2.textProperty().bind(maximum_alt);
        var lbl_min_alt = new Label("Min Altitude ( m )");
        var lbl_min_alt2 = new Label();
        lbl_min_alt2.getStyleClass().add("num_label");
        lbl_min_alt2.textProperty().bind(minimum_alt);
        var lbl_cal = new Label("Calories ( kcal )");
        var lbl_cal2 = new Label();
        lbl_cal2.getStyleClass().add("num_label");
        lbl_cal2.textProperty().bind(total_cal);
        lbl_cal2.setOnMouseEntered(e -> {
//            String html = SustainabilityCalculator.getHtml(Double.valueOf(CyclingPower_Web_Local.total_cal.get()));
//            Console.log("html: " + html);
    //        TooltipHelper.showQuickTooltip(lbl_cal2,html, CustomTooltip.TooltipPosition.UP_LEFT,true);
        });
        var lbl_cad = new Label("Avg Cadence ( rpm )");
        var lbl_cad2 = new Label();
        lbl_cad2.getStyleClass().add("num_label");
        lbl_cad2.textProperty().bind(avg_cad);

        HtmlText eco_html = new HtmlText(SustainabilityCalculator.getHtml(Double.valueOf(CyclingPower_Web_Local.total_cal.get())));
        eco_html.textProperty().bind(new StringBinding() {
            { bind(total_cal); }
            @Override
            protected String computeValue() {
                return SustainabilityCalculator.getHtml(Double.valueOf(CyclingPower_Web_Local.total_cal.get()));
            }
        });

        // Create the VBox with non-null nodes only
        List<Node> nodes = Arrays.asList(
                PIE_CHART_IS_HERE ? server_lbl : null,
                PIE_CHART_IS_HERE ? path_lbl : null,
                PIE_CHART_IS_HERE ? pie_chart : null,
                lbl_time,
                lbl_time2,
                lbl_speed,
                lbl_speed2,
                lbl_distance,
                lbl_distance2,
                lbl_power,
                lbl_power2,
                lbl_max_alt,
                lbl_max_alt2,
                lbl_min_alt,
                lbl_min_alt2,
                lbl_cad,
                lbl_cad2,
                lbl_cal,
                lbl_cal2,
                eco_html,
                PIE_CHART_IS_HERE ? name_lbl : null,
                PIE_CHART_IS_HERE ? name_group : null,
                PIE_CHART_IS_HERE ? bar_chart : null,
                PASTAFARIAN_IS_HERE ? gnu_btn : null
        ).stream().filter(node -> node != null).collect(Collectors.toList());

//        Console.log("PieChart: " + (pie_chart != null) + " ");
        var right_pane = new VBox(nodes.toArray(new Node[0]));
        right_pane.setAlignment(Pos.TOP_CENTER);
        if (PIE_CHART_IS_HERE) {
//            VBox.setMargin(pie_chart, new Insets(40, 0, 0, 0));
//            VBox.setMargin(lbl_time, new Insets(20, 0, 0, 0));
            MovingChart.resize(right_pane.getWidth() );
            VBox.setMargin(name_lbl, new Insets(20, 0, 0, 0)); // log
            VBox.setMargin(name_group, new Insets(0, 0, 0, 0)); // log
            VBox.setMargin(server_lbl, new Insets(10, 0, 0, 0)); // 0
            VBox.setMargin(path_lbl, new Insets(0, 0, 0, 0));    // 1
            VBox.setMargin(bar_chart, new Insets(0, 0, 0, 0)); // zbog pastafarian btn ali ne treba ( bottom 0 )
        }
        VBox.setMargin(lbl_time2, new Insets(0, 0, 20, 0));
        VBox.setMargin(lbl_speed2, new Insets(0, 0, 20, 0));
        VBox.setMargin(lbl_distance2, new Insets(0, 0, 20, 0));
        VBox.setMargin(lbl_power2, new Insets(0, 0, 20, 0));
        VBox.setMargin(lbl_max_alt2, new Insets(0, 0, 20, 0));
        VBox.setMargin(lbl_min_alt2, new Insets(0, 0, 20, 0));
        VBox.setMargin(lbl_cal2, new Insets(0, 0, 20, 0));
        VBox.setMargin(eco_html, new Insets(0, 20, 0, 20));
        VBox.setVgrow(graphicon, Priority.ALWAYS);
//        var gridScroll = new ScrollPane( middle_box );       // TODO - ne radi po defaultu
//        gridScroll.setFitToWidth(true);
//        gridScroll.setFitToHeight(true);
        right_pane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if(PIE_CHART_IS_HERE)MovingChart.resize( newVal.doubleValue() );
//            chart.setPrefWidth(newVal.doubleValue());
//            root.setPrefWidth(newVal.doubleValue());
//            chart.requestLayout();
//            chart.layout();
        });

            ScrollPane scrollPane = new ScrollPane(right_pane);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            scrollPane.setPrefWidth(screen_width * 0.2);
            scrollPane.setPrefHeight(screen_height);
            scrollPane.setPadding(new Insets(0, 0, 0, 0));
            scrollPane.setVvalue(0);
            scrollPane.setHvalue(0);
            return scrollPane;
    }


    //-----------------------------------------------------
    public static VBox getPane(){

        boolean PIE_CHART_IS_HERE;
        boolean PASTAFARIAN_IS_HERE;
        if ((screen_width < 800 || screen_height < 600) && pie_chart == null){
//            Console.log("pie_chart created in right pane");
            pie_chart = MovingChart.get_chart();
            bar_chart = SummaryChart.get_chart();
//            pie_chart.setAlignment(Pos.BOTTOM_CENTER);
            PIE_CHART_IS_HERE = true;
            PASTAFARIAN_IS_HERE = true;
        } else {
            PIE_CHART_IS_HERE = false;
            PASTAFARIAN_IS_HERE = false;
        }

        var lbl_time = new Label("Moving Time");
        lbl_time.textProperty().bind(new StringBinding() {
            { bind(IS_TOTAL_TIME); }
            @Override
            protected String computeValue() {
//                Console.log(IS_TOTAL_TIME.get() ? "Total Time" : "Moving Time");
                return IS_TOTAL_TIME.get() ? "Total Time" : "Moving Time";
            }
        });
        var lbl_time2 = new Label();
        lbl_time2.getStyleClass().add("num_label");
        lbl_time2.textProperty().bind(moving_time);
        var lbl_speed = new Label("Avg Speed ( Km/h )");
        var lbl_speed2 = new Label();
        lbl_speed2.getStyleClass().add("num_label");
        lbl_speed2.textProperty().bind(avg_speed);
        var lbl_distance = new Label("Tot Distance ( Km )");
        var lbl_distance2 = new Label();
        lbl_distance2.getStyleClass().add("num_label");
        lbl_distance2.textProperty().bind(total_distance);
        var lbl_power = new Label("Avg Power ( W )");
        var lbl_power2 = new Label();
        lbl_power2.getStyleClass().add("num_label");
        lbl_power2.textProperty().bind(avg_power);
        var lbl_max_alt = new Label("Max Altitude ( m )");
        var lbl_max_alt2 = new Label();
        lbl_max_alt2.getStyleClass().add("num_label");
        lbl_max_alt2.textProperty().bind(maximum_alt);
        var lbl_min_alt = new Label("Min Altitude ( m )");
        var lbl_min_alt2 = new Label();
        lbl_min_alt2.getStyleClass().add("num_label");
        lbl_min_alt2.textProperty().bind(minimum_alt);
        var lbl_cal = new Label("Calories ( kcal )");
        var lbl_cal2 = new Label();
        lbl_cal2.getStyleClass().add("num_label");
        lbl_cal2.textProperty().bind(total_cal);
        lbl_cal2.setOnMouseEntered(e -> {
//            String html = SustainabilityCalculator.getHtml(Double.valueOf(CyclingPower_Web_Local.total_cal.get()));
//            Console.log("html: " + html);
            //        TooltipHelper.showQuickTooltip(lbl_cal2,html, CustomTooltip.TooltipPosition.UP_LEFT,true);
        });
        var lbl_cad = new Label("Avg Cadence ( rpm )");
        var lbl_cad2 = new Label();
        lbl_cad2.getStyleClass().add("num_label");
        lbl_cad2.textProperty().bind(avg_cad);

        eco_html = new HtmlText(SustainabilityCalculator.getHtml(Double.valueOf(CyclingPower_Web_Local.total_cal.get())));
        eco_html.setPrefWidth(Region.USE_PREF_SIZE);
        eco_html.textProperty().bind(new StringBinding() {
            { bind(total_cal); }
            @Override
            protected String computeValue() {
                return SustainabilityCalculator.getHtml(Double.valueOf(CyclingPower_Web_Local.total_cal.get()));
            }
        });

        // Create the VBox with non-null nodes only
        List<Node> nodes = Arrays.asList(
                PIE_CHART_IS_HERE ? server_lbl : null,
                PIE_CHART_IS_HERE ? path_lbl : null,
                PIE_CHART_IS_HERE ? pie_chart : null,
                lbl_time,
                lbl_time2,
                lbl_speed,
                lbl_speed2,
                lbl_distance,
                lbl_distance2,
                lbl_power,
                lbl_power2,
                lbl_max_alt,
                lbl_max_alt2,
                lbl_min_alt,
                lbl_min_alt2,
                lbl_cad,
                lbl_cad2,
                lbl_cal,
                lbl_cal2,
                eco_html,
                PIE_CHART_IS_HERE ? name_lbl : null,
                PIE_CHART_IS_HERE ? name_group : null,
                PIE_CHART_IS_HERE ? bar_chart : null,
                PASTAFARIAN_IS_HERE ? gnu_btn : null
        ).stream().filter(node -> node != null).collect(Collectors.toList());

//        Console.log("PieChart: " + (pie_chart != null) + " ");
        var right_pane = new VBox(nodes.toArray(new Node[0]));
        right_pane.setAlignment(Pos.TOP_CENTER);
        if (PIE_CHART_IS_HERE) {
//            VBox.setMargin(pie_chart, new Insets(40, 0, 0, 0));
//            VBox.setMargin(lbl_time, new Insets(20, 0, 0, 0));
            MovingChart.resize(right_pane.getWidth() );
            VBox.setMargin(name_lbl, new Insets(20, 0, 0, 0)); // log
            VBox.setMargin(name_group, new Insets(0, 0, 0, 0)); // log
            VBox.setMargin(server_lbl, new Insets(10, 0, 0, 0)); // 0
            VBox.setMargin(path_lbl, new Insets(0, 0, 0, 0));    // 1
            VBox.setMargin(bar_chart, new Insets(0, 0, 0, 0)); // zbog pastafarian btn ali ne treba ( bottom 0 )
        }
        VBox.setMargin(lbl_time2, new Insets(0, 0, 20, 0));
        VBox.setMargin(lbl_speed2, new Insets(0, 0, 20, 0));
        VBox.setMargin(lbl_distance2, new Insets(0, 0, 20, 0));
        VBox.setMargin(lbl_power2, new Insets(0, 0, 20, 0));
        VBox.setMargin(lbl_max_alt2, new Insets(0, 0, 20, 0));
        VBox.setMargin(lbl_min_alt2, new Insets(0, 0, 20, 0));
        VBox.setMargin(lbl_cad2, new Insets(0, 0, 20, 0));
        VBox.setMargin(eco_html, new Insets(20, 20, 0, 20));
        VBox.setVgrow(graphicon, Priority.ALWAYS);
//        var gridScroll = new ScrollPane( middle_box );       // TODO - ne radi po defaultu
//        gridScroll.setFitToWidth(true);
//        gridScroll.setFitToHeight(true);
        right_pane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if(PIE_CHART_IS_HERE)MovingChart.resize( newVal.doubleValue() );
//            chart.setPrefWidth(newVal.doubleValue());
//            root.setPrefWidth(newVal.doubleValue());
//            chart.requestLayout();
//            chart.layout();
        });

            return right_pane;
    }
}
