package ru.lazarenko.studentmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.lazarenko.studentmanager.dto.BalanceOperationDto;
import ru.lazarenko.studentmanager.dto.ResponseDto;
import ru.lazarenko.studentmanager.service.AccountService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/{id}/top-up")
    public ResponseDto topUpBalance(@Valid @RequestBody BalanceOperationDto request, @PathVariable Integer id) {
        return accountService.topUpBalanceById(id, request.getSum());
    }

    @PostMapping("/{id}/decrease")
    public void decreaseBalanceByAccountId(@Valid @RequestBody BalanceOperationDto request, @PathVariable Integer id) {
        accountService.decreaseBalanceByAccountId(id, request.getSum());
    }

}
