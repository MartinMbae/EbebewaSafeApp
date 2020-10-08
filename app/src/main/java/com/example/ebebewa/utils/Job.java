package com.example.ebebewa.utils;

/**
 * Created by Martin Mbae on 14,June,2020.
 */
public class Job {

    private String id, invoice_no, title, post_by, origin_place, origin_contact, destination_place, destination_contact, vehicle_type,
            luggage_nature, amount, location_description, description, bid_status, driver_confirm, client_approve, delivery_status,
            status, post_date, due_date, dated, payment_status, amount_paid, balance;

    public Job(String id, String invoice_no, String title, String post_by, String origin_place, String origin_contact, String destination_place, String destination_contact, String vehicle_type, String luggage_nature, String amount, String location_description, String description, String bid_status, String driver_confirm, String client_approve, String delivery_status, String status, String post_date, String due_date, String dated, String payment_status, String amount_paid, String balance) {
        this.id = id;
        this.invoice_no = invoice_no;
        this.title = title;
        this.post_by = post_by;
        this.origin_place = origin_place;
        this.origin_contact = origin_contact;
        this.destination_place = destination_place;
        this.destination_contact = destination_contact;
        this.vehicle_type = vehicle_type;
        this.luggage_nature = luggage_nature;
        this.amount = amount;
        this.location_description = location_description;
        this.description = description;
        this.bid_status = bid_status;
        this.driver_confirm = driver_confirm;
        this.client_approve = client_approve;
        this.delivery_status = delivery_status;
        this.status = status;
        this.post_date = post_date;
        this.due_date = due_date;
        this.dated = dated;
        this.payment_status = payment_status;
        this.amount_paid = amount_paid;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public String getInvoice_no() {
        return invoice_no;
    }

    public String getTitle() {
        return title;
    }

    public String getPost_by() {
        return post_by;
    }

    public String getOrigin_place() {
        return origin_place;
    }

    public String getOrigin_contact() {
        return origin_contact;
    }

    public String getDestination_place() {
        return destination_place;
    }

    public String getDestination_contact() {
        return destination_contact;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public String getLuggage_nature() {
        return luggage_nature;
    }

    public String getAmount() {
        return amount;
    }

    public String getLocation_description() {
        return location_description;
    }

    public String getDescription() {
        return description;
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

    public String getStatus() {
        return status;
    }

    public String getPost_date() {
        return post_date;
    }

    public String getDue_date() {
        return due_date;
    }

    public String getDated() {
        return dated;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public String getAmount_paid() {
        return amount_paid;
    }

    public String getBalance() {
        return balance;
    }
}
