package service;

import dev.webfx.platform.service.SingleServiceProvider;
import java.util.ServiceLoader;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import dev.webfx.platform.blob.Blob;

import javafx.scene.canvas.Canvas;

public final class Service_impl {
    private Service_impl() {}

    private static ServiceProvider getProvider() {
        return SingleServiceProvider.getProvider(ServiceProvider.class, () -> ServiceLoader.load(ServiceProvider.class));
    }

    public static String createObjectURL(Blob blob){
        return getProvider().createObjectURL(blob);
    }

    public static int getWindowWidth(){
        return getProvider().getWindowWidth();
    }

    public static int getWindowHeight(){
        return getProvider().getWindowHeight();
    }

    public static double measureText(String str, Canvas canvas){
        return getProvider().measureText(str, canvas);
    }

    public static String toDataURL(Canvas canvas, String type , String objectURL){
        return getProvider().toDataURL(canvas, type, objectURL);
    }

    public static boolean isDarkModeEnabled(){
        return getProvider().isDarkModeEnabled();
    }

    //-------------------- IndexDB ----------------------
    public static void checkCache(Consumer<Map<String, List<City>>> onSuccess,
                                  Consumer<Throwable> onError){
        getProvider().checkCache(onSuccess, onError);
    }
    public static void saveToCache(Map<String, List<City>> cities,
                                   Runnable onSuccess,
                                   Consumer<Throwable> onError){
        getProvider().saveToCache(cities, onSuccess, onError);
    }
    public static void clearCache(Runnable onSuccess, Consumer<Throwable> onError){
        getProvider().clearCache(onSuccess, onError);
    }
}