package com.upwork.alex.network.registration;

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
