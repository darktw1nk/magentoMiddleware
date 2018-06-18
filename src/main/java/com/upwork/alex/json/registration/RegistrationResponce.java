package com.upwork.alex.json.registration;

public class RegistrationResponce {
    private String status;

    public RegistrationResponce(String status) {
        this.status = status;
    }

    public RegistrationResponce() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
