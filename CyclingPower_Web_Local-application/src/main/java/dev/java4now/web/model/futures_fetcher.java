package dev.java4now.web.model;

import dev.java4now.web.CyclingPower_Web_Local;
import dev.java4now.web.Settings;
import dev.java4now.web.charts.SummaryChart;
import dev.java4now.web.custom_ui.MenuButtonGroup;
import dev.java4now.web.http.SimpleBase64;
import dev.webfx.platform.ast.ReadOnlyAstArray;
import dev.webfx.platform.ast.ReadOnlyAstObject;
import dev.webfx.platform.ast.json.Json;
import dev.webfx.platform.async.Future;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.fetch.Fetch;
import dev.webfx.platform.fetch.FetchOptions;
import dev.webfx.platform.fetch.Headers;
import dev.webfx.platform.scheduler.Scheduler;
import javafx.application.Platform;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static dev.java4now.web.CyclingPower_Web_Local.*;
import static dev.java4now.web.custom_ui.SimpleListView.progress;
import static dev.java4now.web.view.UpPane.jsonFileListView;


public class futures_fetcher {
    // UI component
//    private static ListView<String> jsonFileListView = new ListView<>();
    // Result map
    public static NavigableActivityLinkedList linked_list = new NavigableActivityLinkedList();
    public static LinkedHashMap<String, String> final_list = new LinkedHashMap<>();
    public static Map<Month, Integer> activity_per_month = new LinkedHashMap<>();
    private static final int current_Year = LocalDate.now().getYear();
    public static int chosen_year = current_Year;
    public static MenuButtonGroup yearSelector; // Set from LeftPane
    public static MenuButtonGroup monthSelector;
    // Progress counter
    private static boolean LOAD_ACTIVITY = true;
    private static boolean FETCH_NAME_LOCAL = true;
    private static long DELAY = 0; // 1000 ako je remote ( nominatim server )
    private static int ERROR_COUNT = 0;


    // Cache for all activities
    private static final Map<String, CyclingActivity> activityCache = new LinkedHashMap<>();
    private static boolean isCacheLoaded = false;
    private static boolean allFetchingDone = false;


