package rw.bk.taxi24.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import rw.bk.taxi24.api.domain.Driver;
import rw.bk.taxi24.api.domain.DriverStatus;
import rw.bk.taxi24.api.domain.Rider;
import rw.bk.taxi24.api.repository.DriverRepository;
import rw.bk.taxi24.api.repository.RiderRepository;

@Component
public class DataLoader implements ApplicationRunner {

    private final RiderRepository riderRepository;
    private final DriverRepository driverRepository;

    @Autowired
    public DataLoader(DriverRepository driverRepository, RiderRepository riderRepository) {
        this.driverRepository = driverRepository;
        this.riderRepository = riderRepository;
    }

    public void run(ApplicationArguments args) {
        Driver driver = new Driver().name("David").latitude(50.1f).longitude(1.2f).status(DriverStatus.AVAILABLE);
        driverRepository.save(driver);
        driver = new Driver().name("Melanie").latitude(50.1f).longitude(51.2f).status(DriverStatus.UNAVAILABLE);
        driverRepository.save(driver);
        driver = new Driver().name("Filou").latitude(3.1f).longitude(11.2f).status(DriverStatus.OCCUPIED);
        driverRepository.save(driver);

        Rider rider = new Rider().name("Rider1").amountRides(0);
        riderRepository.save(rider);
        rider = new Rider().name("Rider2").amountRides(10);
        riderRepository.save(rider);
    }
}
