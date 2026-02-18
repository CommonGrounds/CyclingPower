package dev.java4now.web.http;

import dev.java4now.web.CyclingPower_Web_Local;
import dev.webfx.platform.ast.json.Json;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.fetch.Fetch;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Duration;

public class IPCodes {

    public static final BooleanProperty RUN_PLOTTING = new SimpleBooleanProperty(false);
//{"status":"success","country":"Serbia","countryCode":"RS","region":"00","regionName":"Belgrade","city":"Belgrade","zip":"","lat":44.8046,"lon":20.4637,
// "timezone":"Europe/Belgrade","isp":"A1 Srbija d.o.o","org":"A1 Serbia","as":"AS44143 A1 Srbija d.o.o","query":"77.243.24.142"}
    private static String error, reason, country, countryCode, city;
    private static double lat,lon;

    private static boolean parseData(String codes) {
        error = Json.parseObject(codes).getString("fail");
        if(error != null){
            Console.log("error: " + error );
            reason = Json.parseObject(codes).getString("message");
            Console.log("error reason: " + reason );
            return false;
        }

        lat = Json.parseObject(codes).getDouble("latitude");
        lon = Json.parseObject(codes).getDouble("longitude");
        country  = Json.parseObject(codes).getString("country_name");
        countryCode  = Json.parseObject(codes).getString("country_code");
        city = Json.parseObject(codes).getString("city");
//        title_icon.set("icon-24px/" + countryCode   + ".png");
//        Console.log("JsonObject Success: (" + lon + " )\n" + "(" + lat + " )\n" + "(" + country + " )\n" + "(" + countryCode + " )\n" + "(" + city + " )");
        return true;
    }



    //---------------------------------------
    public static void getIPCodes() {
        // https://ipapi.co - 30000 monthly free - have ssl
        String IP_API_url = "https://ipapi.co/json/";

        Fetch.fetch(IP_API_url)
                .onFailure(error -> {
                    Console.log("Fetch IP_API failure: " + error);
                })
                .onSuccess(response -> {
//                    Console.log("Fetch IP_API success: ok = " + response.ok());
                    response.text()
                            .onFailure(error -> Console.log("Json IP_API failure: " + error))
                            .onSuccess(text -> {
//                                Console.log(text);
                                boolean ok = parseData(text);
                                if (ok) {
                                    // TODO Run leaflet set location
                                    Platform.runLater(() -> {
                                        Timeline pause = new Timeline(new KeyFrame(Duration.seconds(1), evt -> {
//                                        Console.log("parse IP location OK");
                                            CyclingPower_Web_Local.map.set_map_ip_position(lat,lon);
                                        }));
                                        pause.play(); // only play once after 1 second
                                    });
                                }
// u listeneru preko Platform.runLater() jer je Fetch non FX process thread ( vazi samo za javafx, Java Script radi i bez toga a moze i ovako )
//                                dataLoaded.setValue(true);
                            });
                });
    }
}
