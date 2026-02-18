package dev.java4now.web.CityLocation;

import dev.java4now.web.util.Format;
import dev.java4now.web.util.HaversineUtils;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.resource.Resource;
import service.Service_impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CityService {

    private Map<String, List<service.City>> cities = new HashMap<>();
    private boolean isLoaded = false;

    /**
     * Učitava gradove sa IndexedDB cache-om
     */
    public void loadCitiesWithCache(Consumer<Map<String, List<service.City>>> onSuccess,
                                    Consumer<Throwable> onError) {
        Console.log("Checking IndexedDB cache...");

        // 1. Proveri cache
        Service_impl.checkCache(
                cachedData -> {
                    if (cachedData != null && !cachedData.isEmpty()) {
                        Console.log("Using cached cities from IndexedDB");
                        cities = cachedData;
                        isLoaded = true;
                        onSuccess.accept(cities);
                    } else {
                        // 2. Nema cache - učitaj sa servera
                        loadFromServer(onSuccess, onError);
                    }
                },
                error -> {
                    Console.log("Cache check failed, loading from server: " + error.getMessage());
                    loadFromServer(onSuccess, onError);
                }
        );
    }

    private void loadFromServer(Consumer<Map<String, List<service.City>>> onSuccess,
                                Consumer<Throwable> onError) {
        Console.log("Loading cities from server...");
        long start = System.currentTimeMillis();

        Resource.loadText(
                "dev/java4now/web/data/cities1000.txt",

                stream -> {
                    try {
                        // OČISTI POSTOJEĆU MAPU PRE DODAVANJA
                        cities.clear();

                        // PARSE I DODAJ U INSTANCU MAPE
                        parseCities(stream);
                        isLoaded = true;

                        long end = System.currentTimeMillis();
                        Console.log("Cities parsed in " + (end - start) + " ms");
                        Console.log("Total cities in map: " + countTotalCities());

                        // 3. Sačuvaj u cache
                        Service_impl.saveToCache(cities,
                                () -> Console.log("Cities cached successfully"),
                                error -> Console.log("Cache save failed: " + error.getMessage())
                        );

                        onSuccess.accept(cities);

                    } catch (Exception e) {
                        Console.log("Parse error: " + e.getMessage());
                        onError.accept(e);
                    }
                },

                error -> {
                    Console.log("Load error: " + error.getMessage());
                    onError.accept(error);
                }
        );
    }

    private void parseCities(String stream) {
        String[] lines = stream.replace("\r\n", "\n")
                .replace("\r", "\n")
                .split("\n");

        int count = 0;
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            String[] str = line.split("\t");
            if (str.length < 9) continue;

            try {
                service.City city = new service.City();
                city.setCity(str[1]);
                city.setAsciiname(str[2]);
                city.setLatitude(Double.parseDouble(str[4]));
                city.setLongitude(Double.parseDouble(str[5]));
                city.setCountrycode(str[8]);
                addToMap(city);
                count++;
            } catch (Exception e) {
                // Skip invalid lines
                Console.log("Skipping invalid line: " + line.substring(0, Math.min(50, line.length())));
            }
        }

        Console.log("Parsed " + count + " cities");
    }

    // Dodajte ovu metodu za brojanje ukupnih gradova
    private int countTotalCities() {
        int total = 0;
        for (List<service.City> cityList : cities.values()) {
            total += cityList.size();
        }
        return total;
    }

    public service.City findByLatLong(Double latitude, Double longitude) {
        return getFromMap(latitude, longitude);
    }

    /* Adds the given city to the hashmap with location based index*/
    private void addToMap(service.City city) {
        String index = HaversineUtils.LOCINDEX(city.getLatitude(), city.getLongitude());

        List<service.City> sameIndexCities = cities.get(index);

        if (sameIndexCities == null) {
            sameIndexCities = new ArrayList<service.City>();
        }

        //Add the new city into the list of cities sharing the same index.
        sameIndexCities.add(city);
        //Add the list into the hashmap
        cities.put(index, sameIndexCities);
    }

    /* Gets a city from the hashmap for given latitude and longitude */
    private service.City getFromMap(Double latitude, Double longitude) {
        service.City result = null;
        Long start = System.nanoTime();

        String[] indexes = HaversineUtils.INDEXESAROUND(latitude, longitude);

        for (String index : indexes) {
            List<service.City> citiesForIndex = cities.get(index);

            if (citiesForIndex != null) {
                for (service.City city : citiesForIndex) {
                    if (result == null) {
                        result = city;
                    } else {
                        float resultDistance = HaversineUtils.getDistance(latitude, longitude, result.getLatitude(), result.getLongitude());
                        float cityDistance = HaversineUtils.getDistance(latitude, longitude, city.getLatitude(), city.getLongitude());

                        if (cityDistance < resultDistance) {
                            result = city;
                        }
                    }
                }
            }
        }

        Long end = System.nanoTime();
        if (result != null) {
//            Console.log("Found city: " + result.getCity() + ", Country: " + result.getCountrycode());
//            Console.log("Distance: " + HaversineUtils.getDistance(latitude, longitude, result.getLatitude(), result.getLongitude()) + " m");
        } else {
            Console.log("No city found for coordinates: " + latitude + ", " + longitude);
        }
        Console.log("Search Time: " + Format.formatDouble_GWT((double)(end - start)/1000000,3) + " ms");

        return result;
    }

    // Dodajte getter za proveru stanja
    public boolean isLoaded() {
        return isLoaded;
    }

    public int getCityCount() {
        return countTotalCities();
    }
}