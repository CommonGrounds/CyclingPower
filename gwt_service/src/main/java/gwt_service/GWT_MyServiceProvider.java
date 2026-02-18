package gwt_service;

import dev.webfx.kit.mapper.peers.javafxgraphics.elemental2.util.HtmlFonts;
import dev.webfx.kit.mapper.peers.javafxgraphics.elemental2.util.HtmlUtil;
import dev.webfx.platform.console.Console;
import elemental2.dom.Blob;
import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

import elemental2.dom.Image;  // JS Image for loading

import service.ServiceProvider;
import service.City;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class GWT_MyServiceProvider implements ServiceProvider {

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "URL")
    private static class NativeURL {
        public static native String createObjectURL(Blob blob);
    }

    public String createObjectURL(Blob blob) {
        return NativeURL.createObjectURL((Blob) blob);
    }

    private static native boolean isDarkMode() /*-{
    var mq = window.matchMedia('(prefers-color-scheme: dark)');

    // Moderni browseri + normalno ponašanje
    if (mq.media !== 'not all') {
        return mq.matches;
    }

    // Vrlo stari browseri ili slomljeni slučajevi (retko 2026.)
    // Fallback: pitaj i za light, pa zaključi
    var mqLight = window.matchMedia('(prefers-color-scheme: light)');
    if (mqLight.media !== 'not all') {
        return !mqLight.matches;  // ako nije light → verovatno dark
    }

    // Ako ništa ne radi (npr. stari embedded view ili čudan about:config)
    // Najbolje pretpostaviti LIGHT – većina ljudi očekuje light ako nema signala za dark
    // Alternativa: document.body ili html backgroundColor proveriti, ali to je komplikovano
    return false;
}-*/;

    public boolean isDarkModeEnabled() {
        return isDarkMode();
    }

    public int getWindowWidth() {
        return DomGlobal.window.innerWidth;
    }

    public int getWindowHeight() {
        return DomGlobal.window.innerHeight;
    }

    public double measureText(String str, Canvas canvas) {
        GraphicsContext context1 = canvas.getGraphicsContext2D();
        Font font = context1.getFont();
        HTMLCanvasElement canvasElement = HtmlUtil.createElement("canvas");
        CanvasRenderingContext2D gc = dev.webfx.kit.mapper.peers.javafxgraphics.elemental2.html.Context2DHelper.getCanvasContext2D(canvasElement, true);
        gc.setFont(HtmlFonts.getHtmlFontDefinition(font));
        return gc.measureText(str).width;
    }

    public String toDataURL(Canvas canvas, String type, String objectURL) {
        AtomicReference<Double> originalHeight = new AtomicReference<>((double) 0);
        AtomicReference<Double> originalWidth = new AtomicReference<>((double) 0);

        Image img = new Image();
        img.src = objectURL;

        originalWidth.set((double) img.naturalWidth);
        originalHeight.set((double) img.naturalHeight);
        Console.log("Native image loaded: " + originalWidth + "x" + originalHeight + "px");

        double newHeight = 600;
        double newWidth = originalWidth.get() * (newHeight / originalHeight.get());

        HTMLCanvasElement htmlCanvas = (HTMLCanvasElement) DomGlobal.document.createElement("canvas");
        htmlCanvas.width = (int) Math.round(newWidth);
        htmlCanvas.height = (int) Math.round(newHeight);

        CanvasRenderingContext2D ctx = get2D(htmlCanvas);

        if (ctx == null) {
            Console.log("Failed to get 2D context; fallback to original.");
            return null;
        }

        ctx.drawImage(img, 0, 0, newWidth, newHeight);
        Console.log("Drew image to canvas: " + newWidth + "x" + newHeight + "px");
        return htmlCanvas.toDataURL(type);
    }

    public native HTMLCanvasElement asHtmlCanvas(Canvas canvas) /*-{
        return canvas;
    }-*/;

    private static native CanvasRenderingContext2D get2D(HTMLCanvasElement c) /*-{
        return c.getContext("2d");
    }-*/;

    // ==================== IndexedDB Native Methods ====================

    private static final String DB_NAME = "CitiesDB";
    private static final String STORE_NAME = "cities";
    private static final String CACHE_KEY = "all_cities";
    private static final String CACHE_TIMESTAMP_KEY = "cache_timestamp";
    // Cache validity: 7 dana - koristimo double umesto long
    private static final double CACHE_MAX_AGE_MS = 7.0 * 24 * 60 * 60 * 1000;

    /**
     * Provera cache-a u IndexedDB
     */
    public void checkCache(Consumer<Map<String, List<City>>> onSuccess,
                           Consumer<Throwable> onError) {
        checkCacheNative(
                jsonData -> {
                    try {
                        // Proveri timestamp asinhrono
                        getCacheTimestampNative(timestamp -> {
                            double now = (double) System.currentTimeMillis(); // Konvertuj u double

                            if (now - timestamp > CACHE_MAX_AGE_MS) {
                                Console.log("Cache expired (age: " + ((now - timestamp) / 1000 / 60 / 60) + " hours)");
                                onSuccess.accept(null);
                                return;
                            }

                            Console.log("Retrieved JSON from cache: " + (jsonData != null ? jsonData.length() : "null") + " chars");
                            // Deserijalizuj JSON u Map
                            if (jsonData != null) {
                                Console.log("JSON preview: " + jsonData.substring(0, Math.min(100, jsonData.length())));
                                Map<String, List<City>> cities = deserializeCities(jsonData);
                                Console.log("Cache valid, loaded " + countCities(cities) + " cities");
                                onSuccess.accept(cities);
                            } else {
                                onSuccess.accept(null);
                            }
                        });

                    } catch (Exception e) {
                        Console.log("Cache deserialization error: " + e.getMessage());
                        onSuccess.accept(null);
                    }
                },
                error -> onError.accept(new Exception(error))
        );
    }

    /**
     * Čuvanje u IndexedDB
     */
    public void saveToCache(Map<String, List<City>> cities,
                            Runnable onSuccess,
                            Consumer<Throwable> onError) {
        try {
            String jsonData = serializeCities(cities);
            double timestamp = (double) System.currentTimeMillis();

            Console.log("Saving " + countCities(cities) + " cities to IndexedDB...");

            saveToCacheNative(jsonData, timestamp,
                    () -> {
                        Console.log("Cache saved successfully");
                        Console.log("Serialized JSON length: " + jsonData.length());
                        Console.log("JSON preview: " + jsonData.substring(0, Math.min(100, jsonData.length())));
                        onSuccess.run();
                    },
                    error -> onError.accept(new Exception(error))
            );

        } catch (Exception e) {
            Console.log("Cache serialization error: " + e.getMessage());
            onError.accept(e);
        }
    }

    /**
     * Brisanje cache-a
     */
    public void clearCache(Runnable onSuccess, Consumer<Throwable> onError) {
        clearCacheNative(
                () -> {
                    Console.log("Cache cleared");
                    onSuccess.run();
                },
                error -> onError.accept(new Exception(error))
        );
    }

    // ==================== JSNI Native Methods ====================

    private native void checkCacheNative(Consumer<String> onSuccess,
                                         Consumer<String> onError) /*-{
        var dbName = @gwt_service.GWT_MyServiceProvider::DB_NAME;
        var storeName = @gwt_service.GWT_MyServiceProvider::STORE_NAME;
        var cacheKey = @gwt_service.GWT_MyServiceProvider::CACHE_KEY;

        var request = $wnd.indexedDB.open(dbName, 1);

        request.onerror = function(e) {
            var error = "IndexedDB open error: " + e.target.error;
            onError.@java.util.function.Consumer::accept(*)(error);
        };

        request.onupgradeneeded = function(e) {
            var db = e.target.result;
            if (!db.objectStoreNames.contains(storeName)) {
                db.createObjectStore(storeName);
            }
        };

        request.onsuccess = function(e) {
            var db = e.target.result;

            try {
                var tx = db.transaction([storeName], 'readonly');
                var store = tx.objectStore(storeName);
                var getRequest = store.get(cacheKey);

                getRequest.onsuccess = function(e) {
                    var data = e.target.result;
                    if (data) {
                        onSuccess.@java.util.function.Consumer::accept(*)(data);
                    } else {
                        onSuccess.@java.util.function.Consumer::accept(*)(null);
                    }
                };

                getRequest.onerror = function(e) {
                    onError.@java.util.function.Consumer::accept(*)("Get error");
                };

            } catch (err) {
                onError.@java.util.function.Consumer::accept(*)(err.toString());
            } finally {
                db.close();
            }
        };
    }-*/;

    private native void getCacheTimestampNative(Consumer<Double> onSuccess) /*-{
        var dbName = @gwt_service.GWT_MyServiceProvider::DB_NAME;
        var storeName = @gwt_service.GWT_MyServiceProvider::STORE_NAME;
        var timestampKey = @gwt_service.GWT_MyServiceProvider::CACHE_TIMESTAMP_KEY;

        var request = $wnd.indexedDB.open(dbName, 1);

        request.onerror = function(e) {
            onSuccess.@java.util.function.Consumer::accept(*)(0.0);
        };

        request.onsuccess = function(e) {
            var db = e.target.result;
            try {
                var tx = db.transaction([storeName], 'readonly');
                var store = tx.objectStore(storeName);
                var getRequest = store.get(timestampKey);

                getRequest.onsuccess = function(e) {
                    var timestamp = e.target.result;
                    if (timestamp) {
                        onSuccess.@java.util.function.Consumer::accept(*)(timestamp);
                    } else {
                        onSuccess.@java.util.function.Consumer::accept(*)(0.0);
                    }
                };

                getRequest.onerror = function(e) {
                    onSuccess.@java.util.function.Consumer::accept(*)(0.0);
                };

            } catch (err) {
                onSuccess.@java.util.function.Consumer::accept(*)(0.0);
            } finally {
                db.close();
            }
        };
    }-*/;

    private native void saveToCacheNative(String jsonData,
                                          double timestamp,
                                          Runnable onSuccess,
                                          Consumer<String> onError) /*-{
        var dbName = @gwt_service.GWT_MyServiceProvider::DB_NAME;
        var storeName = @gwt_service.GWT_MyServiceProvider::STORE_NAME;
        var cacheKey = @gwt_service.GWT_MyServiceProvider::CACHE_KEY;
        var timestampKey = @gwt_service.GWT_MyServiceProvider::CACHE_TIMESTAMP_KEY;

        var request = $wnd.indexedDB.open(dbName, 1);

        request.onerror = function(e) {
            onError.@java.util.function.Consumer::accept(*)("IndexedDB open error");
        };

        request.onupgradeneeded = function(e) {
            var db = e.target.result;
            if (!db.objectStoreNames.contains(storeName)) {
                db.createObjectStore(storeName);
            }
        };

        request.onsuccess = function(e) {
            var db = e.target.result;

            try {
                var tx = db.transaction([storeName], 'readwrite');
                var store = tx.objectStore(storeName);

                // Sačuvaj podatke
                store.put(jsonData, cacheKey);

                // Sačuvaj timestamp
                store.put(timestamp, timestampKey);

                tx.oncomplete = function() {
                    onSuccess.@java.lang.Runnable::run()();
                    db.close();
                };

                tx.onerror = function(e) {
                    onError.@java.util.function.Consumer::accept(*)("Transaction error");
                    db.close();
                };

            } catch (err) {
                onError.@java.util.function.Consumer::accept(*)(err.toString());
                db.close();
            }
        };
    }-*/;

    private native void clearCacheNative(Runnable onSuccess,
                                         Consumer<String> onError) /*-{
        var dbName = @gwt_service.GWT_MyServiceProvider::DB_NAME;

        var request = $wnd.indexedDB.deleteDatabase(dbName);

        request.onsuccess = function() {
            onSuccess.@java.lang.Runnable::run()();
        };

        request.onerror = function(e) {
            onError.@java.util.function.Consumer::accept(*)("Delete error");
        };
    }-*/;

    // ==================== JSON Serialization ====================

    /**
     * Serijalizuj Map u JSON string
     */
    private String serializeCities(Map<String, List<City>> cities) {
        StringBuilder json = new StringBuilder("{");

        boolean first = true;
        for (Map.Entry<String, List<City>> entry : cities.entrySet()) {
            if (!first) json.append(",");
            first = false;

            json.append("\"").append(entry.getKey()).append("\":[");

            boolean firstCity = true;
            for (City city : entry.getValue()) {
                if (!firstCity) json.append(",");
                firstCity = false;

                json.append("{")
                        .append("\"city\":\"").append(escapeJson(city.getCity())).append("\",")
                        .append("\"asciiname\":\"").append(escapeJson(city.getAsciiname())).append("\",")
                        .append("\"latitude\":").append(city.getLatitude()).append(",")
                        .append("\"longitude\":").append(city.getLongitude()).append(",")
                        .append("\"countrycode\":\"").append(escapeJson(city.getCountrycode())).append("\"")
                        .append("}");
            }

            json.append("]");
        }

        json.append("}");
        return json.toString();
    }

    /**
     * Deserijalizuj JSON string u Map
     */
    private Map<String, List<City>> deserializeCities(String json) {
        Map<String, List<City>> result = new HashMap<>();
        parseJsonNative(json, result);
        return result;
    }

    private native void parseJsonNative(String json, Map<String, List<City>> result) /*-{
        try {
            var data = JSON.parse(json);

            for (var key in data) {
                if (data.hasOwnProperty(key)) {
                    var cityArray = data[key];
                    var javaList = @java.util.ArrayList::new()();

                    for (var i = 0; i < cityArray.length; i++) {
                        var cityData = cityArray[i];
                        var city = @service.City::new()();

                        city.@service.City::setCity(*)(cityData.city);
                        city.@service.City::setAsciiname(*)(cityData.asciiname);
                        city.@service.City::setLatitude(*)(cityData.latitude);
                        city.@service.City::setLongitude(*)(cityData.longitude);
                        city.@service.City::setCountrycode(*)(cityData.countrycode);

                        javaList.@java.util.Collection::add(Ljava/lang/Object;)(city);
                    }

                    result.@java.util.Map::put(*)(key, javaList);
                }
            }
        } catch (e) {
            console.error("JSON parse error:", e);
        }
    }-*/;

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private int countCities(Map<String, List<City>> cities) {
        int count = 0;
        for (List<City> list : cities.values()) {
            count += list.size();
        }
        return count;
    }
}