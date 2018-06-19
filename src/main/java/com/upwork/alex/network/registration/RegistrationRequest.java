package com.upwork.alex.network.registration;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class RegistrationRequest {
    @NotEmpty
    private String firstname;
    private String middlename;
    @NotEmpty
    private String lastname;
    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    @Size(min = 6,max = 256)
    private String password;
    private Boolean subscribed = false;

    public RegistrationRequest(@NotEmpty String firstname, String middlename, @NotEmpty String lastname, @NotEmpty @Email String email, @NotEmpty @Size(min = 6, max = 256) String password, Boolean subscribed) {
        this.firstname = firstname;
        this.middlename = middlename;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.subscribed = subscribed;
    }

    public RegistrationRequest() {
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Boolean subscribed) {
        this.subscribed = subscribed;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{ ")
                .append(firstname)
                .append(", ")
                .append(middlename)
                .append(", ")
                .append(lastname)
                .append(", ")
                .append(email)
                .append(", ")
                .append(password)
                .append(", ")
                .append(subscribed)
                .append(" }");
        return builder.toString();
    }
}
