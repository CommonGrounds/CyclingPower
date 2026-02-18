package dev.java4now.web.charts;

import dev.java4now.web.model.futures_fetcher;
import dev.webfx.extras.type.PrimType;
import dev.webfx.extras.visual.VisualColumn;
import dev.webfx.extras.visual.VisualResult;
import dev.webfx.extras.visual.VisualResultBuilder;
import dev.webfx.extras.visual.controls.charts.VisualBarChart;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import static dev.java4now.web.Settings.loadActivityPerMonthFromStorage;

public class SummaryChart {

    static VisualBarChart barChart;
    static VisualResultBuilder vrb;

    static {
        loadActivityPerMonthFromStorage(); // IMPORTANT Load data from storage if available - otherwise it will be empty -
    }

    //------------------------------------------------
    public static VBox get_chart() {
        barChart = createBarChart();
        // Use wrapper approach
        return createChartWrapper(barChart);
    }


    //------------------------------------------------
    public static void resize(double width) {
        if (barChart == null) return;
        barChart.setPrefWidth(width);
        barChart.requestLayout();
        barChart.layout();
    }


    // Workaround: Create a minimal TabPane-like wrapper
    //-----------------------------------------------------------
    private static VBox createChartWrapper(VisualBarChart chart) {
        // Create a TabPane with a single tab to trigger proper rendering
        Tab chartTab = new Tab("", chart);    // .tab-header-area je default css
        chartTab.setClosable(false);

        TabPane wrapper = new TabPane(chartTab);
        wrapper.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Hide tab headers to make it look like a simple container
//        wrapper.setStyle("-fx-tab-max-height: 0; -fx-tab-min-height: 0;");
//        wrapper.getStyleClass().add("tab-pane");
        var root = new VBox(wrapper);
        root.setAlignment(Pos.CENTER);
        return root;
        //       return null;
    }



    //---------------------------------------------------------------------
    private static VisualResult createColumnFormatXYVisualResult() {

        vrb = new VisualResultBuilder(12,
                VisualColumn.create("Year", PrimType.STRING),
                VisualColumn.create("Rides - " + futures_fetcher.chosen_year, PrimType.INTEGER)
        );
        final int[] count = {0};
        futures_fetcher.activity_per_month.forEach((m, counter) -> {
                    String monthName = m.toString().substring(0, 1).toUpperCase() + m.toString().substring(1, 3).toLowerCase(); // 1. slovo veliko, ostale malo
//                    Console.log("Month: " + monthName + ", Activities: " + counter);
                    vrb.setValue(count[0], 0, monthName);
                    vrb.setValue(count[0], 1, counter);
                    count[0]++;
                }
        );

        return vrb.build();
    }


    //-----------------------------------------------------------
    private static VisualBarChart createBarChart() {
        VisualBarChart chart = new VisualBarChart();
        //chart.setVisualResult(createColumnFormatXYVisualResult());
//        chart.setVisualResult(createRowFormatXYVisualResult());

        chart.setVisualResult(createColumnFormatXYVisualResult());
        chart.autosize();
        return chart;
    }


    //-----------------------------------------------------------
    public static void update_data() {
        if (barChart == null) return;

        vrb = new VisualResultBuilder(12,
                VisualColumn.create("Year", PrimType.STRING),
                VisualColumn.create("Rides - " + futures_fetcher.chosen_year, PrimType.INTEGER)
        );

        final int[] count = {0};
        futures_fetcher.activity_per_month.forEach((m, counter) -> {
                    String monthName = m.toString().substring(0, 1).toUpperCase() + m.toString().substring(1, 3).toLowerCase(); // 1. slovo veliko, ostale malo
//                    Console.log("Month: " + monthName + ", Activities: " + counter);
                    vrb.setValue(count[0], 0, monthName);
                    vrb.setValue(count[0], 1, counter);
                    count[0]++;
                }
        );

        barChart.setVisualResult(vrb.build());
    }
}
