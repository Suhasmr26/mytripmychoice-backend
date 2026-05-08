package com.mytripmychoice.backend.service;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.mytripmychoice.backend.dto.TripRequest;
import java.util.*;

@Service
public class AIService {

    private String apiKey = "AIzaSyCakRVlBhEZ32JvXlHLieyfLfGPDAB2EaY";

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

            String url = "https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent?key=" + apiKey;

            Map<String, Object> body = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, String> part = new HashMap<>();
            part.put("text", prompt.toString());
            content.put("parts", List.of(part));
            body.put("contents", List.of(content));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = rest.postForEntity(url, entity, Map.class);

            List<Map> candidates = (List<Map>) response.getBody().get("candidates");
            Map firstCandidate = candidates.get(0);
            Map contentResponse = (Map) firstCandidate.get("content");
            List<Map> parts = (List<Map>) contentResponse.get("parts");
            return (String) parts.get(0).get("text");

        } catch (Exception e) {
            System.out.println("Gemini Error: " + e.getMessage());
            throw new RuntimeException("AI generation failed: " + e.getMessage());
        }
    }
}
