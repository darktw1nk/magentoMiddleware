package com.upwork.alex.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MagentoConfiguration {

    @Value("${magento.url.base}")
    private String baseUrl;

    @Value("${magento.url.registration.form}")
    private String formUrl;

    @Value("${magento.url.registration.request}")
    private String registrationRequestUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getFormUrl() {
        return formUrl;
    }

    public void setFormUrl(String formUrl) {
        this.formUrl = formUrl;
    }

    public String getRegistrationRequestUrl() {
        return registrationRequestUrl;
    }

    public void setRegistrationRequestUrl(String registrationRequestUrl) {
        this.registrationRequestUrl = registrationRequestUrl;
    }
}
