package ec.edu.espe.banquito.switchpayments.banquitotariffservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HealthControllerTest {

    @Test
    void healthReturnsUpStatus() {
        ResponseEntity<Map<String, Object>> response = new HealthController().health();

        assertThat(response.getBody()).containsEntry("status", "UP");
    }
}
