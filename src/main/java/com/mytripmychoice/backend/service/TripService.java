package com.mytripmychoice.backend.service;

import com.mytripmychoice.backend.dto.TripRequest;
import com.mytripmychoice.backend.model.Trip;
import com.mytripmychoice.backend.model.User;
import com.mytripmychoice.backend.repository.TripRepository;
import com.mytripmychoice.backend.repository.UserRepository;
import com.mytripmychoice.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AIService aiService;

    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<?> generate(TripRequest request, String token) {
        try {
            // Extract user from token
            String cleanToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.extractUserId(cleanToken);

            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "User not found!"));
            }

            User user = userOpt.get();

            // Check credits
            if (user.getCredits() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "No credits left! Please buy more."));
            }

            // Generate plan using AI
            String plan = aiService.generateTripPlan(request);

            // Save trip
            Trip trip = new Trip();
            trip.setUserId(userId);
            trip.setTripType(request.getTripType());
            trip.setDestination(request.getDestination());
            trip.setBudget(request.getBudget());
            trip.setTravelMode(request.getTravelMode());
            trip.setPersonsCount(request.getMembers().size());
            trip.setGeneratedPlan(plan);
            tripRepository.save(trip);

            // Deduct credit
            user.setCredits(user.getCredits() - 1);
            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("plan", plan);
            response.put("creditsLeft", user.getCredits());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Failed to generate plan: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> getUserTrips(String token) {
        String cleanToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(cleanToken);

        List<Trip> trips = tripRepository.findByUserId(userId);
        return ResponseEntity.ok(trips);
    }
}