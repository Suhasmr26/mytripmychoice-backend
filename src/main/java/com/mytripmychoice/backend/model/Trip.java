package com.mytripmychoice.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String tripType;
    private String destination;
    private Double budget;
    private String travelMode;
    private int personsCount;

    @Column(columnDefinition = "LONGTEXT")
    private String generatedPlan;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTripType() { return tripType; }
    public void setTripType(String tripType) { this.tripType = tripType; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }
    public String getTravelMode() { return travelMode; }
    public void setTravelMode(String travelMode) { this.travelMode = travelMode; }
    public int getPersonsCount() { return personsCount; }
    public void setPersonsCount(int personsCount) { this.personsCount = personsCount; }
    public String getGeneratedPlan() { return generatedPlan; }
    public void setGeneratedPlan(String generatedPlan) { this.generatedPlan = generatedPlan; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}