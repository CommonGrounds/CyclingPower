package dev.java4now.web.maps;

import dev.java4now.web.http.IPCodes;
import dev.java4now.web.model.CyclingActivity;
import dev.webfx.platform.console.Console;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/*
In WebFX (browser), WebView isn’t natively supported, but WebFX emulates it by mapping it to a <div> (via webfx-htmlTag).
The htmlContent_multi_zoom becomes the inner HTML of that <div>, and webEngine.executeScript calls are translated to JavaScript executions in the browser DOM.
GWT Compilation:
Your code compiles with GWT because WebView and WebEngine are part of WebFX’s supported JavaFX subset.
WebFX’s runtime (e.g., webfx-platform) likely shims these APIs to work with the browser’s native DOM and JavaScript engine.
The webfx-htmlTag property ensures the WebView is treated as a <div> in the compiled output, aligning with your #map styling.
 */

public class Leaflet {

    public WebView webView;
    private WebEngine webEngine;

    public static final StringProperty zoom_txt = new SimpleStringProperty("0");

    String htmlContent_basic = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "  <head>\n" +
            "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.css\" />\n" +
            "    <style>\n" +
            "      #map { height: 100vh; width: 100vw; }\n" +
            "    </style>\n" +
            "  </head>\n" +
            "  <body>\n" +
            "    <div id=\"map\"></div>\n" +
            "    <script src=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.js\"></script>\n" +
            "    <script>\n" +
            "      // Configure Leaflet icons\n" +
            "      L.Icon.Default.mergeOptions({\n" +
            "          iconUrl: 'marker-icon.png',\n" +                             // default - net
            "          shadowUrl: 'marker-shadow.png'\n" +
            "      });\n" +
            "\n" +
            "      var map = L.map('map').setView([51.505, -0.09], 13);\n" +         // svg , 13 zoom level and center
            "      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
            "        attribution: '&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors'\n" +
            "      }).addTo(map);\n" +
            "    </script>\n" +
            "  </body>\n" +
            "</html>";

    // zoom label u okviru top - right control layer-a
    String htmlContent_multi = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "  <head>\n" +
            "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.css\" />\n" +
            "    <style>\n" +
            "      #map { height: 95vh; width: 100%; }\n" +        // #map { height: 100vh; width: 100vw; } - pun ekran
            "      .leaflet-top-center-container {\n" +
            "        position: absolute;\n" +
            "        top: 10px;\n" +
            "        text-align: center;\n" +
            "        right: 50%;\n" +
            "        transform: translateX(-100%);\n" +
            "        pointer-events: none;\n" + /* Allow map interactions through the control */
            "        z-index: 1000;\n" +
            "      }\n" +
            "      .leaflet-top-center-gauge {\n" +
            "        padding: 5px 15px;\n" +
            "        border-radius: 4px;\n" +
            "        border: 1px solid #ccc;\n" +
            "        pointer-events: auto;\n" +   /* Re-enable interactions for the gauge */
            "      }\n" +
            "    </style>\n" +
            "  </head>\n" +
            "  <body>\n" +
            "    <div id=\"map\"></div>\n" +
            "    <script src=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.js\"></script>\n" +
            "    <script>\n" +
            "      // Configure Leaflet icons\n" +
            "      L.Icon.Default.mergeOptions({\n" +
            "          iconUrl: 'marker-icon.png',\n" +                             // default - net
            "          shadowUrl: 'marker-shadow.png'\n" +
            "      });\n" +
            "\n" +
            "      var map = L.map('map',{renderer: L.canvas()}).setView([51.505, -0.09], 13);\n" +         // canvas , 13 zoom level and center

            "      const osm = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +    // posto je addTo(map) - bice default mapa
            "      attribution: '&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors'}).addTo(map);\n" +

            "      const openTopoMap = L.tileLayer('https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png', {maxZoom: 15,attribution: 'Map data: &copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors, <a href=\"http://viewfinderpanoramas.org\">SRTM</a> | Map style: &copy; <a href=\"https://opentopomap.org\">OpenTopoMap</a> (<a href=\"https://creativecommons.org/licenses/by-sa/3.0/\">CC-BY-SA</a>)'});\n" +

