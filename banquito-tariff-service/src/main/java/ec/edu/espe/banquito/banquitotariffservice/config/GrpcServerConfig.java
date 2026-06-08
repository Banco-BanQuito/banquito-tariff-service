package ec.edu.espe.banquito.banquitotariffservice.config;

import ec.edu.espe.banquito.banquitotariffservice.grpc.TariffGrpcServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Component
public class GrpcServerConfig implements SmartLifecycle {

    private final int port;
    private final TariffGrpcServiceImpl tariffGrpcService;
    private Server server;
    private boolean running;

    public GrpcServerConfig(
            @Value("${grpc.server.port:9090}") int port,
            TariffGrpcServiceImpl tariffGrpcService) {
        this.port = port;
        this.tariffGrpcService = tariffGrpcService;
    }

    @Override
    public void start() {
        try {
            server = ServerBuilder.forPort(port)
                    .addService(tariffGrpcService)
                    .build()
                    .start();
            running = true;
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo iniciar el servidor gRPC en el puerto " + port, exception);
        }
    }

    @Override
    public void stop() {
        if (server != null) {
            server.shutdown();
        }
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
