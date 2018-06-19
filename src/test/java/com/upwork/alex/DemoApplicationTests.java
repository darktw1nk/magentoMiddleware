package com.upwork.alex;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upwork.alex.controller.MagentoController;
import com.upwork.alex.network.registration.RegistrationRequest;
import com.upwork.alex.network.registration.RegistrationRequestBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MagentoController.class)
@AutoConfigureRestDocs(outputDir = "target/restdocs")
public class DemoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    public void contextLoads() {
    }

    @Test
    public void checkRegistrationBadRequest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/register"))
                .andExpect(status().isBadRequest())
                .andDo(document("home"));
    }

    @Test
    public void checkRegistrationSuccess() throws Exception {
        RegistrationRequest request = new RegistrationRequestBuilder()
                .setFirstname("Alex")
                .setMiddlename("Middle")
                .setLastname("Go15")
                .setEmail(System.currentTimeMillis() + "@gmail.com")
                .setSubscribed(true)
                .setPassword("password").createRegistrationRequest();

        this.mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("home", responseFields(fieldWithPath("status").description("represents status of registration operation"))
                        , requestFields(fieldWithPath("firstname").description("firstname of a person"),
                                fieldWithPath("middlename").description("middlename of a person"),
                                fieldWithPath("lastname").description("lastname of a person"),
                                fieldWithPath("email").description("email, would be used as a login"),
                                fieldWithPath("password").description("password for this account"),
                                fieldWithPath("subscribed").description("checks if person subscribed for a receiving news from company"))));
    }

}
