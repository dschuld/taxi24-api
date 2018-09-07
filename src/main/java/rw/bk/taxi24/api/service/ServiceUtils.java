package rw.bk.taxi24.api.service;

public class ServiceUtils {

    public static double kilometersBetweenCoordinates(double lat1, double long1, double lat2, double long2) {

        double degToRad = Math.PI / 180.0;

        double phi1 = lat1 * degToRad;

        double phi2 = lat2 * degToRad;

        double lam1 = long1 * degToRad;

        double lam2 = long2 * degToRad;

        return 6371.01 * Math.acos(Math.sin(phi1) * Math.sin(phi2) + Math.cos(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1));

    }

}