            "      const Esri_WorldImagery = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {\n" +
            "      attribution: 'Tiles &copy; Esri &mdash; Source: Esri, i-cubed, USDA, USGS, AEX, GeoEye, Getmapping, Aerogrid, IGN, IGP, UPR-EGP, and the GIS User Community'})\n" +

            "      const layerControl = L.control.layers().addTo(map);\n" +
            "      layerControl.addBaseLayer(osm, 'OpenStreetMap');\n" +
            "      layerControl.addBaseLayer(openTopoMap, 'OpenTopoMap');\n" +
            "      layerControl.addBaseLayer(Esri_WorldImagery, 'Satellite');\n" +

            "      const ZoomViewer = L.Control.extend({ onAdd() {const container = L.DomUtil.create('div', 'leaflet-top-center-container');\n" +
            "      const gauge = L.DomUtil.create('div', 'leaflet-top-center-gauge'); container.style.width = '130px';\n" +
            "      container.style.background = 'rgba(255,255,255,0.7)';gauge.style.fontFamily = 'Arial';gauge.style.fontSize = '14px';\n" +
            "      map.on('zoomstart zoom zoomend', (ev) => { gauge.innerHTML = `Zoom level: ${map.getZoom()}`;});\n" +
            "      container.appendChild(gauge); return container;}});\n" +

            "      const zoomViewerControl = (new ZoomViewer()).addTo(map);\n" +

            "      L.control.scale({imperial:false}).addTo(map);\n" +  // po defaultu metric + imperial - bottom left

            "    </script>\n" +
            "  </body>\n" +
            "</html>";

    // zoom label direktno na mapi ( center ) - osm mapa eng. names
    String htmlContent_multi_zoom = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "  <head>\n" +
            "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.css\" />\n" +
            "    <style>\n" +
            "      #map { height: 95vh; width: 100%; }\n" +        // #map { height: 100vh; width: 100vw; } - pun ekran
            "      .leaflet-top-center-gauge {\n" +
            "        position: absolute;\n" +
            "        top: 10px;\n" +
            "        left: 50%;\n" +
            "        transform: translateX(-50%);\n" +
            "        background: rgba(255,255,255,0.7);\n" +
            "        padding: 5px 15px;\n" +
            "        border-radius: 4px;\n" +
            "        z-index: 500;\n" +          // iznad mape a ispod control layer - menu items ( 1000 - iznad svega )
            "        font-family: Arial;\n" +
            "        font-size: 14px;\n" +
            "      }\n" +
            "      .leaflet-top-center-gauge {\n" +
            "        padding: 5px 15px;\n" +
            "        border-radius: 4px;\n" +
            "        border: 1px solid #ccc;\n" +
            "        pointer-events: auto;\n" +   /* Re-enable interactions for the gauge */
            "      }\n" +
            "    </style>\n" +
            "  </head>\n" +
            "  <body>\n" +
            "    <div id=\"map\"></div>\n" +
            "    <script src=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.js\"></script>\n" +
            "    <script>\n" +
            "      // Configure Leaflet icons\n" +
            "      L.Icon.Default.mergeOptions({\n" +
            "          iconUrl: 'marker-icon.png',\n" +                             // default - net
            "          shadowUrl: 'marker-shadow.png'\n" +
            "      });\n" +
            "\n" +
            "      var map = L.map('map',{renderer: L.canvas()}).setView([44.8711, 20.6412], 13);\n" +         // canvas , 13 zoom level and set center (setView)

            "      const osm = L.tileLayer('https://tiles.stadiamaps.com/tiles/osm_bright/{z}/{x}/{y}{r}.png', {maxZoom: 20,\n" +    // posto je addTo(map) - bice default mapa
            "      attribution: '&copy; <a href=\"https://www.stadiamaps.com/\" target=\"_blank\">Stadia Maps</a> &copy; <a href=\"https://openmaptiles.org/\" target=\"_blank\">OpenMapTiles</a> &copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors'}).addTo(map);\n" +

            "      const openTopoMap = L.tileLayer('https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png', {maxZoom: 15,attribution: 'Map data: &copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors, <a href=\"http://viewfinderpanoramas.org\">SRTM</a> | Map style: &copy; <a href=\"https://opentopomap.org\">OpenTopoMap</a> (<a href=\"https://creativecommons.org/licenses/by-sa/3.0/\">CC-BY-SA</a>)'});\n" +

