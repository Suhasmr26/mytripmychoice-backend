package com.mytripmychoice.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.mytripmychoice.backend.dto.TripRequest;
import java.util.*;

@Service
public class AIService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    public String generateTripPlan(TripRequest request) {

        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a trip planning assistant. Create a detailed day-by-day trip plan.\n");
        prompt.append("Trip Type: ").append(request.getTripType()).append("\n");
        prompt.append("Destination: ").append(request.getDestination()).append("\n");
        prompt.append("Budget: Rs.").append(request.getBudget()).append("\n");
        prompt.append("Travel Mode: ").append(request.getTravelMode()).append("\n");
        prompt.append("Members:\n");

        for (TripRequest.MemberDto m : request.getMembers()) {
            prompt.append("- ").append(m.getName())
                  .append(", Age: ").append(m.getAge())
                  .append(", Sex: ").append(m.getSex()).append("\n");
        }

        prompt.append("\nIMPORTANT: We only provide trip PLANS not bookings or packages.\n");
        prompt.append("Children below 7 years travel free.\n");
        prompt.append("Include: Day-wise itinerary, estimated costs, food suggestions, places to visit.");

        try {
            RestTemplate rest = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("HTTP-Referer", "http://localhost:5173");
            headers.set("X-Title", "MyTripMyChoice");

            String url = "https://openrouter.ai/api/v1/chat/completions";

            Map<String, Object> body = new HashMap<>();
            body.put("model", "openrouter/free");

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt.toString());
            messages.add(message);
            body.put("messages", messages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = rest.postForEntity(url, entity, Map.class);

            System.out.println("OpenRouter response: " + response.getStatusCode());

            List<Map> choices = (List<Map>) response.getBody().get("choices");
            Map firstChoice = choices.get(0);
            Map messageResponse = (Map) firstChoice.get("message");
            return (String) messageResponse.get("content");

        } catch (Exception e) {
            System.out.println("AI Error: " + e.getMessage());
            throw new RuntimeException("AI generation failed: " + e.getMessage());
        }
    }
}