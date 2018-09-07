package rw.bk.taxi24.api.service;

import org.junit.Test;
import rw.bk.taxi24.api.domain.Driver;
import rw.bk.taxi24.api.domain.enumeration.DriverStatus;

import static junit.framework.TestCase.assertTrue;

public class ServiceUtilsTest {

    @Test
    public void distanceCalculation() {
        //Kigali Airport
        double airportLat = -1.962992861040836d;
        double airportLong = 30.13498306274414;

        //BK Headquarters
        double bkHqLat = -1.9481474387299946d;
        double bkHqLong = 30.059639811515808d;

        double kilometers = ServiceUtils.kilometersBetweenCoordinates(airportLat, airportLong, bkHqLat, bkHqLong);

        //The distance between Kigali Airport and BK Headquarter is slightly more than 8.5 km
        assertTrue(kilometers > 8.45 && kilometers < 8.55);
    }


    public static Driver createDriver(DriverStatus status, String name, long id) {
        Driver driver = new Driver();
        driver.setStatus(status);
        driver.setName(name);
        driver.setId(id);
        return driver;
    }

    public static Driver createDriver(DriverStatus status, String name, long id, double latitude, double longitude) {
        Driver driver = createDriver(status, name, id);
        driver.setLongitude(longitude);
        driver.setLatitude(latitude);
        return driver;
    }
}
