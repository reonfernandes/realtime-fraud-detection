package com.reon.titan_backend.actuator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// prometheus scrapes without a token, so this endpoint must stay open
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.data.mongodb.auto-index-creation=false",
        // blank so the admin seeder skips, tests have no mongo running
        "security.admin.email=",
        "security.admin.password="
})
class MetricsEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void prometheusEndpointIsOpen() throws Exception {
        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("jvm_memory_used_bytes")));
    }

    @Test
    void apiDocsAreOpen() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/api/v1/transactions")));
    }

    @Test
    void logoutIsReachableWithoutAToken() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/auth/logout"))
                .andExpect(status().isOk());
    }

    @Test
    void transactionEndpointStillNeedsLogin() throws Exception {
        mockMvc.perform(get("/api/v1/transactions/me"))
                .andExpect(status().isUnauthorized());
    }
}
