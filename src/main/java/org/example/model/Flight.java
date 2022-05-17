package org.example.model;

import lombok.Getter;

@Getter
public class Flight {
    public transient String origin;
    public String origin_name;
    public transient String destination;
    public String destination_name;
    public String departure_date;
    public String departure_time;
    public String arrival_date;
    public String arrival_time;
    public transient String carrier;
    public transient int stops;
    public transient int price;
}
