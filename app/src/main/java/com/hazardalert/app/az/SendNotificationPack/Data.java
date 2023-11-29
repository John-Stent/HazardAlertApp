package com.hazardalert.app.az.SendNotificationPack;

public class Data {
    private String title;
    private String message;


    public Data() {
    }

    public Data(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
