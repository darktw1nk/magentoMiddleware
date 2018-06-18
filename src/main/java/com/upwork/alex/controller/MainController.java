package com.upwork.alex.controller;

import com.sun.deploy.net.HttpResponse;
import org.apache.http.Header;
import org.apache.http.ProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.cookie.Cookie;
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

import java.io.IOException;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MainController {

    private final static Logger logger = LoggerFactory.getLogger(MainController.class);

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object register(@RequestParam(value = "firstname", required = true) String firstname,
                           @RequestParam(value = "middlename", required = false, defaultValue = "") String middlename,
                           @RequestParam(value = "lastname", required = true) String lastname,
                           @RequestParam(value = "email", required = true) String email,
                           @RequestParam(value = "password", required = true) String password,
                           @RequestParam(value = "subscribed", required = false, defaultValue = "false") Boolean subscribed) {
        String result = "error";
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
            map.add("firstname", firstname);
            map.add("middlename", middlename);
            map.add("lastname", lastname);
            map.add("email", email);
            map.add("password", password);
            map.add("confirmation", password);
            map.add("is_subscribed", subscribed ? "1" : "0");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            logger.debug(request.toString());

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
                                for(Header header : setCookieHeaders){
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
            ResponseEntity<String> response = restTemplate.postForEntity("http://54.172.134.28/customer/account/createpost/", request, String.class);

            String responseHtml = response.getBody();
            logger.debug(responseHtml);
            if (responseHtml.contains("Welcome, " + firstname)) {
                result = "success";
            } else {
                result = "error";
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        logger.debug("request completed");

        return result;
    }
}
