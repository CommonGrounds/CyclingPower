package dev.java4now.web.websocket;

import dev.java4now.web.CyclingPower_Web_Local;
import dev.webfx.platform.ast.ReadOnlyAstObject;
import dev.webfx.platform.console.Console;
import dev.webfx.stack.com.websocket.WebSocket;
import dev.webfx.stack.com.websocket.WebSocketListener;
import dev.webfx.stack.com.websocket.WebSocketService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

import static dev.java4now.web.CyclingPower_Web_Local.BASE_URL;


public class WebSocketClient {

    static boolean CONNECTION_ERROR = false;
    private static Timeline timeline;

    public static void startWebSocket(String url) {

        ReadOnlyAstObject options = dev.webfx.platform.ast.AST.createObject();
//                .set("protocol", "protocolOne")
        WebSocket webSocket = WebSocketService.createWebSocket(url, options);

        webSocket.setListener(new WebSocketListener() {
            @Override
            public void onOpen() {
                Console.log("WebSocket connection opened.");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //WebFXPart_Orig.lbl_message.set("WebSocket connection opened");
                        CyclingPower_Web_Local.server_txt.set("Server OK ✅");
                    }
                });
                webSocket.send("Hello from WebFX!");
            }

            @Override
            public void onMessage(String message) {
                Console.log("Received message: " + message);
                webSocket.send("Client updated");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
//                        CyclingPower_Web.url.set("http://localhost:8880/api/download-json/" + message);
                        CyclingPower_Web_Local.url.set(BASE_URL + "/api/download-json/" + message);
                        CyclingPower_Web_Local.server_txt.set(message + " \uD83C\uDF10");
                        CyclingPower_Web_Local.data_is_ready.set(true);
                    }
                });
            }

            @Override
            public void onError(String error) {
                Console.log("WebSocket error: " + error);
                CONNECTION_ERROR = true;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //WebFXPart_Orig.lbl_message.set("WebSocket грешка: " + error);
                        CyclingPower_Web_Local.server_txt.set(error + " ❗");
                    }
                });
            }

            @Override
            public void onClose(ReadOnlyAstObject reason) {
//                boolean local_conn_error = CONNECTION_ERROR;  // zbog debug citljivosti variable
                // Bezbedno rukovanje reason objektom koji može biti null
                String closeCode = "unknown";
                if (reason != null) {
                    try {
                        closeCode = reason.getString("code");
                        Console.log("WebSocket connection closed - Reason: " + getCloseCodeDescription(closeCode));
                        if (closeCode == null) {
                            closeCode = "no-code";
                        }
                    } catch (Exception e) {
                        closeCode = "error-getting-code";
                    }
                }
                String finalCloseCode = closeCode;

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
//                      CyclingPower_Web_Local.server_txt.set("connection error" + " ❗");
                        handleCloseScenario(finalCloseCode);
                    }
                });
            }
        });
    }


    private static String getCloseCodeDescription(String code) {
        if (code == null) return "Null code";

        switch (code) {
            case "1000":
                return "Normal closure";
            case "1001":
                return "Endpoint going away";
            case "1002":
                return "Protocol error";
            case "1003":
                return "Unsupported data";
            case "1005":
                return "No status received";
            case "1006":
                return "Abnormal closure";
            case "1007":
                return "Invalid frame payload data";
            case "1008":
                return "Policy violation";
            case "1009":
                return "Message too big";
            case "1010":
                return "Missing extension";
            case "1011":
                return "Internal error";
            case "1012":
                return "Service restart";
            case "1013":
                return "Try again later";
            case "1015":
                return "TLS handshake failed";
            default:
                if (code.startsWith("3")) return "Library-specific error";
                if (code.startsWith("4")) return "Application-specific error";
                return "Unknown code";
        }
    }


    private static void handleCloseScenario(String code) {

        Platform.runLater(() -> {
            if ("1006".equals(code) && CONNECTION_ERROR) {
                // Pri pokretanju se na Firefoxu trigeruje onError i onClose posle 30 sec - pa produzavamo do 3 min
                timeline = new Timeline(new KeyFrame(Duration.minutes(3), evt -> {
                    // Network greške, connection refused, itd.
                    CyclingPower_Web_Local.server_txt.set("Connection error ❗");
                    Console.log("Connection error");
                }));
                timeline.play();
                CyclingPower_Web_Local.server_txt.set("Server waking up ⏰");
                CONNECTION_ERROR = false;
            } else if ("1000".equals(code)) {
                // Normalno zatvaranje
                CyclingPower_Web_Local.server_txt.set("Disconnected ⚡");
            } else if ("1001".equals(code)) {
                // Klient napušta (browser tab zatvoren)
                CyclingPower_Web_Local.server_txt.set("Client disconnected 🏃");
            } else if ("1011".equals(code)) {
                // Server greška
                CyclingPower_Web_Local.server_txt.set("Server error 🔧");
            } else if ("1008".equals(code)) {
                // Policy violation (možda auth greška)
                CyclingPower_Web_Local.server_txt.set("Policy violation 🚫");
            } else {
                // Ostali slučajevi
                CyclingPower_Web_Local.server_txt.set("Connection pause \uD83D\uDCA4");
            }
        });
    }


//--------------------------------------------
    public static void stopCounting() {
        if (timeline != null) {
            timeline.stop();
            Console.log("Stop counting");
        }
    }
}