    // Fetch all files and populate cache (called once or on refresh)
    public static void fetchAllFiles() {
//        String listJsonUrl = "http://localhost:8880/api/list-json";
        String listJsonUrl = BASE_URL + "/api/list-json";
        FetchOptions options = new FetchOptions();
        Headers headers = Headers.create();
        String auth = Settings.name_txt.get() + ":" + Settings.pass_txt.get();
        headers.set("Authorization", "Basic " + SimpleBase64.encode(auth));
        Console.log("fetchAllFiles() - " + auth + " - " + SimpleBase64.encode(auth));
        options.setMethod("GET");
        options.setHeaders(headers);
        allFetchingDone = false;

        Fetch.fetch(listJsonUrl, options)
                .onSuccess(response -> {
                    if (response.status() == 200) {
                        response.text()
                                .onSuccess(jsonText -> {
                                    ReadOnlyAstArray jsonFiles = Json.parseArray(jsonText);
                                    activityCache.clear();
                                    linked_list.clear();
                                    final_list.clear();
                                    AtomicInteger counter = new AtomicInteger();

                                    if (jsonFiles.isEmpty()) {
                                        jsonFileListView.getItems().add("No JSON files found");
                                        isCacheLoaded = true;
                                        updateListFromCache();
                                        return;
                                    }

                                    progress.setVisible(true);
                                    Future<List<CyclingActivity>> allFetches = Future.succeededFuture(new ArrayList<>());
                                    for (int i = 0; i < jsonFiles.size(); i++) {
                                        String fileName = jsonFiles.getString(i);
//                                        Console.log("Fetching " + fileName + " (" + counter.get() + "/" + jsonFiles.size() + ")");
                                        allFetches = allFetches.flatMap(list -> fetchJsonFile(fileName)
                                                .map(activity -> {
                                                    if (activity != null) {
                                                        list.add(activity);
                                                        if(LOAD_ACTIVITY && Settings.AUTO_LOAD){
                                                            CyclingPower_Web_Local.load_activity(activity); // auto ucitavanje samo 1. rezultata
                                                            LOAD_ACTIVITY = false;
                                                        }
                                                        activityCache.put(fileName, activity);
                                                        counter.getAndIncrement();
                                                        Console.log("Cached " + fileName + " (" + counter.get() + "/" + jsonFiles.size() + ")");
                                                        isCacheLoaded = true;
                                                        updateListFromCache();
                                                        Platform.runLater(() -> progress.setProgress((double) counter.get() / jsonFiles.size()));
                                                    }
                                                    return list;
                                                }));
                                    }

                                    allFetches.onSuccess(activities -> {
                                        Console.log("All files cached. Total: " + activityCache.size());
                                        isCacheLoaded = true;
                                        allFetchingDone = true;
                                        progress.setVisible(false);
                                        updateListFromCache(); // Initial display
                                        if(bar_chart != null) {
                                            SummaryChart.update_data();
                                            Settings.saveActivityPerMonthToStorage();
                                        }
                                    }).onFailure(error -> {
                                        Console.log("Error in batch fetch: " + error.getMessage());
                                        progress.setVisible(false);
                                        isCacheLoaded = true;
                                        updateListFromCache();
                                    });
                                })
                                .onFailure(error -> {
                                    Console.log("Failed to parse JSON file list: " + error.getMessage());
                                    progress.setVisible(false);
                                    isCacheLoaded = true;
                                    updateListFromCache();
                                });
                    } else {
                        Console.log("Failed to fetch JSON file list. Status: " + response.status());
                        progress.setVisible(false);
                        isCacheLoaded = true;
                        updateListFromCache();
                    }
                })
                .onFailure(error -> {
                    Console.log("Fetch error: " + error.getMessage());
                    progress.setVisible(false);
                    isCacheLoaded = true;
                    updateListFromCache();
                });
    }


    // Update list from cached data based on selected year and month
    public static void updateListFromCache() {
        if (!isCacheLoaded) {
            fetchAllFiles(); // Fetch if cache is empty
            return;
        }

        jsonFileListView.getItems().clear();
        linked_list.clear();
        final_list.clear();
        activity_per_month.clear(); // Clear previous counts - ensures the map is reset before counting activities

        // Initialize activity_per_month for all months
        for (Month month : Month.values()) {
            activity_per_month.put(month, 0); // loop initializes the map with all months set to 0 to ensure every month has an entry, even if there are no activities
        }

        // Filter by year and month
        int i = yearSelector.getSelectedValue().indexOf(" ",1);
        String selectedYear = yearSelector.getSelectedValue().substring(1,i);
        i = monthSelector.getSelectedValue().indexOf(" ",1);
        String selectedMonth = monthSelector.getSelectedValue().substring(1,i);
//        Console.log("year-btn:month-btn - " + yearSelector.getSelectedValue() + ":" + monthSelector.getSelectedValue());
        int year = Integer.parseInt(selectedYear);
        chosen_year = year;
        Month month = Month.valueOf(selectedMonth.toUpperCase());
//        Console.log("year:month - " + year + ":" + month);

// Count activities for each month in the current year
// If you want to count activities for a different year (e.g., the selected year from yearSelector), replace current_Year with year in the counting stream.
        activityCache.entrySet().stream()
                .filter(entry -> entry.getValue().getSession().getDate().getYear() == year/*current_Year*/)
                .forEach(entry -> {
                    Month activityMonth = entry.getValue().getSession().getDate().getMonth();
                    activity_per_month.compute(activityMonth, (k, v) -> v == null ? 1 : v + 1);
//                    SummaryChart.update_data(); //  update chart ali radi refresh celog charta svaki put kada se doda activity
                });
        if(allFetchingDone) SummaryChart.update_data(); // update kada se dodaju svi activities

        List<Map.Entry<String, CyclingActivity>> filtered = activityCache.entrySet().stream()
                .filter(entry -> {
                    CyclingActivity a = entry.getValue();
//                    Console.log(a.getSession().getDate().getYear() + ":" + a.getSession().getDate().getMonth());
                    return a.getSession().getDate().getYear() == year && a.getSession().getDate().getMonth() == month;
                })
                .sorted(Comparator.comparing(entry -> entry.getValue().getSession().getDate()))
                .collect(Collectors.toList());

        for (Map.Entry<String, CyclingActivity> entry : filtered) {
            String fileName = entry.getKey();
            CyclingActivity activity = entry.getValue();
            linked_list.add(fileName, activity);
            double tmp = 10 * (activity.getSession().getTotalDistance() / 1000);
            tmp = (double) (int) (tmp);
            tmp /= 10;
            String name = activity.getDescriptiveName() + " - " + tmp + " km";
            final_list.put(name, fileName);
            jsonFileListView.getItems().add(name);
            Console.log("Displayed: " + name + " for " + fileName);
        }

        if (filtered.isEmpty()) {
            jsonFileListView.getItems().add("No activities for " + selectedMonth + " " + selectedYear);
//            progress.setVisible(true); // zbog debug razloga
//            progress.setProgress(0.5);
        }
/*
        // Log the activity counts for debugging (optional)
        activity_per_month.forEach((m, count) ->
                Console.log("Month: " + m + ", Activities: " + count));

 */
    }

