package com.upwork.alex.service;

import com.upwork.alex.configuration.MagentoConfiguration;
import com.upwork.alex.network.registration.RegistrationRequest;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class MagentoService {
    private static final Logger logger = LoggerFactory.getLogger(MagentoService.class);

    @Autowired
    MagentoConfiguration configuration;

    public boolean createAccount(RegistrationRequest request) {
        try {
            ImmutablePair<String, String> formAndFrontendCookie = getFormKeyAndFrontendCookie();
            String response = makeExternalRegistrationRequest(request, formAndFrontendCookie.getKey(), formAndFrontendCookie.getValue());

            return response.contains("Welcome, " + request.getFirstname());
        } catch (IOException e) {
            logger.error("", e);
            return false;
        }
    }

    private String makeExternalRegistrationRequest(RegistrationRequest request, String formKey, String frontendCookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Cookie", frontendCookie);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("form_key", formKey);
        map.add("firstname", request.getFirstname());
        map.add("middlename", request.getMiddlename());
        map.add("lastname", request.getLastname());
        map.add("email", request.getEmail());
        map.add("password", request.getPassword());
        map.add("confirmation", request.getPassword());
        map.add("is_subscribed", request.getSubscribed() ? "1" : "0");

        HttpEntity<MultiValueMap<String, String>> magentoRequest = new HttpEntity<>(map, headers);
        logger.debug(magentoRequest.toString());

        CloseableHttpClient httpClient = HttpClientBuilder
                .create()
                .setRedirectStrategy(new DefaultRedirectStrategy() {
                    @Override
                    public boolean isRedirected(org.apache.http.HttpRequest request, org.apache.http.HttpResponse response, HttpContext context) throws ProtocolException {
                        int statusCode = response.getStatusLine().getStatusCode();
                        // If redirect intercept intermediate response.
                        if (statusCode == 302) {
                            logger.debug("redirect");
                            String redirectURL = response.getFirstHeader("Location").getValue();
                            logger.debug("redirectURL: " + redirectURL);
                            request.removeHeaders("Cookie");
                            return true;
                        }
                        return false;
                    }
                })
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(factory);
        ResponseEntity<String> response = restTemplate.postForEntity(configuration.getRegistrationRequestUrl(), magentoRequest, String.class);

        return response.getBody();
    }

    private ImmutablePair<String, String> getFormKeyAndFrontendCookie() throws IOException {
        Connection.Response jsoupResponce = Jsoup.connect(configuration.getFormUrl()).execute();
        Document doc = jsoupResponce.parse();

        Elements formKeyElements = doc.select("input[name='form_key']");
        Element formKeyElement = formKeyElements.first();

        String formKey = formKeyElement.attr("value");
        String frontendCookie = jsoupResponce.cookie("frontend");

        logger.debug("form key: " + formKey);
        logger.debug("frontend cookie: " + frontendCookie);

        return new ImmutablePair<>(formKey, "frontend=" + frontendCookie);
    }
}
