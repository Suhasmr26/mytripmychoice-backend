package com.mytripmychoice.backend.controller;

import com.mytripmychoice.backend.dto.TripRequest;
import com.mytripmychoice.backend.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "http://localhost:5173")
public class TripController {

    @Autowired
    private TripService tripService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateTrip(
            @RequestBody TripRequest request,
            @RequestHeader("Authorization") String token) {
        return tripService.generate(request, token);
    }

    @GetMapping("/my-trips")
    public ResponseEntity<?> getMyTrips(
            @RequestHeader("Authorization") String token) {
        return tripService.getUserTrips(token);
    }
}