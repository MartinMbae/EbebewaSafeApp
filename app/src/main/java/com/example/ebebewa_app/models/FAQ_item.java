package com.example.ebebewa_app.models;

public class FAQ_item {
    String title, message;

    public FAQ_item(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }


    public String getMessage() {
        return message;
    }

}
