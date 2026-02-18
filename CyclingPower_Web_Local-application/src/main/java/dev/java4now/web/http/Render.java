package dev.java4now.web.http;

import dev.webfx.platform.console.Console;
import dev.webfx.platform.fetch.Fetch;
import dev.webfx.platform.fetch.FetchOptions;
import dev.webfx.platform.fetch.Headers;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Render {
    private static final String RENDER_API_URL = "https://api.render.com/v1/services/srv-cvs5inuuk2gs739mthqg/restart"; // Replace with your actual service ID
    // IMPORTANT - Manual Deploy Variant: Change the URL to /deploys?clearCache=true for a full redeploy instead of just restart.
    private static final String API_KEY = "rnd_itxrWpmMpJDLjhj2CU4i3LkwqHjf"; // Load securely (e.g., from config, env var, or prompt user)
    public static final StringProperty response_txt = new SimpleStringProperty("---"); // Optional: Bind to UI for feedback

    public static void triggerRestart() {
        // Create Headers
        Headers headers = Headers.create();
        headers.set("Authorization", "Bearer " + API_KEY);
        headers.set("Content-Type", "application/json"); // Optional but recommended

        // Set up request options
        FetchOptions options = new FetchOptions();
        options.setMethod("POST");
        options.setHeaders(headers);
        options.setBody(""); // Empty body for restart

        // Perform the fetch request
        Fetch.fetch(RENDER_API_URL, options)
                .onSuccess(response -> {
                    if (response.ok()) {
                        Console.log("Restart triggered successfully!");
                        response_txt.set("Success: Render service restarting...");
                    } else {
                        String errorMsg = "Error triggering restart: " + response.status() + " - " + response.statusText();
                        Console.logNative(errorMsg);
                        response_txt.set("Error: " + response.status() + " - " + response.statusText());
                    }
                })
                .onFailure(error -> {
                    String errorMsg = "Network error: " + error.getMessage();
                    Console.logNative(errorMsg);
                    response_txt.set("Error: " + error.getMessage());
                });
    }

}