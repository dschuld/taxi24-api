package rw.bk.taxi24.api.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import rw.bk.taxi24.api.domain.Driver;
import rw.bk.taxi24.api.domain.enumeration.DriverStatus;
import rw.bk.taxi24.api.repository.DriverRepository;
import rw.bk.taxi24.api.service.dto.DriverDTO;
import rw.bk.taxi24.api.service.mapper.DriverMapper;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static rw.bk.taxi24.api.service.ServiceUtilsTest.createDriver;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DriverServiceTest {

    @MockBean
    private DriverRepository repository;

    @Spy
    private DriverMapper driverMapper;

    @Autowired
    private DriverService service;

    private List<Driver> drivers;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        drivers = new ArrayList<>();
        drivers.add(createDriver(DriverStatus.AVAILABLE, "AirportDriver", 1l, -1.962992861040836d, 30.13498306274414));
        drivers.add(createDriver(DriverStatus.AVAILABLE, "BkHqDriver", 1l, -1.9481474387299946d, 30.059639811515808d));
        drivers.add(createDriver(DriverStatus.AVAILABLE, "KimiDriver", 1l, -1.9544746615897106d, 30.08274669333173d));
        drivers.add(createDriver(DriverStatus.AVAILABLE, "UsEmbassyDriver", 1l, -1.9365425785635548d, 30.077939211280636d));
        drivers.add(createDriver(DriverStatus.AVAILABLE, "NyamiramboStadiumDriver", 1l, -1.9783048149631977d, 30.04395586158978d));
        drivers.add(createDriver(DriverStatus.AVAILABLE, "UniOfRwandaDriver", 1l, -1.96065794316696d, 30.074461480959258d));
    }


    @Test
    public void findAvailableDrivers() {
        PageImpl<Driver> driverPage = new PageImpl<>(drivers);
        PageRequest pageRequest = PageRequest.of(1, 1);
        when(repository.findByStatus(any(), any())).thenReturn(driverPage);

        Page<DriverDTO> availableDrivers = service.findAllAvailableDrivers(pageRequest);
        assertTrue(availableDrivers.getContent().size() == 6);
    }

    @Test
    public void findAvailableDriverIn3KmRadius() {

        PageImpl<Driver> driverPage = new PageImpl<>(drivers);
        PageRequest pageRequest = PageRequest.of(1, 1);
        when(repository.findByStatus(any(), any())).thenReturn(driverPage);
        double kigaliHeightsLat = -1.9532425295779272d;
        double kigaliHeightsLong = 30.093203119591976d;

        List<DriverDTO> drivers = service.findAvailableDriversWithinRadius(pageRequest, kigaliHeightsLat, kigaliHeightsLong, 3.0d).getContent();
        for (DriverDTO driver : drivers) {
            switch (driver.getName()) {
                case "KimiDriver" :
                case "UsEmbassyDriver":
                case "UniOfRwandaDriver":
                    break;
                default:
                    fail("Wrong driver in result set: " + driver);

            }

            assertEquals(3, drivers.size());
        }

    }

}
