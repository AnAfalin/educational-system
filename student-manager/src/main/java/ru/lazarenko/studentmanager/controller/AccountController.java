package ru.lazarenko.studentmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.lazarenko.model.dto.account.BalanceOperationDto;
import ru.lazarenko.model.dto.ResponseDto;
import ru.lazarenko.studentmanager.service.AccountService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/{id}/top-up")
    public ResponseDto topUpBalanceByAccountId(@Valid @RequestBody BalanceOperationDto request, @PathVariable Integer id) {
        return accountService.topUpBalanceById(id, request.getSum());
    }

    @PostMapping("/{id}/decrease")
    public ResponseDto decreaseBalanceByAccountId(@Valid @RequestBody BalanceOperationDto request, @PathVariable Integer id) {
        return accountService.decreaseBalanceByAccountId(id, request.getSum());
    }

}
