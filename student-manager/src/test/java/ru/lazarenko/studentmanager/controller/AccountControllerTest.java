package ru.lazarenko.studentmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.lazarenko.model.dto.account.BalanceOperationDto;
import ru.lazarenko.model.dto.ResponseDto;
import ru.lazarenko.studentmanager.exception.NoFoundElementException;
import ru.lazarenko.studentmanager.service.AccountService;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    AccountService accountService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("top up balance | status is not found | account does not exist")
    void topUpBalance_statusIsNotFound_accountDoesNotExist() throws Exception {
        BalanceOperationDto balanceOperationDto = new BalanceOperationDto();
        balanceOperationDto.setSum(new BigDecimal(150_000));

        doThrow(NoFoundElementException.class)
                .when(accountService)
                .topUpBalanceById(anyInt(), any(BigDecimal.class));

        mvc.perform(post("/api/accounts/1/top-up")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(balanceOperationDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").doesNotExist());
    }

    @Test
    @WithMockUser
    @DisplayName("top up balance | status is ok | account  exist")
    void topUpBalance_statusIsOk_accountExist() throws Exception {
        BalanceOperationDto balanceOperationDto = new BalanceOperationDto();
        balanceOperationDto.setSum(new BigDecimal(150_000));

        ResponseDto responseDto = ResponseDto.builder()
                .status(HttpStatus.OK.name())
                .message("The account balance with id ='%s' has been successfully increased by %s. Current balance: %s"
                        .formatted(1, balanceOperationDto.getSum(), balanceOperationDto.getSum()))
                .build();

        when(accountService.topUpBalanceById(anyInt(), any(BigDecimal.class)))
                .thenReturn(responseDto);

        mvc.perform(post("/api/accounts/1/top-up")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(balanceOperationDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("The account balance with id ='1' has been successfully increased by 150000. Current balance: 150000"));
    }

    @Test
    @WithMockUser
    @DisplayName("top up balance | status is not found | account does not exist")
    void decreaseBalanceByAccountId_statusIsNotFound_accountDoesNotExist() throws Exception {
        BalanceOperationDto balanceOperationDto = new BalanceOperationDto();
        balanceOperationDto.setSum(new BigDecimal(150_000));

        doThrow(NoFoundElementException.class)
                .when(accountService)
                .decreaseBalanceByAccountId(anyInt(), any(BigDecimal.class));

        mvc.perform(post("/api/accounts/1/decrease")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(balanceOperationDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").doesNotExist());
    }

    @Test
    @WithMockUser
    @DisplayName("top up balance | status is ok | account  exist")
    void decreaseBalanceByAccountId_statusIsOk_accountExist() throws Exception {
        BalanceOperationDto balanceOperationDto = new BalanceOperationDto();
        balanceOperationDto.setSum(new BigDecimal(150_000));

        ResponseDto responseDto = ResponseDto.builder()
                .status(HttpStatus.OK.name())
                .message("The account balance with id ='%s' has been successfully increased by %s. Current balance: 50000"
                        .formatted(1, balanceOperationDto.getSum()))
                .build();

        when(accountService.decreaseBalanceByAccountId(anyInt(), any()))
                .thenReturn(responseDto);

        mvc.perform(post("/api/accounts/1/decrease")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(balanceOperationDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("The account balance with id ='1' has been successfully increased by 150000. Current balance: 50000"));
    }
}