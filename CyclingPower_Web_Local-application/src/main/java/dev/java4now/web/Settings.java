package dev.java4now.web;

import dev.java4now.web.model.futures_fetcher;
import dev.webfx.platform.ast.AST;
import dev.webfx.platform.ast.AstObject;
import dev.webfx.platform.ast.ReadOnlyAstObject;
import dev.webfx.platform.ast.json.Json;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.storage.LocalStorage;
import dev.webfx.platform.util.Strings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import service.Service_impl;

import java.time.Month;
import java.util.Locale;
import java.util.Map;

public class Settings {

    public static final boolean AUTO_LOAD = true;
    public static final boolean SHOW_COFFEE = false;
    public static final StringProperty name_txt = new SimpleStringProperty(null); // Example UI element
    public static final StringProperty email_txt = new SimpleStringProperty(null); // Example UI element
    public static final StringProperty pass_txt = new SimpleStringProperty(null); // Example UI element

    //---------------------------------------
    public static void loadState() {
        name_txt.set(Strings.asString(LocalStorage.getItem("user_name")));
        email_txt.set(Strings.asString(LocalStorage.getItem("user_email")));
        pass_txt.set(Strings.asString(LocalStorage.getItem("user_pass")));
        /*
        // User klasa - probni kod
        User user = new User(name_txt.get(), pass_txt.get(), email_txt.get());
        // User to json using UserSerialCodec
        AstObject ast = AST.createObject();
        UserSerialCodec codec = new UserSerialCodec(); // eksplicitno registrovan za kompleksnije slučajeve
        SerialCodecManager.registerSerialCodec(codec);
        codec.encode(user,ast);
        String jsonText = Json.formatAny(ast);
        Console.log("Obj_to_json-Using custom codec: " + jsonText);
         */

        /*
        // City klasa - probni kod - nema je ovde
        // Serijalizacija
        City city = new City();
        city.setCountrycode("RS");
        city.setCity("Belgrade");
        city.setLatitude(44.8125);
        city.setLongitude(20.4612);
        city.setPopulation(1378682L);

        AstObject ast = AST.createObject();
        CitySerialCodec codec = new CitySerialCodec();
        SerialCodecManager.registerSerialCodec(codec);
        codec.encode(city, ast);
        String json = Json.formatAny(ast);
        Console.log("Obj_to_json-Using custom codec: " + json);

// Deserijalizacija
        ReadOnlyAstObject astObject = Json.parseObject(json);
        City deserializedCity = codec.decode(astObject);
        Console.log("json_to_Obj-Using custom codec: " + deserializedCity.toString());
         */
    }


    //---------------------------------------------
    public static void saveState(String name,String email, String pass) {
        LocalStorage.setItem("user_name", name.toLowerCase(Locale.ROOT));
        LocalStorage.setItem("user_email", email);
        LocalStorage.setItem("user_pass", pass);
    }


    //-------------------------------------------------
    public static void getScreenSize(){
        CyclingPower_Web_Local.screen_width = Service_impl.getWindowWidth();
        CyclingPower_Web_Local.screen_height = Service_impl.getWindowHeight();
//        Console.log("Width: " + CyclingPower_Web.screen_width + ", Height: " + CyclingPower_Web.screen_height);
    }



    // Save activity_per_month to LocalStorage
    //--------------------------------------------------------------
    public static void saveActivityPerMonthToStorage() {
        try {
            AstObject jsonObject = AST.createObject();
            for (Map.Entry<Month, Integer> entry : futures_fetcher.activity_per_month.entrySet()) {
                jsonObject.set(entry.getKey().name(), entry.getValue());
            }

            String jsonString = Json.formatAny(jsonObject);

            // Log the map contents for debugging
            Console.log("Saving activity_per_month: " + futures_fetcher.activity_per_month.toString());
            // Store in LocalStorage
            LocalStorage.setItem("activity_per_month", jsonString);
            Console.log("Saved activity_per_month to LocalStorage: " + jsonString);
        } catch (Exception e) {
            Console.log("Failed to save activity_per_month to LocalStorage: " + e.getMessage());
        }
    }



    // Load activity_per_month from LocalStorage - 1. put se poziva inicijacijom chart klase ( Static )
    //----------------------------------------------------------
    public static void loadActivityPerMonthFromStorage() {
        String jsonString = LocalStorage.getItem("activity_per_month");
        if (jsonString != null && !jsonString.isEmpty()) {
            try {
                ReadOnlyAstObject jsonObject = Json.parseObject(jsonString);
                futures_fetcher.activity_per_month.clear(); // Clear existing map
                for (Month month : Month.values()) {
                    Integer count = jsonObject.getInteger(month.name());
                    futures_fetcher.activity_per_month.put(month, count != null ? count : 0);
                }
                Console.log("Loaded activity_per_month from LocalStorage: " + jsonString);
            } catch (Exception e) {
                Console.log("Failed to parse activity_per_month from LocalStorage: " + e.getMessage());
                // Initialize with zeros if parsing fails
                futures_fetcher.activity_per_month.clear();
                for (Month month : Month.values()) {
                    futures_fetcher.activity_per_month.put(month, 0);
                }
            }
        } else {
            Console.log("No activity_per_month found in LocalStorage, initializing with zeros");
            // Initialize with zeros if nothing is stored
            futures_fetcher.activity_per_month.clear();
            for (Month month : Month.values()) {
                futures_fetcher.activity_per_month.put(month, 0);
            }
        }
    }
}
