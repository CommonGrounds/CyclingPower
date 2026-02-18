package dev.java4now.web.http;

import dev.webfx.platform.console.Console;
import dev.webfx.platform.fetch.Fetch;
import dev.webfx.platform.fetch.FetchOptions;
import dev.webfx.platform.fetch.Headers;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SaveUser {
//    private static final String SERVER_URL = "http://localhost:8880/api/endpoint";
    private static final String SERVER_URL = "https://cyclingpower-server-1.onrender.com/api/endpoint";
    public static final StringProperty response_txt = new SimpleStringProperty("---"); // Example UI element

    public static void saveUserData(String jsonData) {
        // Create Headers
        Headers headers = Headers.create();
        headers.set("Content-Type", "application/json");

        // Set up request options
        FetchOptions options = new FetchOptions();
        options.setMethod("POST");
        options.setHeaders(headers);
        options.setBody(jsonData);

        // Perform the fetch request
        Fetch.fetch(SERVER_URL, options)
                .onSuccess(response -> {
                    if (response.ok()) {
                        Console.log("JSON sent successfully!");
                        response_txt.set("Success!");
                    } else {
                        String errorMsg = "Error sending JSON: " + response.status() + " - " + response.statusText();
                        Console.logNative(errorMsg);
                        response_txt.set("Response: Error: " + response.status() + " - " + response.statusText());
                    }
                })
                .onFailure(error -> {
                    String errorMsg = "Error: " + error.getMessage();
                    Console.logNative(errorMsg);
                    response_txt.set("Response: Error - " + error.getMessage());
                });
    }
}