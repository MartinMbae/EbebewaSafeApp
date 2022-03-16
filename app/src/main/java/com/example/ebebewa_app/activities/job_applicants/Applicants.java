package com.example.ebebewa_app.activities.job_applicants;

/**
 * Created by Martin Mbae on 14,June,2020.
 */
public class Applicants {

    private String id, user, driver, post, status, created_date, first_name, last_name, passport_photo, phone, vehicle_type, plate_number, payment_mode, type, amount, driver_confirm, client_approve;

    public Applicants(String id, String user, String driver, String post, String status, String created_date, String first_name, String last_name, String passport_photo, String phone, String vehicle_type, String plate_number, String payment_mode, String type, String amount, String driver_confirm, String client_approve) {
        this.id = id;
        this.user = user;
        this.driver = driver;
        this.post = post;
        this.status = status;
        this.created_date = created_date;
        this.first_name = first_name;
        this.last_name = last_name;
        this.passport_photo = passport_photo;
        this.phone = phone;
        this.vehicle_type = vehicle_type;
        this.plate_number = plate_number;
        this.payment_mode = payment_mode;
        this.type = type;
        this.amount = amount;
        this.driver_confirm = driver_confirm;
        this.client_approve = client_approve;
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

    public String getPost() {
        return post;
    }

    public String getStatus() {
        return status;
    }

    public String getCreated_date() {
        return created_date;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getPassport_photo() {
        return passport_photo;
    }

    public String getPhone() {
        return phone;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public String getPlate_number() {
        return plate_number;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public String getType() {
        return type;
    }

    public String getAmount() {
        return amount;
    }

    public String getDriver_confirm() {
        return driver_confirm;
    }

    public String getClient_approve() {
        return client_approve;
    }
}
