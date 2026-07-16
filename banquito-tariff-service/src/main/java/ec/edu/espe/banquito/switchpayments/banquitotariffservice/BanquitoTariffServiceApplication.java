package ec.edu.espe.banquito.switchpayments.banquitotariffservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BanquitoTariffServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BanquitoTariffServiceApplication.class, args);
		System.out.println("CI/CD backend validation: tariff-service started");
	}

}
