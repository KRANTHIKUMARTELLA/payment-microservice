package com.ecommerce.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name="order-service")
public interface OrderClient {
}
