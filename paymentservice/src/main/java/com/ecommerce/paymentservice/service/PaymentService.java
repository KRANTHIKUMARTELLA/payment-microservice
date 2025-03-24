package com.ecommerce.paymentservice.service;
import com.ecommerce.paymentservice.dto.PaymentRequest;
import com.ecommerce.paymentservice.dto.PaymentResponse;

import com.ecommerce.paymentservice.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${stripe.secretKey}")
    private String stripeSecretKey;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

//    public PaymentResponse processPayment(PaymentRequest request) {
//        Stripe.apiKey = stripeSecretKey;
//
//        log.info("Processing payment for Order ID: {}", request.getOrderId());
//
//        try {
//            Map<String, Object> params = new HashMap<>();
//            params.put("amount", (long) (request.getAmount() * 100)); // Convert to cents
//            params.put("currency", "usd");
//            params.put("description", "Payment for Order ID: " + request.getOrderId());
//
//            PaymentIntent intent = PaymentIntent.create(params);
//
//            log.info("PaymentIntent created with ID: {}", intent.getId());
//
//            Payment payment = new Payment();
//            payment.setPaymentId(intent.getId());
//            payment.setOrderId(request.getOrderId());
//            payment.setAmount(request.getAmount());
//            payment.setStatus(intent.getStatus());
//            payment.setCreatedAt(LocalDateTime.now());
//
//            paymentRepository.save(payment);
//
//            log.info("Payment saved successfully with ID: {}", payment.getId());
//
//            PaymentResponse response = new PaymentResponse();
//            response.setPaymentId(intent.getId());
//            response.setStatus(intent.getStatus());
//            response.setMessage("Payment successful");
//
//            return response;
//
//        } catch (StripeException e) {
//            log.error("Stripe API error: {}", e.getMessage());
//            throw new StripeException("Failed to process payment. Please try again later.");
//        } catch (Exception e) {
//            log.error("Unexpected error: {}", e.getMessage());
//            throw new PaymentException("An error occurred while processing payment.");
//        }
//    }

    public PaymentResponse checkoutProducts(PaymentRequest productRequest) {
        // Set your secret key. Remember to switch to your live secret key in production!
        Stripe.apiKey = stripeSecretKey;

        // Create a PaymentIntent with the order amount and currency
        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(productRequest.getName())
                        .build();

        // Create new line item with the above product data and associated price
        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(productRequest.getCurrency() != null ? productRequest.getCurrency() : "USD")
                        .setUnitAmount(productRequest.getAmount())
                        .setProductData(productData)
                        .build();

        // Create new line item with the above price data
        SessionCreateParams.LineItem lineItem =
                SessionCreateParams
                        .LineItem.builder()
                        .setQuantity(productRequest.getQuantity())
                        .setPriceData(priceData)
                        .build();

        // Create new session with the line items
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:8080/success")
                        .setCancelUrl("http://localhost:8080/cancel")
                        .addLineItem(lineItem)
                        .build();

        // Create new session
        Session session = null;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            //log the error
        }

        return PaymentResponse
                .builder()
                .status("SUCCESS")
                .message("Payment session created ")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }
}
