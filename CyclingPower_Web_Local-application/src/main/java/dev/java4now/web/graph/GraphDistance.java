package dev.java4now.web.graph;

import dev.java4now.web.CyclingPower_Web_Local;
import dev.java4now.web.util.Format;
import dev.java4now.web.util.Helper_light;
import dev.webfx.platform.console.Console;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import service.Service_impl;

import static dev.java4now.web.CyclingPower_Web_Local.activity;
import static dev.java4now.web.CyclingPower_Web_Local.map;
import static dev.java4now.web.graph.CanvasChartPane.*;
import static dev.java4now.web.util.Helper_light.formatTimestamp;

public class GraphDistance {

    public static void draw_distance(double width, double height, Canvas layer_1, Canvas layer_2, GraphicsContext context1, GraphicsContext context2){
        //################### POSTAVLJA BOJU NA TRANSPARENTNO ( OBAVEZNO PRE REDRAW ) ####################
        context1.clearRect(0, 0, width, height);
//        context2.clearRect(0, 0, canvasWidth, canvasHeight);
//################################################################################################

        start_position_x = width / 20;                       // y osa
        start_position_y = height - 40;                      // x osa
        step_vertical = (width - start_position_x) / 9.0;
        step_horizontale = (height - 40) / 5.0;
        graph_step = /*random_data.size()*/  activity.getRecords().size() / width;

        //------------------- Koordinatni sistem ------------
        context1.beginPath();
        context1.setStroke(Color.rgb(150, 150, 150));
        //       Console.log(forecast.time);
        context1.moveTo(start_position_x, start_position_y + 5);
        context1.lineTo(start_position_x, 0);                 // Linija vertikala
        context1.setLineWidth(2);                                      // debela
        context1.stroke();
        context1.closePath();
        context1.beginPath();
        context1.moveTo(start_position_x - 5, height - 40);      // Linija horizontala debela
        context1.lineTo(width, start_position_y);
        context1.stroke();
        context1.closePath();
        context1.beginPath();
//----------------- Linije na osi ----------------------
        context1.setStroke(Color.rgb(200, 200, 200));
        context1.setLineWidth(1);
        // text
        Font font = Font.font("sans-serif", FontWeight.BOLD, FontPosture.REGULAR, 12);
        context1.setFont(font);
        String txt;
        context1.setFill(Color.rgb(150, 150, 150));
        context1.setTextAlign(TextAlignment.CENTER);
        // vertikale po x osi
        double km_seq = (double) km_dist / 9;
        for (int i = 1; i < 9; i++) {
            double current_pos = start_position_x + (step_vertical * i);
            context1.moveTo(current_pos, start_position_y);
            context1.lineTo(current_pos, 5);
            //------- Format na 1 decimalu ----------
//            txt = Format.formatDouble_GWT(km_seq * i,1);
            var pos = Helper_light.map((long) current_pos,
                    (long) start_position_x,
                    (long) layer_2.getWidth(),
                    0L,
                    (long) layer_2.getWidth() - (long) start_position_x);
            txt = Format.formatDouble_GWT(activity.getRecords().get((int) (graph_step * pos)).getDistance()/1000,1);
            //---------------------------------------
            context1.fillText(txt, current_pos, height - 25);
            context1.stroke();
        }
        context1.setTextAlign(TextAlignment.RIGHT);
        context1.fillText("km", width, height - 25);
        // horizontale po y osi
        double max_alt_tmp = max_alt + (max_alt / 10.f);            //  10 % plus za koordinantni sistem
        for (int i = 1; i < 5; i++) {
            double current_pos = start_position_y - (step_horizontale * i);
            context1.moveTo(start_position_x, current_pos);
            context1.lineTo(width, current_pos);
            context1.stroke();
        }
//--------------------- GRAPH  ----------------------
        double scale = ((height - 40) / max_alt_tmp)*0.82;
//        Console.log("scale: " + scale);
        context1.setGlobalAlpha(0.6);
        context1.closePath();
        context1.beginPath();
        context1.setStroke(Color.rgb(2, 68, 2)); // green
        context1.setLineWidth(2);
        for (int i = 0; i < width; i++) {
            double y = activity.getRecords().get((int) (graph_step * i)).getAltitude();
            if (i == 0) {
                context1.moveTo(start_position_x, height-40/*start_position_y*/ - (y * scale));    // TODO - visina graph-a  veca nego sto treba
                continue;
            }
            context1.lineTo(start_position_x + i, height-40/*start_position_y*/ - (y * scale));
//			context1.arc(one_hour_width * i, max_pos - (int)y * scale_temp,2 ,0 ,2 * Math.PI );
            if (i == width - 1) {
                context1.lineTo(width, height-40/*start_position_y*/ - (activity.getRecords().get(activity.getRecords().size() - 1).getAltitude() * scale));
            }
        }
        context1.setGlobalAlpha(0.6);
        context1.stroke();
        context1.setGlobalAlpha(1);

        //------------------ FILL GRAPH SHAPE ( PATH ) - TRANSPARENT -----------------
        context1.save();
        context1.lineTo(width, start_position_y);
        context1.lineTo(start_position_x, start_position_y); // -3 polovina debljine x-ose
        context1.lineTo(start_position_x, (activity.getRecords().get(0).getAltitude() * scale));
// zatvorena putanja koja pocinje od beginPath , nema stroke za deo putanje koji se ne vidi pa ne treba setGlobalAlpha(0);
        context1.closePath();
        Font font_big = Font.font("sans-serif", FontWeight.BOLD, FontPosture.REGULAR, 22);
        context1.setFont(font_big);
        context1.setTextAlign(TextAlignment.RIGHT);
        txt = "Distance";
        context1.fillText(txt,width - Service_impl.measureText(txt, layer_2),start_position_y - 10);
        context1.setGlobalAlpha(0.3);
        context1.setFill(Color.rgb(2, 68, 2)); // green
        context1.fill();
        // Black rectangle ako je mobile
        if (width < 760) {
            context1.setGlobalAlpha(0.5);
            context1.setFill(Color.rgb(0, 0, 0)); // black
            context1.fillRect(start_position_x + 2, 0, 60,start_position_y );
        }
        context1.restore(); // vraca na staro ne treba context1.setGlobalAlpha(1);

        // text po y osi
        if (width < 760) {
            context1.setTextAlign(TextAlignment.LEFT);
        } else {
            context1.setTextAlign(TextAlignment.RIGHT);
        }
        double step_alt = max_alt_tmp / 4;
        for (int i = 1; i < 5; i++) {
            double current_pos = start_position_y - (step_horizontale * i);
            if(i == 4){
                txt = Format.formatDouble_GWT((step_alt * i)*1,0) + " m";
            }else{
                txt = Format.formatDouble_GWT((step_alt * i),0) + " m";
            }
            if (width < 760) {
                context1.setFill(Color.rgb(255, 255, 255) ); // bela
                context1.fillText(txt, start_position_x + 5, current_pos + font.getSize() / 2);
//                context1.setFill(Color.rgb(150, 150, 150));
            } else {
                context1.fillText(txt, start_position_x - 5, current_pos + font.getSize() / 2);
            }
        }
    }



