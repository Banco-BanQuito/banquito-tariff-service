package ec.edu.espe.banquito.switchpayments.banquitotariffservice.controller;

import ec.edu.espe.banquito.switchpayments.banquitotariffservice.dto.TariffCalculationResponse;
import ec.edu.espe.banquito.switchpayments.banquitotariffservice.dto.TariffRangeResponse;
import ec.edu.espe.banquito.switchpayments.banquitotariffservice.service.TariffCalculationService;
import ec.edu.espe.banquito.switchpayments.banquitotariffservice.service.TariffQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TariffControllerTest {

    @Mock
    private TariffCalculationService calculationService;

    @Mock
    private TariffQueryService queryService;

    private TariffController controller;

    @BeforeEach
    void setUp() {
        controller = new TariffController(calculationService, queryService);
    }

    @Test
    void calculateDelegatesToCalculationService() {
        TariffCalculationResponse expected = new TariffCalculationResponse();
        when(calculationService.calculate(50, "BATCH-1")).thenReturn(expected);

        ResponseEntity<TariffCalculationResponse> response = controller.calculate(50, "BATCH-1");

        assertThat(response.getBody()).isSameAs(expected);
    }

    @Test
    void findRangesDelegatesToQueryService() {
        List<TariffRangeResponse> expected = List.of(new TariffRangeResponse());
        when(queryService.findAll()).thenReturn(expected);

        ResponseEntity<List<TariffRangeResponse>> response = controller.findRanges();

        assertThat(response.getBody()).isSameAs(expected);
    }
}
