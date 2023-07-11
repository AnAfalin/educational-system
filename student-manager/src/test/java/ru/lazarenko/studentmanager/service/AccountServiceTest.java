package ru.lazarenko.studentmanager.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import ru.lazarenko.model.dto.ResponseDto;
import ru.lazarenko.studentmanager.entity.Account;
import ru.lazarenko.studentmanager.entity.Student;
import ru.lazarenko.studentmanager.exception.NoFoundElementException;
import ru.lazarenko.studentmanager.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
class AccountServiceTest {
    @Autowired
    AccountService underTest;

    @MockBean
    AccountRepository accountRepository;

    @Test
    @DisplayName("top up balance | NoFoundElementException | account does not exist")
    void topUpBalance_noFoundElementException_accountDoesNotExist() {
        when(accountRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NoFoundElementException.class, () -> underTest.topUpBalanceById(1, new BigDecimal(150_000)));

        verify(accountRepository, times(1)).findById(anyInt());
        verify(accountRepository, times(0)).save(any());
    }

    @Test
    @WithMockUser
    @DisplayName("top up balance | correct return response | account exist")
    void topUpBalance_statusIsOk_accountExist() {
        BigDecimal sum = new BigDecimal(150_000);

        Account account = Account.builder()
                .id(1)
                .balance(new BigDecimal(0))
                .student(new Student())
                .build();

        when(accountRepository.findById(anyInt()))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(any(Account.class)))
                .thenReturn(account);

        ResponseDto result = underTest.topUpBalanceById(1, sum);

        verify(accountRepository, times(1)).findById(anyInt());
        verify(accountRepository, times(1)).save(any());

        assertThat(result.getStatus()).isEqualTo("OK");
        assertThat(result.getMessage()).isEqualTo("The account balance with id ='1' has been successfully increased by 150000. Current balance: 150000");
    }

    @Test
    @WithMockUser
    @DisplayName("top up balance | status is not found | account does not exist")
    void decreaseBalanceByAccountId_statusIsNotFound_accountDoesNotExist(){
        when(accountRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NoFoundElementException.class, () -> underTest.topUpBalanceById(1, new BigDecimal(150_000)));

        verify(accountRepository, times(1)).findById(anyInt());
        verify(accountRepository, times(0)).save(any());
    }

    @Test
    @WithMockUser
    @DisplayName("top up balance | status is ok | account  exist")
    void decreaseBalanceByAccountId_statusIsOk_accountExist() {
        BigDecimal sum = new BigDecimal(150_000);

        Account account = Account.builder()
                .id(1)
                .balance(new BigDecimal(200_000))
                .student(new Student())
                .build();

        when(accountRepository.findById(anyInt()))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(any(Account.class)))
                .thenReturn(account);

        ResponseDto result = underTest.decreaseBalanceByAccountId(1, sum);

        verify(accountRepository, times(1)).findById(anyInt());
        verify(accountRepository, times(1)).save(any());

        assertThat(result.getStatus()).isEqualTo("OK");
        assertThat(result.getMessage()).isEqualTo("The account balance with id ='1' has been successfully increased by 150000. Current balance: 50000");
    }
}