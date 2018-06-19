package com.upwork.alex.controller;


import com.upwork.alex.network.registration.RegistrationRequest;
import com.upwork.alex.network.registration.RegistrationResponce;
import org.apache.http.Header;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.*;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.net.HttpCookie;
import java.util.List;

@RestController
public class MainController {

    private final static Logger logger = LoggerFactory.getLogger(MainController.class);

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RegistrationResponce register(@Valid @RequestBody RegistrationRequest request) {
        String result = "error";
        logger.debug(request.toString());
        String url = "http://54.172.134.28/customer/account/create/";

        try {
            Connection.Response jsoupResponce = Jsoup.connect(url).execute();
            // Document doc = Jsoup.connect(url).get();
            Document doc = jsoupResponce.parse();
            Elements formKeyElements = doc.select("input[name='form_key']");
            Element formKeyElement = formKeyElements.first();
            logger.debug(formKeyElement.html());
            String formKey = formKeyElement.attr("value");
            String frontendCookie = jsoupResponce.cookie("frontend");
            logger.debug("form key: " + formKey);
            logger.debug("frontend cookie: " + frontendCookie);
            result = formKey;


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("Cookie", "frontend=" + frontendCookie);

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
                            //   System.out.println(response);
                            int statusCode = response.getStatusLine().getStatusCode();
                            // If redirect intercept intermediate response.
                            if (statusCode == 302) {
                                logger.debug("redirect");
                                String redirectURL = response.getFirstHeader("Location").getValue();
                                logger.debug("redirectURL: " + redirectURL);

                                Header[] setCookieHeaders = response.getHeaders("Set-Cookie");
                                StringBuilder cookie = new StringBuilder();
                                for (Header header : setCookieHeaders) {
                                    List<HttpCookie> parcedCookie = HttpCookie.parse(header.getValue());
                                    cookie.append(parcedCookie.get(0).getName()).append("=").append(parcedCookie.get(0).getValue()).append(";");
                                    logger.debug(header.getValue());
                                    logger.debug(header.getName());
                                }
                                logger.debug(cookie.toString());
                                request.removeHeaders("Cookie");
                                //  request.setHeader("Cookie",cookie.toString());

                                return true;
                            }
                            return false;
                        }
                    })
                    .build();
            /*CookieStore cookieStore = new BasicCookieStore();
            RequestConfig requestConfig = RequestConfig
                    .custom()
                    .setCookieSpec(CookieSpecs.STANDARD)
                    .build();

            CloseableHttpClient httpClient = HttpClientBuilder.create()
                    .setRedirectStrategy(new LaxRedirectStrategy())
                    .setDefaultRequestConfig(requestConfig)
                    .setDefaultCookieStore(cookieStore)
                    .build();*/
            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

            RestTemplate restTemplate = new RestTemplate(factory);
            ResponseEntity<String> response = restTemplate.postForEntity("http://54.172.134.28/customer/account/createpost/", magentoRequest, String.class);

            String responseHtml = response.getBody();
            if (responseHtml.contains("Welcome, " + request.getFirstname())) {
                result = "success";
            } else {
                result = "error";
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        logger.debug("request completed");

        return new RegistrationResponce(result);
    }
}
