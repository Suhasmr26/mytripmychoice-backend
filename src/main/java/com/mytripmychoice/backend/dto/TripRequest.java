package com.mytripmychoice.backend.dto;

import java.util.List;

public class TripRequest {

    private String tripType;
    private String destination;
    private Double budget;
    private String travelMode;
    private List<MemberDto> members;

    public String getTripType() { return tripType; }
    public void setTripType(String tripType) { this.tripType = tripType; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }
    public String getTravelMode() { return travelMode; }
    public void setTravelMode(String travelMode) { this.travelMode = travelMode; }
    public List<MemberDto> getMembers() { return members; }
    public void setMembers(List<MemberDto> members) { this.members = members; }

    public static class MemberDto {
        private String name;
        private int age;
        private String sex;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public String getSex() { return sex; }
        public void setSex(String sex) { this.sex = sex; }
    }
}