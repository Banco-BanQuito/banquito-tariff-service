package ec.edu.espe.banquito.banquitotariffservice.dto;

import java.math.BigDecimal;

public class TariffCalculationResponse {

    private Integer successfulTx;
    private BigDecimal unitFee;
    private BigDecimal commissionSubtotal;
    private BigDecimal ivaRate;
    private BigDecimal ivaAmount;
    private BigDecimal totalCharge;
    private String tariffRangeApplied;

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

}
