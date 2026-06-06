package ec.edu.espe.banquito.banquitotariffservice.service;

import ec.edu.espe.banquito.banquitotariffservice.dto.TariffRangeResponse;
import ec.edu.espe.banquito.banquitotariffservice.model.PaymentTariff;
import ec.edu.espe.banquito.banquitotariffservice.repository.PaymentTariffRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TariffQueryService {

    private final PaymentTariffRepository repository;

    public TariffQueryService(PaymentTariffRepository repository) {
        this.repository = repository;
    }

    public List<TariffRangeResponse> findAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private TariffRangeResponse toResponse(PaymentTariff tariff) {
        TariffRangeResponse response = new TariffRangeResponse();
        response.setId(tariff.getId());
        response.setMinTransactions(tariff.getMinTransactions());
        response.setMaxTransactions(tariff.getMaxTransactions());
        response.setUnitFee(tariff.getUnitFee());
        response.setDescription(tariff.getDescription());
        return response;
    }
}
