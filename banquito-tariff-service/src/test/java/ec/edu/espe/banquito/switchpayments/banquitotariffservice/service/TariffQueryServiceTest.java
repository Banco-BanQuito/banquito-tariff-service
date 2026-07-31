package ec.edu.espe.banquito.switchpayments.banquitotariffservice.service;

import ec.edu.espe.banquito.switchpayments.banquitotariffservice.dto.TariffRangeResponse;
import ec.edu.espe.banquito.switchpayments.banquitotariffservice.model.PaymentTariff;
import ec.edu.espe.banquito.switchpayments.banquitotariffservice.repository.PaymentTariffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TariffQueryServiceTest {

    @Mock
    private PaymentTariffRepository repository;

    private TariffQueryService service;

    @BeforeEach
    void setUp() {
        service = new TariffQueryService(repository);
    }

    @Test
    void findAllMapsEntitiesToResponses() {
        PaymentTariff tariff = new PaymentTariff(1L, 1, 100, new BigDecimal("0.50"));
        when(repository.findAll()).thenReturn(List.of(tariff));

        List<TariffRangeResponse> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUnitFee()).isEqualByComparingTo("0.50");
    }

    @Test
    void findAllReturnsEmptyListWhenNoTariffsExist() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.findAll()).isEmpty();
    }
}
