package rw.bk.taxi24.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import rw.bk.taxi24.api.domain.Driver;
import rw.bk.taxi24.api.domain.Rider;
import rw.bk.taxi24.api.domain.Trip;
import rw.bk.taxi24.api.domain.enumeration.DriverStatus;
import rw.bk.taxi24.api.domain.enumeration.TripStatus;
import rw.bk.taxi24.api.repository.DriverRepository;
import rw.bk.taxi24.api.repository.RiderRepository;
import rw.bk.taxi24.api.repository.TripRepository;
import rw.bk.taxi24.api.service.dto.DriverDTO;
import rw.bk.taxi24.api.service.dto.RiderDTO;
import rw.bk.taxi24.api.service.dto.TripDTO;
import rw.bk.taxi24.api.service.mapper.DriverMapperImpl;
import rw.bk.taxi24.api.service.mapper.RiderMapperImpl;
import rw.bk.taxi24.api.service.mapper.TripMapper;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

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
        DriverDTO driverDTO = new DriverMapperImpl().toDto(driver);
        driverDTO.setId(driverId);
        Rider rider = new Rider().name("rheRider");
        rider.setId(riderId);
        RiderDTO riderDTO = new RiderMapperImpl().toDto(rider);
        riderDTO.setId(riderId);
        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(riderRepository.findById(riderId)).thenReturn(Optional.of(rider));
        when(repository.save(any())).thenAnswer((Answer) invocation -> invocation.getArguments()[0]);

        TripDTO tripDTO = service.requestTrip(driverId, riderId);

        verify(driverRepository, times(1)).findById(driverId);
        verify(riderRepository, times(1)).findById(riderId);
        assertEquals(TripStatus.REQUESTED, tripDTO.getTripStatus());
        assertEquals(driverDTO, tripDTO.getDriver());
        assertEquals(riderDTO, tripDTO.getRider());

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

    @Test
    public void completeTrip() {

        updateTrip(TripStatus.ACTIVE, TripStatus.COMPLETED, tripId -> service.completeTrip(tripId));
        verify(riderRepository, times(1)).saveAndFlush(any());
    }


    @Test(expected = BadTripStatusException.class)
    public void invalidCompleteTrip() {

        updateTrip(TripStatus.REQUESTED, TripStatus.COMPLETED, tripId -> service.completeTrip(tripId));
    }

    @Test
    public void startTrip() {
        updateTrip(TripStatus.REQUESTED, TripStatus.ACTIVE, tripId -> service.startTrip(tripId));
    }

    @Test(expected = BadTripStatusException.class)
    public void invalidStartTrip() {

        updateTrip(TripStatus.COMPLETED, TripStatus.ACTIVE, tripId -> service.startTrip(tripId));
    }

    @Test
    public void cancelTrip() {
        updateTrip(TripStatus.REQUESTED, TripStatus.CANCELLED, tripId -> service.cancelTrip(tripId));
    }

    @Test(expected = BadTripStatusException.class)
    public void invalidCancelTrip() {

        updateTrip(TripStatus.COMPLETED, TripStatus.CANCELLED, tripId -> service.cancelTrip(tripId));
    }

    private void updateTrip(TripStatus currentStatus, TripStatus newStatus, Function<Long, TripDTO> function) {
        long tripId = 1l;
        Trip trip = new Trip();
        trip.setId(tripId);
        trip.driver(new Driver()).rider(new Rider()).tripStatus(currentStatus);

        when(repository.findById(tripId)).thenReturn(Optional.of(trip));

        function.apply(tripId);

        verify(repository, times(1)).save(trip);
        assertEquals(trip.getTripStatus(), newStatus);
    }


}
