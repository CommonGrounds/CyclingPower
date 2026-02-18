package dev.java4now.web.util;

public class HaversineUtils {
  
    //** The Earth's radius, in kilometers. */
    private static final double EARTH_RADIUS_M = 6371.0 * 1000.0;

    ///** The Earth's radius, in miles. */
    //private static final double EARTH_RADIUS_M = 3961.0;
  
  /**
   * Gets the great circle distance in meters between two geographical points, using
   * the <a href="http://en.wikipedia.org/wiki/Haversine_formula">haversine formula</a>.
   *
   * @param latitude1 the latitude of the first point
   * @param longitude1 the longitude of the first point
   * @param latitude2 the latitude of the second point
   * @param longitude2 the longitude of the second point
   * @return the distance, in meters, between the two points
   */
  public static float getDistance(double latitude1, double longitude1, double latitude2,
          double longitude2) {
      double dLat = Math.toRadians(latitude2 - latitude1);
      double dLon = Math.toRadians(longitude2 - longitude1);
      double lat1 = Math.toRadians(latitude1);
      double lat2 = Math.toRadians(latitude2);
      double sqrtHaversineLat = Math.sin(dLat / 2);
      double sqrtHaversineLon = Math.sin(dLon / 2);
      double a = sqrtHaversineLat * sqrtHaversineLat + sqrtHaversineLon * sqrtHaversineLon
              * Math.cos(lat1) * Math.cos(lat2);
      double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  
      return (float) (EARTH_RADIUS_M * c);
  }
  
  /**
   * This method returns an compound index for given location.
   * @param latitude
   * @param longitude
   * @return String Index
   */
  public static String LOCINDEX(double latitude, double longitude) {
      // Konvertuj u integer
      int latInt = (int) latitude;
      int lonInt = (int) longitude;

      // Koristi Format.pad() za zero-padding sa 3 cifre
      String latStr = Format.pad(latInt, 3);
      String lonStr = Format.pad(lonInt, 3);

      return latStr + ":" + lonStr;
  }
  
  /**
   * This method returns an compound index for given location and 8 other squares around it.
   * @param latitude
   * @param longitude
   * @return String[] indexes
   */
  
  public static String[] INDEXESAROUND(double latitude, double longitude) {
    String[] result = new String[9];
    
    for(int latInd = -1; latInd <= 1;  latInd ++){
      for(int lonInd = -1; lonInd <= 1;  lonInd ++) {

        result[(latInd+1)*3 + (lonInd+1)]  = LOCINDEX (latitude + ((double)latInd) , longitude + ((double)lonInd));

      }
    }
    return result;
  }
  
}


