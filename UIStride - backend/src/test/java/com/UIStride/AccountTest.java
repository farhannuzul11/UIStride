package com.UIStride;

import com.UIStride.model.Account;
import com.UIStride.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void testRegisterSuccess() throws Exception {
        Account mockAccount = new Account("testuser", "Password123", "testuser@example.com");
        when(accountService.saveAccount(any(Account.class))).thenReturn(mockAccount);

        mockMvc.perform(post("/account/register")
                        .param("username", "testuser")
                        .param("password", "Password123")
                        .param("email", "testuser@example.com"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Account created successfully"));
    }

    @Test
    void testRegisterInvalidEmail() throws Exception {
        mockMvc.perform(post("/account/register")
                        .param("username", "testuser")
                        .param("password", "Password123")
                        .param("email", "invalid-email"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password format"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        Account account = new Account("testuser", "Password123", "testuser@example.com");
        when(accountService.findAccountByEmail("testuser@example.com"))
                .thenReturn(Optional.of(account));

        mockMvc.perform(post("/account/login")
                        .param("email", "testuser@example.com")
                        .param("password", "Password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    void testLoginAccountNotFound() throws Exception {
        when(accountService.findAccountByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/account/login")
                        .param("email", "nonexistent@example.com")
                        .param("password", "Password123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Account not found"));
    }

    @Test
    void testLoginIncorrectPassword() throws Exception {
        Account account = new Account("testuser", "Password123", "testuser@example.com");
        when(accountService.findAccountByEmail("testuser@example.com"))
                .thenReturn(Optional.of(account));

        mockMvc.perform(post("/account/login")
                        .param("email", "testuser@example.com")
                        .param("password", "wrongpassword"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Incorrect password"));
    }

}
