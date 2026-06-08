package ec.edu.espe.banquito.banquitotariffservice.grpc;

import ec.edu.espe.banquito.banquitotariffservice.dto.TariffCalculationResponse;
import ec.edu.espe.banquito.banquitotariffservice.exception.TariffNotFoundException;
import ec.edu.espe.banquito.banquitotariffservice.service.TariffCalculationService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class TariffGrpcServiceImpl extends TariffGrpcServiceGrpc.TariffGrpcServiceImplBase {

    private final TariffCalculationService calculationService;

    public TariffGrpcServiceImpl(TariffCalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @Override
    public void calculateTariff(
            TariffCalculationGrpcRequest request,
            StreamObserver<TariffCalculationGrpcResponse> responseObserver) {

        if (request.getSuccessfulTx() < 1) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("successful_tx debe ser mayor o igual a 1")
                    .asRuntimeException());
            return;
        }

        if (request.getBatchId() == null || request.getBatchId().isBlank()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("batch_id es requerido")
                    .asRuntimeException());
            return;
        }

        try {
            TariffCalculationResponse calculation = calculationService.calculate(
                    request.getSuccessfulTx(),
                    request.getBatchId());

            TariffCalculationGrpcResponse response = TariffCalculationGrpcResponse.newBuilder()
                    .setSuccessfulTx(calculation.getSuccessfulTx())
                    .setUnitFee(calculation.getUnitFee().toPlainString())
                    .setCommissionSubtotal(calculation.getCommissionSubtotal().toPlainString())
                    .setIvaRate(calculation.getIvaRate().toPlainString())
                    .setIvaAmount(calculation.getIvaAmount().toPlainString())
                    .setTotalCharge(calculation.getTotalCharge().toPlainString())
                    .setTariffRangeApplied(calculation.getTariffRangeApplied())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (TariffNotFoundException exception) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(exception.getMessage())
                    .asRuntimeException());
        } catch (RuntimeException exception) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error calculando tarifa")
                    .asRuntimeException());
        }
    }
}
