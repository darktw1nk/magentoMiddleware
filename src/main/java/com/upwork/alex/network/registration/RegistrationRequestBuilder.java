package com.upwork.alex.network.registration;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class RegistrationRequestBuilder {
    private @NotEmpty String firstname;
    private String middlename = "";
    private @NotEmpty String lastname;
    private @NotEmpty @Email String email;
    private @NotEmpty @Size(min = 6, max = 256) String password;
    private Boolean subscribed = false;

    public RegistrationRequestBuilder setFirstname(@NotEmpty String firstname) {
        this.firstname = firstname;
        return this;
    }

    public RegistrationRequestBuilder setMiddlename(String middlename) {
        this.middlename = middlename;
        return this;
    }

    public RegistrationRequestBuilder setLastname(@NotEmpty String lastname) {
        this.lastname = lastname;
        return this;
    }

    public RegistrationRequestBuilder setEmail(@NotEmpty @Email String email) {
        this.email = email;
        return this;
    }

    public RegistrationRequestBuilder setPassword(@NotEmpty @Size(min = 6, max = 256) String password) {
        this.password = password;
        return this;
    }

    public RegistrationRequestBuilder setSubscribed(Boolean subscribed) {
        this.subscribed = subscribed;
        return this;
    }

    public RegistrationRequest createRegistrationRequest() {
        return new RegistrationRequest(firstname, middlename, lastname, email, password, subscribed);
    }
}