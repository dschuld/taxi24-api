package rw.bk.taxi24.api.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import rw.bk.taxi24.api.domain.Driver;
import rw.bk.taxi24.api.domain.DriverStatus;
import rw.bk.taxi24.api.repository.DriverRepository;
import rw.bk.taxi24.api.service.dto.DriverDTO;
import rw.bk.taxi24.api.service.mapper.DriverMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DriverServiceTest {

    @Mock
    private DriverRepository repository;

    @Spy
    private DriverMapper driverMapper;

    private DriverService service;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new DriverService(repository, driverMapper);
    }


    @Test
    public void findAvailableDrivers() {
        List<Driver> drivers = new ArrayList<>();
        Driver driver = new Driver();
        driver.setStatus(DriverStatus.AVAILABLE);
        driver.setName("TestName");
        driver.setId(1l);
        drivers.add(driver);
        PageImpl<Driver> driverPage = new PageImpl<>(drivers);
        PageRequest pageRequest = PageRequest.of(1, 1);
        when(repository.findByStatus(any(), any())).thenReturn(driverPage);

        Page<DriverDTO> availableDrivers = service.findAllAvailableDrivers(pageRequest);
        assertTrue(availableDrivers.getContent().size() == 1);
    }
}
