package com.example.ebebewa_app.models;

/**
 * Created by Martin Mbae on 04,June,2020.
 */
public class CoverageRegions {

    private String id, city_name, status;

    public CoverageRegions(String id, String city_name, String status) {
        this.id = id;
        this.city_name = city_name;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getCity_name() {
        return city_name;
    }

    public String getStatus() {
        return status;
    }
}
