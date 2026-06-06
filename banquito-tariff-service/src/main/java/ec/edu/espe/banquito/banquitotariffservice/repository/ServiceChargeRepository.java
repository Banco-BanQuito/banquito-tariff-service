package ec.edu.espe.banquito.banquitotariffservice.repository;

import ec.edu.espe.banquito.banquitotariffservice.model.ServiceCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceChargeRepository extends JpaRepository<ServiceCharge, Long> {
}
