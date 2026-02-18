package dev.java4now.web.charts;

import dev.webfx.extras.type.PrimType;
import dev.webfx.extras.visual.VisualColumn;
import dev.webfx.extras.visual.VisualColumnBuilder;
import dev.webfx.extras.visual.VisualResult;
import dev.webfx.extras.visual.VisualResultBuilder;
import dev.webfx.extras.visual.controls.charts.VisualPieChart;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MovingChart {

    static VisualPieChart pieChart;

    //------------------------------------------------
    public static VBox get_chart(){
        pieChart = createPieChart();
        // Use wrapper approach
        return createChartWrapper(pieChart);
    }


    //------------------------------------------------
    public static void resize(double width){
        if(pieChart == null) return;
        pieChart.setPrefWidth(width);
        pieChart.requestLayout();
        pieChart.layout();
    }



    // Workaround: Create a minimal TabPane-like wrapper
    //-----------------------------------------------------------
    private static VBox createChartWrapper(VisualPieChart chart) {
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


    //----------------------------------------------------------
    private VisualResult createColumnFormatXYVisualResult(double moving, double pause) {
        VisualResultBuilder vrb = new VisualResultBuilder(2,
                new VisualColumnBuilder(null, PrimType.STRING).setRole("series").build(),
                VisualColumn.create("Moving", PrimType.INTEGER),
                VisualColumn.create("Pause", PrimType.INTEGER)
        );
        String m = moving + " %";
        String p = pause + " %";
        moving = (moving+0.005) * 100;
        pause = (pause+0.005) * 100;
        vrb.setValue(0, 0, "Moving");
        vrb.setValue(0, 1, (int)moving);
        vrb.setValue(1, 0, "Pause");
        vrb.setValue(1, 1, (int)pause);
        return vrb.build();
    }



    //----------------------------------------------------------------
    private static VisualResult createRowFormatPieVisualResult(double moving, double pause) {
        VisualResultBuilder vrb = new VisualResultBuilder(2,
                new VisualColumnBuilder(null, PrimType.STRING).setRole("series").build(),
                VisualColumn.create(null, PrimType.DOUBLE)
        );
        // Convert to percentage
        if(pause < 1.0){
            moving = moving * 100;
            pause = pause * 100;
        }
        vrb.setValue(0, 0, "Moving: " + Math.round(moving) + "%");
        vrb.setValue(0, 1, Math.round(moving));
        vrb.setValue(1, 0, "Pause: " + Math.round(pause) + "%");
        vrb.setValue(1, 1, Math.round(pause));
        return vrb.build();
    }


    //--------------------------------------------------------
    private static VisualPieChart createPieChart(){
        VisualPieChart chart = new VisualPieChart();
        //chart.setVisualResult(createColumnFormatPieVisualResult()); // Column format
        chart.setVisualResult(createRowFormatPieVisualResult(0,100)); // Or alternative row format
 //       chart.setPrefSize(800, 600);
        chart.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        chart.autosize();
        return chart;
    }


    //-----------------------------------------------------------
    public static void update_data(double total,double moving_total) {
        if(pieChart == null) return;
//        Console.log("Total: " + total + ", Moving Total: " + moving_total);
        double moving = moving_total / total;
        double pause = 1.0 - moving;
//        Console.log("Moving: " + moving + ", Pause: " + pause);
        pieChart.setVisualResult(createRowFormatPieVisualResult(moving,pause));
    }
}
