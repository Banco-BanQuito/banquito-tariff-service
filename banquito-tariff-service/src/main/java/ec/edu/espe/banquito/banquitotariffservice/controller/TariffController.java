package ec.edu.espe.banquito.banquitotariffservice.controller;

import ec.edu.espe.banquito.banquitotariffservice.dto.TariffCalculationResponse;
import ec.edu.espe.banquito.banquitotariffservice.dto.TariffRangeResponse;
import ec.edu.espe.banquito.banquitotariffservice.service.TariffCalculationService;
import ec.edu.espe.banquito.banquitotariffservice.service.TariffQueryService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v2/tariff")
public class TariffController {

    private final TariffCalculationService calculationService;
    private final TariffQueryService queryService;

    public TariffController(TariffCalculationService calculationService, TariffQueryService queryService) {
        this.calculationService = calculationService;
        this.queryService = queryService;
    }

    @GetMapping("/calculate")
    public ResponseEntity<TariffCalculationResponse> calculate(
            @RequestParam("successful_tx") @Min(1) Integer successfulTx,
            @RequestParam @NotBlank String batchId) {

        return ResponseEntity.ok(calculationService.calculate(successfulTx, batchId));
    }

    @GetMapping("/ranges")
    public ResponseEntity<List<TariffRangeResponse>> findRanges() {
        return ResponseEntity.ok(queryService.findAll());
    }
}
