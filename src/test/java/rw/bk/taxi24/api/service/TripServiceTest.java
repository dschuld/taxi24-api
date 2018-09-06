package rw.bk.taxi24.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import rw.bk.taxi24.api.domain.Driver;
import rw.bk.taxi24.api.domain.Rider;
import rw.bk.taxi24.api.domain.enumeration.DriverStatus;
import rw.bk.taxi24.api.domain.enumeration.TripStatus;
import rw.bk.taxi24.api.repository.DriverRepository;
import rw.bk.taxi24.api.repository.RiderRepository;
import rw.bk.taxi24.api.repository.TripRepository;
import rw.bk.taxi24.api.service.dto.TripDTO;
import rw.bk.taxi24.api.service.mapper.TripMapper;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TripServiceTest {

    @MockBean
    private TripRepository repository;

    @MockBean
    private DriverRepository driverRepository;

    @MockBean
    private RiderRepository riderRepository;

    @Spy
    private TripMapper tripMapper;

    @Autowired
    private TripService service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void requestTrip() {

        long driverId = 11l;
        long riderId = 7l;
        Driver driver = new Driver().name("theDriver").status(DriverStatus.AVAILABLE);
        driver.setId(driverId);
        Rider rider = new Rider().name("rheRider");
        rider.setId(riderId);
        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(riderRepository.findById(riderId)).thenReturn(Optional.of(rider));
        when(repository.save(any())).thenAnswer((Answer) invocation -> invocation.getArguments()[0]);

        TripDTO tripDTO = service.requestTrip(driverId, riderId);

        verify(driverRepository, times(1)).findById(driverId);
        verify(riderRepository, times(1)).findById(riderId);
        assertEquals(tripDTO.getTripStatus(), TripStatus.REQUESTED);
        assertEquals(tripDTO.getDriver(), driver);
        assertEquals(tripDTO.getRider(), rider);

    }

    @Test(expected = DriverNotAvailableException.class)
    public void requestTripWithOccupiedDriver() {
        long driverId = 11l;
        long riderId = 7l;
        Driver driver = new Driver().name("theDriver").status(DriverStatus.OCCUPIED);
        driver.setId(driverId);
        Rider rider = new Rider().name("rheRider");
        rider.setId(riderId);
        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(riderRepository.findById(riderId)).thenReturn(Optional.of(rider));
        when(repository.save(any())).thenAnswer((Answer) invocation -> invocation.getArguments()[0]);

        service.requestTrip(driverId, riderId);
    }


    @Test(expected = NoSuchElementException.class)
    public void requestTripWithNotExistingRider() {
        long driverId = 11l;
        long riderId = 7l;
        Driver driver = new Driver().name("theDriver").status(DriverStatus.AVAILABLE);
        driver.setId(driverId);
        Rider rider = new Rider().name("rheRider");
        rider.setId(riderId);
        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(riderRepository.findById(riderId)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer((Answer) invocation -> invocation.getArguments()[0]);

        service.requestTrip(driverId, riderId);
    }

}
