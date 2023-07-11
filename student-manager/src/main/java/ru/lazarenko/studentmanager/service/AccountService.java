package ru.lazarenko.studentmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lazarenko.model.dto.ResponseDto;
import ru.lazarenko.studentmanager.entity.Account;
import ru.lazarenko.studentmanager.exception.NoFoundElementException;
import ru.lazarenko.studentmanager.repository.AccountRepository;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public ResponseDto topUpBalanceById(Integer id, BigDecimal sum) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NoFoundElementException("Account with id='%s' not found"));

        account.setBalance(account.getBalance().add(sum));

        Account savedAccount = accountRepository.save(account);
        return ResponseDto.builder()
                .status(HttpStatus.OK.name())
                .message("The account balance with id ='%s' has been successfully increased by %s. Current balance: %s"
                        .formatted(account.getId(), sum, savedAccount.getBalance()))
                .build();
    }

    @Transactional
    public ResponseDto decreaseBalanceByAccountId(Integer id, BigDecimal sum) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NoFoundElementException("Account with id='%s' not found"));

        account.setBalance(account.getBalance().subtract(sum));

        Account savedAccount = accountRepository.save(account);
        return ResponseDto.builder()
                .status(HttpStatus.OK.name())
                .message("The account balance with id ='%s' has been successfully increased by %s. Current balance: %s"
                        .formatted(account.getId(), sum, savedAccount.getBalance()))
                .build();
    }
}