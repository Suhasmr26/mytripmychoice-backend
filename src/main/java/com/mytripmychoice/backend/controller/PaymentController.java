package com.mytripmychoice.backend.controller;

import com.mytripmychoice.backend.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data) throws Exception {
        RazorpayClient client = new RazorpayClient(keyId, keySecret);
        JSONObject options = new JSONObject();
        int amount = (int) data.get("amount");
        options.put("amount", amount);
        options.put("currency", "INR");
        options.put("receipt", "receipt_" + System.currentTimeMillis());
        Order order = client.orders.create(options);
        return ResponseEntity.ok(order.toJson().toString());
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
            @RequestBody Map<String, String> data,
            @RequestHeader("Authorization") String token) {
        return paymentService.verifyAndAddCredits(data, token);
    }
}