    private static Future<CyclingActivity> fetchJsonFile(String fileName) {
//        Console.log("fetchJsonFile(" + fileName + ")");
//        String jsonUrl = "http://localhost:8880/api/download-json/" + fileName;
        String jsonUrl = BASE_URL + "/api/download-json/" + fileName;
        FetchOptions options = new FetchOptions();
        Headers headers = Headers.create();
        String auth = Settings.name_txt.get() + ":" + Settings.pass_txt.get();
        headers.set("Authorization", "Basic " + SimpleBase64.encode(auth));
        options.setMethod("GET");
        options.setHeaders(headers);

        return Future.future(promise -> {
            Fetch.fetch(jsonUrl, options)
                    .onSuccess(response -> {
                        if (response.status() == 200) {
                            response.text()
                                    .onSuccess(jsonText -> {
                                        try {
                                            ReadOnlyAstObject jsonObject = Json.parseObject(jsonText);
                                            CyclingActivity activity = CyclingActivity.fromJson(jsonObject);

                                            List<CyclingActivity.RecordData> records = activity.getRecords();
                                            if (records == null || records.isEmpty()) {
                                                activity.setDescriptiveName(fileName);
                                                promise.complete(activity);
                                                return;
                                            }

                                            CyclingActivity.RecordData first = records.get(0);
                                            double firstLat = first.getLatitude();
                                            double firstLon = first.getLongitude();
                                            int middleIndex = records.size() / 2;
                                            CyclingActivity.RecordData middle = records.get(middleIndex);
                                            double middleLat = middle.getLatitude();
                                            double middleLon = middle.getLongitude();

                                            AtomicReference<String> first_city = new AtomicReference<>();
                                            fetchCityName(firstLat, firstLon)
                                                    .flatMap(firstCity ->
                                                            Future.future(delayPromise ->
                                                                    Scheduler.scheduleDelay(DELAY, () -> {
                                                                        first_city.set(firstCity);
                                                                        fetchCityName(middleLat, middleLon)
                                                                                .onSuccess(middleCity -> delayPromise.complete(middleCity))
                                                                                .onFailure(delayPromise::fail);
                                                                    }))
                                                    )
                                                    .onSuccess(middleCity -> {
                                                        String descriptiveName = first_city.get() + " to " + middleCity;
                                                        activity.setDescriptiveName(descriptiveName);
                                                        promise.complete(activity);
                                                    })
                                                    .onFailure(error -> {
                                                        Console.log("Failed to fetch city names for " + fileName + ": " + error.getMessage());
                                                        activity.setDescriptiveName(fileName);
                                                        promise.complete(activity);
                                                    });
                                        } catch (Exception e) {
                                            promise.fail(e);
                                        }
                                    })
                                    .onFailure(error -> promise.fail(error));
                        } else {
                            promise.fail(new Exception("Failed to fetch " + fileName + ". Status: " + response.status()));
                        }
                    })
                    .onFailure(error -> promise.fail(error));
        });
    }


