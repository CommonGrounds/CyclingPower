package dev.java4now.web.generator;

import java.util.ArrayList;
import java.util.Random;

public class CyclingElevationGenerator {

    /**
     * Generates a random elevation profile for a hilly cycling stage.
     *
     * @param length The length of the course in kilometers
     * @param samplesPerKm Number of elevation samples per kilometer
     * @param minElevation Minimum elevation in meters
     * @param maxElevation Maximum elevation in meters
     * @param numHills Approximate number of hills to generate
     * @return ArrayList of Integer elevation values in meters
     */
    public static ArrayList<Integer> generateHillyStageProfile(
            int length,
            int samplesPerKm,
            int minElevation,
            int maxElevation,
            int numHills) {

        ArrayList<Integer> random_data = new ArrayList<>();
        Random random = new Random();

        // Total number of data points
        int totalPoints = length * samplesPerKm;

        // Generate hill peaks positions
        int[] hillPeaks = new int[numHills];
        for (int i = 0; i < numHills; i++) {
            hillPeaks[i] = random.nextInt(totalPoints);
        }

        // Sort hill peaks to process them in order
        java.util.Arrays.sort(hillPeaks);

        // Initial elevation (somewhere in the lower third)
        int currentElevation = minElevation + random.nextInt((maxElevation - minElevation) / 3);
        random_data.add(currentElevation);

        // Generate the profile point by point
        for (int point = 1; point < totalPoints; point++) {
            // Check if we're approaching a hill peak
            boolean nearPeak = false;
            int distanceToPeak = Integer.MAX_VALUE;
            int peakHeight = 0;

            for (int hill : hillPeaks) {
                int distance = hill - point;
                if (distance >= -samplesPerKm * 3 && distance <= samplesPerKm * 5 && Math.abs(distance) < distanceToPeak) {
                    nearPeak = true;
                    distanceToPeak = Math.abs(distance);

                    // Higher peaks for main hills
                    if (distance == 0) {
                        peakHeight = minElevation + (int)(random.nextDouble() * 0.8 * (maxElevation - minElevation) + 0.2 * (maxElevation - minElevation));
                    }
                }
            }

            // Determine elevation change
            int elevationChange;

            if (nearPeak) {
                if (distanceToPeak == 0) {
                    // We're at a peak
                    elevationChange = peakHeight - currentElevation;
                } else if (distanceToPeak <= samplesPerKm * 2) {
                    // Approaching a peak - steeper uphill
                    elevationChange = random.nextInt(30) - 5; // Mostly uphill
                } else if (distanceToPeak <= samplesPerKm * 5) {
                    // Descending from a peak - steeper downhill
                    elevationChange = random.nextInt(30) - 25; // Mostly downhill
                } else {
                    // Random change but trending appropriately
                    elevationChange = random.nextInt(20) - 10;
                }
            } else {
                // Normal rolling terrain
                elevationChange = random.nextInt(10) - 5;

                // Tendency to return to a baseline
                if (currentElevation > minElevation + (maxElevation - minElevation) / 3) {
                    elevationChange -= 2; // Tend to go down if we're high
                } else if (currentElevation < minElevation + (maxElevation - minElevation) / 6) {
                    elevationChange += 2; // Tend to go up if we're low
                }
            }

            // Apply elevation change
            currentElevation += elevationChange;

            // Ensure within bounds
            currentElevation = Math.max(minElevation, Math.min(maxElevation, currentElevation));

            // Add to data
            random_data.add(currentElevation);
        }

        return random_data;
    }
/*
    // Example usage
    public static void main(String[] args) {
        // Generate a 120km course with 10 samples per km, elevation between 100-1200m, with 8 hills
        ArrayList<Integer> random_data = generateHillyStageProfile(120, 10, 100, 1200, 8);

        // Print sample of the data
        System.out.println("Generated " + random_data.size() + " elevation points");
        System.out.println("First 20 points: " + random_data.subList(0, 20));
        System.out.println("Elevation range: " +
                Collections.min(random_data) + "m to " +
                Collections.max(random_data) + "m");
    }
 */
}
