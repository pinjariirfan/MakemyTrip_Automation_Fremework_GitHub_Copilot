package com.makemytrip.models;

import java.util.Objects;

public class FlightDetails implements Comparable<FlightDetails> {
    private String airlineName;
    private String flightCode;
    private String departureTime;
    private String arrivalTime;
    private String duration;
    private String stops;
    private int price;

    public FlightDetails() {
    }

    public FlightDetails(String airlineName, String flightCode, String departureTime,
                         String arrivalTime, String duration, String stops, int price) {
        this.airlineName = airlineName;
        this.flightCode = flightCode;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.stops = stops;
        this.price = price;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void setFlightCode(String flightCode) {
        this.flightCode = flightCode;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStops() {
        return stops;
    }

    public void setStops(String stops) {
        this.stops = stops;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public int compareTo(FlightDetails other) {
        return Integer.compare(this.price, other.price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlightDetails that = (FlightDetails) o;
        return price == that.price &&
                Objects.equals(airlineName, that.airlineName) &&
                Objects.equals(departureTime, that.departureTime) &&
                Objects.equals(arrivalTime, that.arrivalTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(airlineName, departureTime, arrivalTime, price);
    }

    @Override
    public String toString() {
        return String.format(
                "Airline: %-15s | Code: %-10s | Dep: %-6s | Arr: %-6s | Duration: %-8s | Stops: %-10s | Price: ₹%,d",
                airlineName != null ? airlineName : "N/A",
                flightCode != null && !flightCode.isEmpty() ? flightCode : "N/A",
                departureTime != null ? departureTime : "N/A",
                arrivalTime != null ? arrivalTime : "N/A",
                duration != null ? duration : "N/A",
                stops != null ? stops : "N/A",
                price
        );
    }
}
