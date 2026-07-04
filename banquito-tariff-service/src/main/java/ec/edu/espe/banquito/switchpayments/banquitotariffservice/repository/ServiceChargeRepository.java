package ec.edu.espe.banquito.switchpayments.banquitotariffservice.repository;

import ec.edu.espe.banquito.switchpayments.banquitotariffservice.model.ServiceCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceChargeRepository extends JpaRepository<ServiceCharge, Long> {
}
