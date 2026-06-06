package ec.edu.espe.banquito.banquitotariffservice.dto;

import java.math.BigDecimal;

public class TariffRangeResponse {

    private Long id;
    private Integer minTransactions;
    private Integer maxTransactions;
    private BigDecimal unitFee;
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