    // Fetch city name from Nominatim OpenStreetMap
    private static Future<String> fetchCityName(double lat, double lon) {
        if (FETCH_NAME_LOCAL) {
            service.City city = cityService.findByLatLong(lat,lon);
            return Future.future(promise -> {
                if (city != null) {
                    promise.complete(city.getCity());
                }else {
                    ERROR_COUNT++;
                    Console.log("ERROR_COUNT: " + ERROR_COUNT );
                    if(ERROR_COUNT >= 3){
                        FETCH_NAME_LOCAL = false; // prelazimo na nominatim
                        DELAY = 1000;
                    }
                    promise.complete("Unknown");
                }
            });
        }

        Console.log("Using Nominatim to fetch city - FETCH_NAME_LOCAL: " + FETCH_NAME_LOCAL + ", DELAY: " + DELAY);
        String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + lat + "&lon=" + lon;

        FetchOptions options = new FetchOptions();
        options.setMethod("GET");
        // Nominatim requires a User-Agent header per usage policy
        Headers headers = Headers.create();
        headers.set("User-Agent", "CyclingApp/1.0 (java4now@gmail.com)");
        options.setHeaders(headers);

        return Future.future(promise -> {
            Fetch.fetch(url, options)
                    .onSuccess(response -> {
                        if (response.status() == 200) {
                            response.text()
                                    .onSuccess(jsonText -> {
                                        try {
                                            ReadOnlyAstObject json = Json.parseObject(jsonText);
                                            ReadOnlyAstObject address = json.getObject("address");
                                            String city = address.getString("city");
                                            if (city == null) {
                                                city = address.getString("town") != null ? address.getString("town") :
                                                        address.getString("village") != null ? address.getString("village") : "Unknown";
                                            }
                                            promise.complete(city);
                                        } catch (Exception e) {
                                            promise.fail(e);
                                        }
                                    })
                                    .onFailure(error -> promise.fail(error));
                        } else {
                            promise.fail(new Exception("Failed to fetch city name. Status: " + response.status()));
                        }
                    })
                    .onFailure(error -> promise.fail(error));
        });
    }


    public static void navigateNext() {
        NavigableActivityLinkedList.ActivityEntry next = linked_list.next();
        if (next != null) {
            CyclingPower_Web_Local.left_button_disabled.set(false);
            String fileName = next.getKey();
            String descriptiveName = final_list.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(fileName))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);
            if (descriptiveName != null) {
                jsonFileListView.selectItem(descriptiveName);
//                loadMapForItem(descriptiveName);
            }
        }else{
            CyclingPower_Web_Local.right_button_disabled.set(true);
        }
    }

    public static void navigatePrevious() {
        NavigableActivityLinkedList.ActivityEntry prev = linked_list.previous();
        var ddd = linked_list.getEntries();
        if (prev != null) {
            CyclingPower_Web_Local.right_button_disabled.set(false);
            String fileName = prev.getKey();
//            Console.log("get_key: " + prev.getKey());
            String descriptiveName = final_list.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(fileName))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);
            if (descriptiveName != null) {
                jsonFileListView.selectItem(descriptiveName);
//                loadMapForItem(descriptiveName);
            }
        }else{
            CyclingPower_Web_Local.left_button_disabled.set(true);
        }
    }
}