    //----------------------------------------------------------------
    public static void handle_distance(Canvas layer_2, MouseEvent event){
        if(activity == null){
            return;
        }

        // left side
        if(event.getX() < start_position_x){
            context2.clearRect(0, 0, layer_2.getWidth(), layer_2.getHeight()); // brisanje
            map.remove_cursor_marker();
            return;
        }

        double x = layer_2.getLayoutX() + event.getX() + 20; // pozicija pocetka box-a po x
        double y = layer_2.getLayoutY() + event.getY();      // po y
        // right side
        if((event.getX() + 20 + 105) > layer_2.getWidth() ) {
            x = layer_2.getLayoutX() + event.getX() - 20 - 105; // pomeranje pocetka box-a u levo
        }

        context2.clearRect(0, 0, layer_2.getWidth(), layer_2.getHeight()); // brisanje
        context2.beginPath();
        // fill box background
        context2.setFill(Color.rgb(255, 243, 48));
        context2.fillRect(x, layer_2.getLayoutY()+20, 105, 42);
        context2.setStroke(Color.rgb(0, 0, 0));
        // box boundary lines
        context2.setLineWidth(1);
        context2.setGlobalAlpha(0.6);
        context2.strokeRect(x, layer_2.getLayoutY()+20, 105, 42);
        context2.setGlobalAlpha(1);

        // text u box-u

        var pos = Helper_light.map((long) event.getX(),
                (long) start_position_x,
                (long) layer_2.getWidth(),
                0L,
                (long) layer_2.getWidth() - (long) start_position_x);

        context2.setFill(Color.rgb(0, 0, 0));
        Font font = Font.font("sans-serif", FontWeight.SEMI_BOLD, FontPosture.REGULAR, 12);
        context2.setFont(font);
        context2.setTextAlign(TextAlignment.LEFT);
        var str = formatTimestamp(activity.getRecords().get((int) (graph_step * pos)).getTimestamp());
        context2.fillText(str,x+2,2+ font.getSize());
        var dist = activity.getRecords().get((int) (graph_step * pos)).getDistance()/1000;
        context2.fillText("Distance: " + Math.round(dist) + " km",x+2,layer_2.getLayoutY()+20 + 2 + font.getSize());
        context2.fillText("Elev: " + activity.getRecords().get((int) (graph_step * pos)).getAltitude() + " m",x+2,layer_2.getLayoutY()+20 + 2 + (font.getSize() * 2));
        context2.fillText("Grade: " + activity.getRecords().get((int) (graph_step * pos)).getGrade() + " %",x+2,layer_2.getLayoutY()+20 + 2 + (font.getSize() * 3));
        // pointer vertikala
        context2.moveTo(layer_2.getLayoutX() + event.getX(), 0);                       // pozicija pointera
        context2.lineTo(layer_2.getLayoutX() + event.getX(), layer_2.getHeight() - 40);
        context2.setGlobalAlpha(0.6);
        context2.stroke();
        context2.fill();
        context2.closePath();
        context2.setGlobalAlpha(1);

        CyclingPower_Web_Local.map.plot_cursor_marker(activity.getRecords().get((int) (graph_step * pos)).getLatitude(),
                activity.getRecords().get((int) (graph_step * pos)).getLongitude());
//                Console.log("getX: " + ( event.getX() - start_position_x));


        if ((y - layer_2.getLayoutY()) < 20 || y > layer_2.getLayoutY() + layer_2.getHeight() - 20) {
            context2.clearRect(0, 0, layer_2.getWidth(), layer_2.getHeight());
            map.remove_cursor_marker();
        }
    }