            // Esri Satellite
            "      const esriSatellite = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {maxZoom: 18,\n" +
            "        attribution: 'Tiles &copy; Esri &mdash; Source: Esri, i-cubed, USDA, USGS, AEX, GeoEye, Getmapping, Aerogrid, IGN, IGP, UPR-EGP, and the GIS User Community'\n" +
            "      });\n" +

            // CartoDB Positron Labels (light labels that work well on satellite)
            "      const positronLabels = L.tileLayer('https://{s}.basemaps.cartocdn.com/light_only_labels/{z}/{x}/{y}{r}.png', {\n" +
            "        attribution: '&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors &copy; <a href=\"https://carto.com/attributions\">CARTO</a>',\n" +
            "        subdomains: 'abcd',\n" +
            "        maxZoom: 18,\n" +
            "        opacity: 1.0\n" +
            "      });\n" +

            "      const satelliteWithLabels = L.layerGroup([esriSatellite, positronLabels]);\n" +

            "      const layerControl = L.control.layers({\n" +
            "        'OpenStreetMap': osm,\n" +
            "        'OpenTopoMap': openTopoMap,\n" +
            "        'Satellite with Labels': satelliteWithLabels,\n" +
            "      }).addTo(map);\n" +

            // Set satellite with labels as default
//            "      satelliteWithLabels.addTo(map);\n" +
/*
            "      // Debug: Check if layers are loading\n" +
            "      esriSatellite.on('load', () => console.log('Esri satellite loaded'));\n" +
            "      esriSatellite.on('tileerror', (e) => console.log('Esri error:', e));\n" +
            "      positronLabels.on('load', () => console.log('Positron labels loaded'));\n" +
            "      positronLabels.on('tileerror', (e) => console.log('Positron error:', e));\n" +
*/
            "      // Dodajte gauge direktno na mapu\n" +
            "      const gaugeContainer = L.DomUtil.create('div', 'leaflet-top-center-gauge', map.getContainer());\n" +
            "      map.on('zoomstart zoom zoomend', () => {gaugeContainer.innerHTML = `Zoom level: ${map.getZoom()}`;});\n" +
            "      gaugeContainer.innerHTML = `Zoom level: ${map.getZoom()}`;\n" +

            "      L.control.scale({imperial:false}).addTo(map);\n" +  // po defaultu metric + imperial - bottom left

            "    </script>\n" +
            "  </body>\n" +
            "</html>";

