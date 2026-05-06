package com.mytripmychoice.backend.service;

import com.mytripmychoice.backend.model.Payment;
import com.mytripmychoice.backend.model.User;
import com.mytripmychoice.backend.repository.PaymentRepository;
import com.mytripmychoice.backend.repository.UserRepository;
import com.mytripmychoice.backend.security.JwtUtil;
import com.razorpay.RazorpayClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    public ResponseEntity<?> verifyAndAddCredits(Map<String, String> data, String token) {
        try {
            String orderId = data.get("razorpay_order_id");
            String paymentId = data.get("razorpay_payment_id");
            String signature = data.get("razorpay_signature");
            int credits = Integer.parseInt(data.get("credits"));

            // Verify signature
            String payload = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(keySecret.getBytes(), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes());
            String generated = HexFormat.of().formatHex(hash);

            if (!generated.equals(signature)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Payment verification failed!"));
            }

            // Get user from token
            String cleanToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.extractUserId(cleanToken);

            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "User not found!"));
            }

            User user = userOpt.get();

            // Add credits
            user.setCredits(user.getCredits() + credits);
            userRepository.save(user);

            // Save payment record
            Payment payment = new Payment();
            payment.setUserId(userId);
            payment.setRazorpayOrderId(orderId);
            payment.setRazorpayPaymentId(paymentId);
            payment.setCreditsAdded(credits);
            payment.setStatus("SUCCESS");
            paymentRepository.save(payment);

            return ResponseEntity.ok(Map.of(
                    "message", "Credits added successfully!",
                    "creditsLeft", user.getCredits()
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}