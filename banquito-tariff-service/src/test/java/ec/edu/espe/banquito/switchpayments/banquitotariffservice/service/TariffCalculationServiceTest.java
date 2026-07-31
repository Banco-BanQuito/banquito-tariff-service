package ec.edu.espe.banquito.switchpayments.banquitotariffservice.service;

import ec.edu.espe.banquito.switchpayments.banquitotariffservice.dto.TariffCalculationResponse;
import ec.edu.espe.banquito.switchpayments.banquitotariffservice.exception.TariffNotFoundException;
import ec.edu.espe.banquito.switchpayments.banquitotariffservice.model.PaymentTariff;
import ec.edu.espe.banquito.switchpayments.banquitotariffservice.model.ServiceCharge;
import ec.edu.espe.banquito.switchpayments.banquitotariffservice.repository.PaymentTariffRepository;
import ec.edu.espe.banquito.switchpayments.banquitotariffservice.repository.ServiceChargeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TariffCalculationServiceTest {

    @Mock
    private PaymentTariffRepository tariffRepository;

    @Mock
    private ServiceChargeRepository chargeRepository;

    private TariffCalculationService service;

    @BeforeEach
    void setUp() {
        service = new TariffCalculationService(tariffRepository, chargeRepository);
    }

    @Test
    void calculateReturnsChargeBreakdownAndPersistsIt() {
        PaymentTariff tariff = new PaymentTariff(1L, 1, 100, new BigDecimal("0.50"));
        when(tariffRepository.findByMinTransactionsLessThanEqualAndMaxTransactionsGreaterThanEqual(50, 50))
                .thenReturn(Optional.of(tariff));

        TariffCalculationResponse response = service.calculate(50, "BATCH-1");

        assertThat(response.getCommissionSubtotal()).isEqualByComparingTo("25.00");
        assertThat(response.getTotalCharge()).isEqualByComparingTo("28.75");
        verify(chargeRepository).save(any(ServiceCharge.class));
    }

    @Test
    void calculateThrowsWhenNoTariffMatchesRange() {
        when(tariffRepository.findByMinTransactionsLessThanEqualAndMaxTransactionsGreaterThanEqual(any(), any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.calculate(5, "BATCH-2"))
                .isInstanceOf(TariffNotFoundException.class);

        verify(chargeRepository, never()).save(any(ServiceCharge.class));
    }
}
