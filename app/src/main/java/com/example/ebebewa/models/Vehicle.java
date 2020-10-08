package com.example.ebebewa.models;

/**
 * Created by Martin Mbae on 04,June,2020.
 */
public class Vehicle {

    private String id, type, amount_per_km;

    public Vehicle(String id, String type, String amount_per_km) {
        this.id = id;
        this.type = type;
        this.amount_per_km = amount_per_km;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getAmount_per_km() {
        return amount_per_km;
    }
}
