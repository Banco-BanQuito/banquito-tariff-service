package ec.edu.espe.banquito.banquitotariffservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;

@Entity
@Table(name = "payment_tariff")
public class PaymentTariff {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "min_successful_tx", nullable = false)
    private Integer minTransactions;

    @Column(name = "max_successful_tx", nullable = false)
    private Integer maxTransactions;

    @Column(name = "unit_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitFee;

    public PaymentTariff() {
    }

    public PaymentTariff(Long id, Integer minTransactions, Integer maxTransactions,
            BigDecimal unitFee) {
        this.id = id;
        this.minTransactions = minTransactions;
        this.maxTransactions = maxTransactions;
        this.unitFee = unitFee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMinTransactions() {
        return minTransactions;
    }

    public void setMinTransactions(Integer minTransactions) {
        this.minTransactions = minTransactions;
    }

    public Integer getMaxTransactions() {
        return maxTransactions;
    }

    public void setMaxTransactions(Integer maxTransactions) {
        this.maxTransactions = maxTransactions;
    }

    public BigDecimal getUnitFee() {
        return unitFee;
    }

    public void setUnitFee(BigDecimal unitFee) {
        this.unitFee = unitFee;
    }

    @Transient
    public String getDescription() {
        return "Rango " + minTransactions + "-" + maxTransactions + " transacciones";
    }
}
