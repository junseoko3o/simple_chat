package com.simple.chat.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.stream.IntStream;

@SpringBootTest
@AutoConfigureMockMvc
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    @WithMockUser
    public void setUp() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/coupon/init"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    public void testJoinQueue() throws Exception {
        IntStream.range(0, 200).forEach(i -> {
            String email = "user" + i + "@test.com";

            try {
                mockMvc.perform(MockMvcRequestBuilders.post("/coupon/join")
                                .param("email", email))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(result -> {
                            String response = result.getResponse().getContentAsString();
                            System.out.println(response);
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testIssueCoupon() throws Exception {
        for (int i = 0; i < 100; i++) {
            mockMvc.perform(MockMvcRequestBuilders.post("/coupon/issue"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(result -> {
                        String response = result.getResponse().getContentAsString();
                        System.out.println(response);
                    });
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/coupon/position")
                        .param("email", "user10@test.com"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    System.out.println(response);
                });
    }
}