    //--------------------------------------------------------------
    public static void handle_slider_distance(Slider slider, Number newValue, Canvas layer_2){
//        Console.log("slider: " + slider.getValue());

        if (activity == null) {
            return;
        }

        var pos = Helper_light.map((long) newValue.doubleValue(),
                (long)(slider.getMin()+0L),
                (long)(slider.getMax()+0L),
                (long) (start_position_x),
                (long) (layer_2.getWidth()));

        double x = layer_2.getLayoutX() + pos + 20; // pozicija pocetka box-a po x
        // right side
        if ((pos + 20 + 105) > layer_2.getWidth()) {
            x = layer_2.getLayoutX() + pos - 20 - 105; // pomeranje pocetka box-a u levo
        }

        context2.clearRect(0, 0, layer_2.getWidth(), layer_2.getHeight()); // brisanje
        context2.beginPath();
        // fill box background
        context2.setFill(Color.rgb(255, 243, 48));
        context2.fillRect(x, layer_2.getLayoutY() + 20, 105, 42);
        context2.setStroke(Color.rgb(0, 0, 0));
        // box boundary lines
        context2.setLineWidth(1);
        context2.setGlobalAlpha(0.6);
        context2.strokeRect(x, layer_2.getLayoutY() + 20, 105, 42);
        context2.setGlobalAlpha(1);
        // text u box-u
//            Console.log(slider.getMax());

        // important - ako je ispod 1 dobijamo exeption - brisemo levo
        if((graph_step * (pos-start_position_x)) < 1.0){
            context2.clearRect(0, 0, layer_2.getWidth(), layer_2.getHeight());
            return;
        }
// TODO samo pos umesto (pos-start_position_x) daje tacan raspon na mapi ali ispis ne odgovara
        try{
            context2.setFill(Color.rgb(0, 0, 0));
            Font font = Font.font("sans-serif", FontWeight.SEMI_BOLD, FontPosture.REGULAR, 12);
            context2.setFont(font);
            context2.setTextAlign(TextAlignment.LEFT);
            var str = formatTimestamp(activity.getRecords().get((int) (graph_step * (pos-start_position_x)) - 1).getTimestamp());
            context2.fillText(str, x + 2, 2 + font.getSize());
            var dist = activity.getRecords().get((int) (graph_step * (pos-start_position_x))).getDistance()/1000;
            context2.fillText("Distance: " + Math.round(dist) + " km",x+2,layer_2.getLayoutY()+20 + 2 + font.getSize());
            context2.fillText("Elev: " + activity.getRecords().get((int) (graph_step * (pos-start_position_x)) - 1).getAltitude() + " m", x + 2, layer_2.getLayoutY() + 20 + 2 + (font.getSize() * 2));
            context2.fillText("Grade: " + activity.getRecords().get((int) (graph_step * (pos-start_position_x)) - 1).getGrade() + " %", x + 2, layer_2.getLayoutY() + 20 + 2 + (font.getSize() * 3));
            // pointer vertikala
            context2.setGlobalAlpha(0.6);
            context2.moveTo(pos, 0);                       // pozicija pointera
            context2.lineTo(pos, layer_2.getHeight() - 40);
            context2.stroke();
            context2.closePath();
            context2.setGlobalAlpha(1);
        }catch (Exception e){
            Console.log("Exception: " + e.getMessage());
            Console.log("GraphDistance value: " + ((graph_step * (pos-start_position_x))));
            context2.clearRect(0, 0, layer_2.getWidth(), layer_2.getHeight());
            return;
        }

        // brisemo desno
        if (pos >= layer_2.getLayoutX() + layer_2.getWidth() ) {
//            Console.log("newValue.intValue(): " + newValue.intValue() + ", pos: " + pos);
            context2.clearRect(0, 0, layer_2.getWidth(), layer_2.getHeight());
        }

        pos = Helper_light.map((long) newValue.doubleValue(),
                (long)(slider.getMin()+0L),
                (long)(slider.getMax()+0L),
                (long) (0L),
                (long) (layer_2.getWidth()));

        CyclingPower_Web_Local.map.plot_cursor_marker(activity.getRecords().get((int) (graph_step * (pos-start_position_x/2)) - 1).getLatitude(),
                activity.getRecords().get((int) (graph_step * (pos-start_position_x/2))).getLongitude());
//                Console.log("getX: " + ( event.getX() - start_position_x));
    }
}