    public Leaflet() {
        webView = new WebView();
//        webView.getStyleClass().add("#map { height: 280px; }");
        webView.setId("map");
        webView.getProperties().put("webfx-htmlTag", "div");
//        webEngine.setUserStyleSheetLocation("#map { height: 180px; }");
        webEngine = webView.getEngine();

//        webEngine.loadContent(htmlContent_basic);
//        webEngine.loadContent(htmlContent_multi);
        webEngine.loadContent(htmlContent_multi_zoom);

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.READY) {
//                dev.java4now.maps.WebFX_Maps.map_loaded.set(true);
                // Page is fully loaded, now you can safely execute JavaScript
                //               webEngine.executeScript("var map = L.map('map').setView([51.505, -0.09], 13);");
//                webEngine.executeScript("L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { attribution: '&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors' }).addTo(map);");
                Console.log("Page loaded successfully!");
                webEngine.executeScript("console.log('Page loaded successfully!');");
            }
        });
    }


    public void plot_route(CyclingActivity activity) {

//        Console.log("Record Size: " + activity.getRecords().size());

        StringBuilder script = new StringBuilder();
        script.append("map.setView([")
                .append(activity.getRecords().get(0).getLatitude()).append(",")
                .append(activity.getRecords().get(0).getLongitude()).append("], 13);\n");

        script.append("var marker = L.marker([")
                .append(activity.getRecords().get(0).getLatitude()).append(",")
                .append(activity.getRecords().get(0).getLongitude()).append("]).addTo(map)\n"  +
                "  const el = marker.getElement();\n" +
                "  if (el != undefined) {\n" +
                "  el.style.filter = 'hue-rotate(240deg)';\n" +  // 120 red, 240 green ( pocetna je blue - pogledati wheel ( WebFX_Maps_2 example ))
                "}\n" +
                "marker.bindPopup('Start!');" );
        /*
        script.append("var cursor = L.circle([")
                .append(activity.getRecords().get(0).getLatitude())
                .append(",")
                .append(activity.getRecords().get(0).getLongitude())
                .append("], {color: 'red', fillColor: '#f03', fillOpacity: 0.5, radius: 8});");
         */
        script.append("var cursor = L.circleMarker([")
                .append(activity.getRecords().get(0).getLatitude())
                .append(",")
                .append(activity.getRecords().get(0).getLongitude())
                .append("], {")
                .append("color: 'red',")
                .append("fillColor: '#f03',")
                .append("fillOpacity: 0.5,")
                .append("radius: 8});");        // Radius in pixels, not meters

        // Plot route using Lat/Lon from records
        script.append("var latlngs = [");
        for (int i = 0; i < activity.getRecords().size(); i++) {
            CyclingActivity.RecordData record = activity.getRecords().get(i);
            script.append("[").append(record.getLatitude()).append(",").append(record.getLongitude()).append("]");
            if (i < activity.getRecords().size() - 1) script.append(",");
        }
        script.append("];\n");
        script.append("var lines = L.polyline(latlngs, {color: 'blue'}).addTo(map);\n");

        script.append("var marker2 = L.marker([")
                .append(activity.getRecords().get(activity.getRecords().size() - 1).getLatitude()).append(",")
                .append(activity.getRecords().get(activity.getRecords().size() - 1).getLongitude()).append("]).addTo(map)\n"  +
                        "  const el2 = marker2.getElement();\n" +
                        "  if (el2 != undefined) {\n" +
                        "  el2.style.filter = 'hue-rotate(120deg)';\n" +  // 120 red, 240 green ( pocetna je blue - pogledati wheel ( WebFX_Maps_2 example ) )
                        "}\n" +
                        "marker2.bindPopup('Finnish!');" );

        // Optionally, add markers or other Leaflet features
        script.append("map.fitBounds(latlngs);\n");

        draw_route(script.toString());

    }


    //---------------------------------------------------
    public void set_map_ip_position(double lat, double lon) {
        if(IPCodes.RUN_PLOTTING.get()){
            return;
        }
        String checkScript =
                "if (typeof map !== 'undefined' && map !== null) {" +
                        "   map.setView([" + lat + "," + lon + "], 12);" +
                        "   'success';" +
                        "} else {" +
                        "   'map not ready';" +
                        "}";

        Object result = webEngine.executeScript(checkScript);
        Console.log("IP Location - Result: " + result);
    }


    //---------------------------------------------------
    public void plot_cursor_marker(double lat, double lon){
        StringBuilder script = new StringBuilder();
        /*
        script.append("cursor.remove();");
        script.append("cursor = L.circle([")  //  circle - radius in meters - zoom change size
                .append(lat)
                .append(",")
                .append(lon)
                .append("], {color: 'red', fillColor: '#f03', fillOpacity: 0.5, radius: 45}).addTo(map);");  // map.getZoom()*2
         */
        script.append("cursor.remove();");
        script.append("cursor = L.circleMarker([") // circleMarker - radius in px - same on every zoom
                .append(lat)
                .append(",")
                .append(lon)
                .append("], {color: 'red', fillColor: '#f03', fillOpacity: 0.5, radius: 8}).addTo(map);");

        draw_cursor_marker(script.toString());
    }




    //---------------------------------------------------
    private void draw_route(String script) {
        IPCodes.RUN_PLOTTING.set(true);
//        Console.log("script: " + script);
        webEngine.executeScript(script);
        webEngine.executeScript("console.log('Route: Done');");
    }



    //---------------------------------------------------
    private void draw_cursor_marker(String script) {
//        Console.log("script: " + script);
        webEngine.executeScript(script);
//        webEngine.executeScript("console.log('Activity: Done');");
    }



    //---------------------------------------------------
    public void remove_cursor_marker(){
        webEngine.executeScript("cursor.remove();");
    }


    //----------------------------------------------------
    public void delete_plot(){
        webEngine.executeScript("if (cursor != null) {cursor.remove()};");
        webEngine.executeScript("if (marker != null) {marker.remove()};");
        webEngine.executeScript("if (marker2 != null) {marker2.remove()};");
        webEngine.executeScript("if (lines != null) {lines.remove()};");
    }
}
