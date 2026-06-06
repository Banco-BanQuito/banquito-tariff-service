package ec.edu.espe.banquito.banquitotariffservice.service;

import ec.edu.espe.banquito.banquitotariffservice.dto.TariffCalculationResponse;
import ec.edu.espe.banquito.banquitotariffservice.exception.TariffNotFoundException;
import ec.edu.espe.banquito.banquitotariffservice.model.PaymentTariff;
import ec.edu.espe.banquito.banquitotariffservice.model.ServiceCharge;
import ec.edu.espe.banquito.banquitotariffservice.repository.PaymentTariffRepository;
import ec.edu.espe.banquito.banquitotariffservice.repository.ServiceChargeRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TariffCalculationService {

    private static final BigDecimal IVA_RATE = new BigDecimal("0.15");

    private final PaymentTariffRepository tariffRepository;
    private final ServiceChargeRepository chargeRepository;

    public TariffCalculationService(PaymentTariffRepository tariffRepository,
            ServiceChargeRepository chargeRepository) {
        this.tariffRepository = tariffRepository;
        this.chargeRepository = chargeRepository;
    }

    @Transactional
    public TariffCalculationResponse calculate(Integer successfulTx, String batchId) {
        PaymentTariff tariff = tariffRepository
                .findByMinTransactionsLessThanEqualAndMaxTransactionsGreaterThanEqual(
                        successfulTx,
                        successfulTx)
                .orElseThrow(() -> new TariffNotFoundException(
                        "No existe tarifa para el lote " + batchId));

        BigDecimal subtotal = tariff.getUnitFee()
                .multiply(BigDecimal.valueOf(successfulTx))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal iva = subtotal.multiply(IVA_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(iva).setScale(2, RoundingMode.HALF_UP);

        ServiceCharge charge = new ServiceCharge();
        charge.setBatchId(batchId);
        charge.setSuccessfulTx(successfulTx);
        charge.setUnitFee(tariff.getUnitFee());
        charge.setCommissionSubtotal(subtotal);
        charge.setIvaRate(IVA_RATE);
        charge.setIvaAmount(iva);
        charge.setTotalCharge(total);
        charge.setTariffRangeApplied(tariff.getDescription());
        charge.setCalculatedAt(Instant.now());
        chargeRepository.save(charge);

        TariffCalculationResponse response = new TariffCalculationResponse();
        response.setSuccessfulTx(successfulTx);
        response.setUnitFee(tariff.getUnitFee());
        response.setCommissionSubtotal(subtotal);
        response.setIvaRate(IVA_RATE);
        response.setIvaAmount(iva);
        response.setTotalCharge(total);
        response.setTariffRangeApplied(tariff.getDescription());

        return response;
    }
}
