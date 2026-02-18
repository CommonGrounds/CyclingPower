package service;

import dev.webfx.platform.blob.Blob;
import javafx.scene.canvas.Canvas;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public interface ServiceProvider {

    default String createObjectURL(Blob blob){
        return "";
    }

    default int getWindowWidth(){
        return 0;
    }

    default int getWindowHeight(){
        return 0;
    }

    default double measureText(String str, Canvas canvas){
        return -1.0;
    }

    default String toDataURL(Canvas canvas, String type,String objectURL){
        return "";
    }

    default boolean isDarkModeEnabled(){
        return false;
    }

    default void checkCache(Consumer<Map<String, List<City>>> onSuccess,
                                        Consumer<Throwable> onError){}

    default void saveToCache(Map<String, List<City>> cities, Runnable onSuccess, Consumer<Throwable> onError){}
    default void clearCache(Runnable onSuccess, Consumer<Throwable> onError){}
}
