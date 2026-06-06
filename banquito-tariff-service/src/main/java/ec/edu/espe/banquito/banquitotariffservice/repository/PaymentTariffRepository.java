package ec.edu.espe.banquito.banquitotariffservice.repository;

import ec.edu.espe.banquito.banquitotariffservice.model.PaymentTariff;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTariffRepository extends JpaRepository<PaymentTariff, Long> {

    Optional<PaymentTariff> findByMinTransactionsLessThanEqualAndMaxTransactionsGreaterThanEqual(
            Integer tx1,
            Integer tx2);
}
