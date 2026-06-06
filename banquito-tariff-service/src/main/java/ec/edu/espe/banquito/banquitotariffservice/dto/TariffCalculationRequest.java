package ec.edu.espe.banquito.banquitotariffservice.dto;

public class TariffCalculationRequest {

    private Integer successfulTx;
    private String batchId;

    public TariffCalculationRequest() {
    }

    public Integer getSuccessfulTx() {
        return successfulTx;
    }

    public void setSuccessfulTx(Integer successfulTx) {
        this.successfulTx = successfulTx;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
}
