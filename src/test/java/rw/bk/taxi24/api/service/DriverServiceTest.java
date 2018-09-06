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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DriverServiceTest {

    @MockBean
    private DriverRepository repository;

    @Spy
    private DriverMapper driverMapper;

    @Autowired
    private DriverService service;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
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
