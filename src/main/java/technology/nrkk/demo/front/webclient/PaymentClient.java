package technology.nrkk.demo.front.webclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import technology.nrkk.demo.front.configs.properties.PaymentProperties;

@Component
public class PaymentClient {
    private final RestTemplate restTemplate;
    private final PaymentProperties properties;
    protected final static Logger logger = LoggerFactory.getLogger(PaymentClient.class);

    @Autowired
    public PaymentClient(RestTemplateBuilder builder, PaymentProperties properties) {
        this.properties = properties;
        this.restTemplate = builder
                .additionalInterceptors((request, body, execution) -> {
                    request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    return execution.execute(request, body);
                })
                .build();
    }
    public void pay(int amount, int customerId, String cardId, String simulate) throws PaymentException {
        try {
            this.tryPay(amount, customerId, cardId, simulate, null);
        } catch (Exception e) {
            if (customerId >= 30) {
                this.tryPay(amount, customerId, cardId, simulate, "legacy");
            }
        }
    }
    private void tryPay(int amount, int customerId, String cardId, String simulate, String provider) throws PaymentException {
        String paymentProvider = "quickpay";
        if (provider != null) {
            paymentProvider = provider;
        } else {
            if (customerId < 10) {
                paymentProvider = "fastpay";
            } else if (customerId < 30) {
                paymentProvider = "stablepay";
            }
        }
        String jsonBody = String.format("{\"amount\": %d, \"customer_id\": %d, \"card_id\": \"%s\", \"simulate\": \"%s\", \"provider\": \"%s\"}", amount, customerId, cardId, simulate, paymentProvider);
        logger.info(jsonBody);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(properties.getUrl() + "/api/payment", entity, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new PaymentException("Payment failed: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new PaymentException("Payment request failed", e);
        }
    }

    public static class PaymentException extends Exception {
        public PaymentException(String message) { super(message); }
        public PaymentException(String message, Throwable cause) { super(message, cause); }
    }
} 