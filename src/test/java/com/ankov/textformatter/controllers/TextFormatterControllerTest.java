package com.ankov.textformatter.controllers;

import com.ankov.textformatter.exceptions.BadRequestException;
import com.ankov.textformatter.services.FormatterService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TextFormatterController.class)
class TextFormatterControllerTest {

    @MockBean
    private FormatterService formatterService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getHtml() throws Exception {
        when(formatterService.getHtml(null)).thenReturn("");

        mockMvc.perform(MockMvcRequestBuilders
        .get("/html"))
        .andExpect(status().isOk());
    }

    @Test
    void inputText() throws Exception {

        when(formatterService.inputText("test", 1)).thenReturn("test");
        when(formatterService.inputText("test", 2)).thenThrow(new BadRequestException("2"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/tf").content("test").param("code", "1"))
                .andExpect(status().isOk());

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/tf").content("test").param("code", "2"));

        result.andExpect(status().isBadRequest());
        String msg = result.andReturn().getResolvedException().getMessage();

        Assertions.assertEquals("2", msg);
    }
}
