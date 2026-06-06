package ec.edu.espe.banquito.banquitotariffservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "service_charge")
public class ServiceCharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "batch_id", nullable = false)
    private String batchId;

    @Column(name = "successful_tx", nullable = false)
    private Integer successfulTx;

    @Column(name = "unit_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitFee;

    @Column(name = "commission_subtotal", nullable = false, precision = 14, scale = 2)
    private BigDecimal commissionSubtotal;

    @Column(name = "iva_rate", nullable = false, precision = 4, scale = 2)
    private BigDecimal ivaRate;

    @Column(name = "iva_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal ivaAmount;

    @Column(name = "total_charge", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalCharge;

    @Column(name = "tariff_range_applied", nullable = false)
    private String tariffRangeApplied;

    @Column(name = "calculated_at", nullable = false)
    private Instant calculatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public Integer getSuccessfulTx() {
        return successfulTx;
    }

    public void setSuccessfulTx(Integer successfulTx) {
        this.successfulTx = successfulTx;
    }

    public BigDecimal getUnitFee() {
        return unitFee;
    }

    public void setUnitFee(BigDecimal unitFee) {
        this.unitFee = unitFee;
    }

    public BigDecimal getCommissionSubtotal() {
        return commissionSubtotal;
    }

    public void setCommissionSubtotal(BigDecimal commissionSubtotal) {
        this.commissionSubtotal = commissionSubtotal;
    }

    public BigDecimal getIvaRate() {
        return ivaRate;
    }

    public void setIvaRate(BigDecimal ivaRate) {
        this.ivaRate = ivaRate;
    }

    public BigDecimal getIvaAmount() {
        return ivaAmount;
    }

    public void setIvaAmount(BigDecimal ivaAmount) {
        this.ivaAmount = ivaAmount;
    }

    public BigDecimal getTotalCharge() {
        return totalCharge;
    }

    public void setTotalCharge(BigDecimal totalCharge) {
        this.totalCharge = totalCharge;
    }

    public String getTariffRangeApplied() {
        return tariffRangeApplied;
    }

    public void setTariffRangeApplied(String tariffRangeApplied) {
        this.tariffRangeApplied = tariffRangeApplied;
    }

    public Instant getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(Instant calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
}
