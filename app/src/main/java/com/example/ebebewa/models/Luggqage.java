package com.example.ebebewa.models;

/**
 * Created by Martin Mbae on 14,June,2020.
 */
public class Luggqage {

    private String id, luggage_nature, charge, status;

    public Luggqage(String id, String luggage_nature, String charge, String status) {
        this.id = id;
        this.luggage_nature = luggage_nature;
        this.charge = charge;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getLuggage_nature() {
        return luggage_nature;
    }

    public String getCharge() {
        return charge;
    }

    public String getStatus() {
        return status;
    }
}
