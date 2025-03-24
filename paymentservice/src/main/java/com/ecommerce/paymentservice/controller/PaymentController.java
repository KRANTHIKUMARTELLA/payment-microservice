package com.ecommerce.paymentservice.controller;
import com.ecommerce.paymentservice.dto.PaymentRequest;
import com.ecommerce.paymentservice.dto.PaymentResponse;
import com.ecommerce.paymentservice.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

//    @PostMapping
//    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
//        return ResponseEntity.ok(paymentService.processPayment(request));
//    }

    @PostMapping("/checkout")
    public ResponseEntity<PaymentResponse> checkoutProducts(@RequestBody PaymentRequest productRequest) {
        PaymentResponse stripeResponse = paymentService.checkoutProducts(productRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stripeResponse);
    }
}
