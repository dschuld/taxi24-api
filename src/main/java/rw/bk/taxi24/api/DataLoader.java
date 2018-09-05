package rw.bk.taxi24.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import rw.bk.taxi24.api.domain.Driver;
import rw.bk.taxi24.api.domain.DriverStatus;
import rw.bk.taxi24.api.repository.DriverRepository;

@Component
public class DataLoader implements ApplicationRunner {

    private DriverRepository driverRepository;

    @Autowired
    public DataLoader(DriverRepository userRepository) {
        this.driverRepository = userRepository;
    }

    public void run(ApplicationArguments args) {
        Driver driver = new Driver();
        driver.setName("David");
        driver.setLatitude(50.1f);
        driver.setLongitude(1.2f);
        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.save(driver);


        driver = new Driver();
        driver.setName("Melanie");
        driver.setLatitude(50.1f);
        driver.setLongitude(51.2f);
        driver.setStatus(DriverStatus.UNAVAILABLE);
        driverRepository.save(driver);


        driver = new Driver();
        driver.setName("Filou");
        driver.setLatitude(3.1f);
        driver.setLongitude(11.2f);
        driver.setStatus(DriverStatus.OCCUPIED);
        driverRepository.save(driver);
    }
}
