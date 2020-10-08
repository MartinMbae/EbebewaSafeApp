package com.example.ebebewa.fragments.available_jobs;

/**
 * Created by Martin Mbae on 23,June,2020.
 */
public class BidJob {

    private String location_description, description, origin_place, destination_place, amount, post_date, due_date, post, status, id, user, driver, created_date, bid_status, driver_confirm, client_approve, delivery_status, client_receipt_confirmation;

    public BidJob(String location_description, String description, String origin_place, String destination_place, String amount, String post_date, String due_date, String post, String status, String id, String user, String driver, String created_date, String bid_status, String driver_confirm, String client_approve, String delivery_status, String client_receipt_confirmation) {
        this.location_description = location_description;
        this.description = description;
        this.origin_place = origin_place;
        this.destination_place = destination_place;
        this.amount = amount;
        this.post_date = post_date;
        this.due_date = due_date;
        this.post = post;
        this.status = status;
        this.id = id;
        this.user = user;
        this.driver = driver;
        this.created_date = created_date;
        this.bid_status = bid_status;
        this.driver_confirm = driver_confirm;
        this.client_approve = client_approve;
        this.delivery_status = delivery_status;
        this.client_receipt_confirmation = client_receipt_confirmation;
    }

    public String getLocation_description() {
        return location_description;
    }

    public String getDescription() {
        return description;
    }

    public String getOrigin_place() {
        return origin_place;
    }

    public String getDestination_place() {
        return destination_place;
    }

    public String getAmount() {
        return amount;
    }

    public String getPost_date() {
        return post_date;
    }

    public String getDue_date() {
        return due_date;
    }

    public String getBid_status() {
        return bid_status;
    }

    public String getDriver_confirm() {
        return driver_confirm;
    }

    public String getClient_approve() {
        return client_approve;
    }

    public String getDelivery_status() {
        return delivery_status;
    }

    public String getClient_receipt_confirmation() {
        return client_receipt_confirmation;
    }

    public String getPost() {
        return post;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getDriver() {
        return driver;
    }

    public String getCreated_date() {
        return created_date;
    }
}
