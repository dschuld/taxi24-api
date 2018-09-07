package rw.bk.taxi24.api.service;

/**
 * Util methods for service classes.
 */
public class ServiceUtils {

    /**
     * Calculate the distance in kilometers between to Points.
     * Taken from https://software.intel.com/en-us/blogs/2012/11/25/calculating-geographic-distances-in-location-aware-apps
     * @param lat1 Latitude of first point
     * @param long1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param long2 Longitude of second point
     * @return The distance in kilometers
     */
    public static double kilometersBetweenCoordinates(double lat1, double long1, double lat2, double long2) {

        double degToRad = Math.PI / 180.0;
        double phi1 = lat1 * degToRad;
        double phi2 = lat2 * degToRad;
        double lam1 = long1 * degToRad;
        double lam2 = long2 * degToRad;
        return 6371.01 * Math.acos(Math.sin(phi1) * Math.sin(phi2) + Math.cos(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1));

    }

}
