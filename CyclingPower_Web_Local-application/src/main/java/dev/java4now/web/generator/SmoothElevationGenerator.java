package dev.java4now.web.generator;

import java.util.ArrayList;
import java.util.Random;

public class SmoothElevationGenerator {
    
    /**
     * Generates a random elevation profile for a hilly cycling stage
     * with smoother transitions to look better when graphed with limited width.
     * 
     * @param length The length of the course in kilometers
     * @param samplesPerKm Number of elevation samples per kilometer
     * @param minElevation Minimum elevation in meters
     * @param maxElevation Maximum elevation in meters
     * @param numHills Approximate number of hills to generate
     * @param screenWidth Width of the display screen in pixels
     * @return ArrayList of Integer elevation values in meters
     */
    public static ArrayList<Integer> generateSmoothHillyProfile(
            int length, 
            int samplesPerKm, 
            int minElevation, 
            int maxElevation, 
            int numHills,
            int screenWidth) {
        
        ArrayList<Integer> random_data = new ArrayList<>();
        Random random = new Random();
        
        // Total number of data points
        int totalPoints = length * samplesPerKm;
        
        // Calculate how many data points per screen pixel
        double pointsPerPixel = (double) totalPoints / screenWidth;
        
        // Limit maximum elevation change based on points per pixel
        // This ensures changes look smooth when displayed on screen
        int maxChangePerSample = Math.max(1, (int)(3 * pointsPerPixel));
        
        // Generate control points for hills (fewer than number of hills for smoother transitions)
        int numControlPoints = numHills * 3; // More control points for smoother curves
        int[] controlPoints = new int[numControlPoints];
        int[] controlElevations = new int[numControlPoints];
        
        // Start and end at moderate elevations
        int startElevation = minElevation + (maxElevation - minElevation) / 4;
        int endElevation = minElevation + (maxElevation - minElevation) / 4;
        
        // First control point
        controlPoints[0] = 0;
        controlElevations[0] = startElevation;
        
        // Last control point
        controlPoints[numControlPoints - 1] = totalPoints - 1;
        controlElevations[numControlPoints - 1] = endElevation;
        
        // Generate intermediate control points
        for (int i = 1; i < numControlPoints - 1; i++) {
            // Distribute control points with some randomness
            controlPoints[i] = random.nextInt(totalPoints);
            
            // Generate varying elevations for control points
            if (i % 3 == 0) {
                // This will be a hill peak
                controlElevations[i] = minElevation + 
                    (int)(random.nextDouble() * 0.7 * (maxElevation - minElevation) + 
                          0.2 * (maxElevation - minElevation));
            } else {
                // Lower elevations for valleys
                controlElevations[i] = minElevation + 
                    (int)(random.nextDouble() * 0.4 * (maxElevation - minElevation));
            }
        }
        
        // Sort control points by position
        for (int i = 0; i < numControlPoints - 1; i++) {
            for (int j = i + 1; j < numControlPoints; j++) {
                if (controlPoints[i] > controlPoints[j]) {
                    // Swap positions
                    int temp = controlPoints[i];
                    controlPoints[i] = controlPoints[j];
                    controlPoints[j] = temp;
                    
                    // Swap elevations
                    temp = controlElevations[i];
                    controlElevations[i] = controlElevations[j];
                    controlElevations[j] = temp;
                }
            }
        }
        
        // Generate profile using spline interpolation between control points
        int prevControlPoint = 0;
        
        for (int point = 0; point < totalPoints; point++) {
            // Find the current control point segment
            while (prevControlPoint < numControlPoints - 1 && 
                   point > controlPoints[prevControlPoint + 1]) {
                prevControlPoint++;
            }
            
            int start = controlPoints[prevControlPoint];
            int end = controlPoints[prevControlPoint + 1];
            int startEle = controlElevations[prevControlPoint];
            int endEle = controlElevations[prevControlPoint + 1];
            
            // Calculate progress through current segment (0.0 to 1.0)
            double progress = (double)(point - start) / (end - start);
            
            // Use a smoothing function (cubic ease in/out)
            double smoothProgress = progress < 0.5 ? 
                4 * progress * progress * progress : 
                1 - Math.pow(-2 * progress + 2, 3) / 2;
                
            // Calculate interpolated elevation
            int elevation = (int)(startEle + (endEle - startEle) * smoothProgress);
            
            // Add small random variation for natural look
            elevation += random.nextInt(maxChangePerSample * 2) - maxChangePerSample;
            
            // Ensure within bounds
            elevation = Math.max(minElevation, Math.min(maxElevation, elevation));
            
            // Add to data
            random_data.add(elevation);
        }
        
        return random_data;
    }
    /*
    // Example usage
    public static void main(String[] args) {
        // Parameters for the profile
        int courseLength = 120;      // Length in km
        int samplesPerKm = 10;       // Data points per km
        int minElevation = 100;      // Minimum elevation in meters
        int maxElevation = 1200;     // Maximum elevation in meters
        int numHills = 6;            // Number of major hills
        int screenWidth = 800;       // Graph display width in pixels
        
        // Generate the elevation profile
        ArrayList<Integer> random_data = generateSmoothHillyProfile(
            courseLength, samplesPerKm, minElevation, maxElevation, numHills, screenWidth);
        
        // Print sample of the data
        System.out.println("Generated " + random_data.size() + " elevation points");
        System.out.println("First 20 points: " + random_data.subList(0, 20));
        System.out.println("Elevation range: " + 
                           Collections.min(random_data) + "m to " + 
                           Collections.max(random_data) + "m");
    }
    */
}
