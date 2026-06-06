package ec.edu.espe.banquito.banquitotariffservice.exception;

public class TariffNotFoundException extends RuntimeException {

    public TariffNotFoundException(String message) {
        super(message);
    }
}
