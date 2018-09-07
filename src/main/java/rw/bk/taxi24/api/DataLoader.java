package rw.bk.taxi24.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import rw.bk.taxi24.api.domain.Driver;
import rw.bk.taxi24.api.domain.Rider;
import rw.bk.taxi24.api.domain.enumeration.DriverStatus;
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
//        Driver driver = new Driver().name("David").latitude(-1.9544746615897106D).longitude(30.08274669333173D).status(DriverStatus.AVAILABLE);
//        driverRepository.save(driver);
//        driver = new Driver().name("Melanie").latitude(-1.9365425785635548D).longitude(30.077939211280636D).status(DriverStatus.AVAILABLE);
//        driverRepository.save(driver);
//        driver = new Driver().name("Filou").latitude(-1.962992861040836D).longitude(30.13498306274414D).status(DriverStatus.AVAILABLE);
//        driverRepository.save(driver);
//
//        Rider rider = new Rider().name("Rider1").amountRides(0).latitude(1.9532425295779272D).longitude(30.093203119591976D);
//        riderRepository.save(rider);
//        rider = new Rider().name("Rider2").amountRides(10).latitude(2D).longitude(2D);
//        riderRepository.save(rider);
    }
}
