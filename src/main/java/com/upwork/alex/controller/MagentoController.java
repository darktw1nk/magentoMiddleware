package com.upwork.alex.controller;


import com.upwork.alex.network.registration.RegistrationRequest;
import com.upwork.alex.network.registration.RegistrationResponce;
import com.upwork.alex.service.MagentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class MagentoController {
    private static final Logger logger = LoggerFactory.getLogger(MagentoController.class);

    @Autowired
    MagentoService magentoService;

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<RegistrationResponce> register(@Valid @RequestBody RegistrationRequest request) {
        logger.debug(request.toString());

        String result;
        HttpStatus status;

        try {
            boolean registered = magentoService.createAccount(request);
            if (registered) result = "success";
            else result = "error";

            status = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("", e);
            result = "internal error";
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        logger.debug("request completed");
        return new ResponseEntity<>(new RegistrationResponce(result), status);
    }
}
