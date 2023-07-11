package ru.lazarenko.studentmanager.service.mapper;

import org.mapstruct.Mapper;
import ru.lazarenko.model.dto.account.AccountDto;
import ru.lazarenko.studentmanager.entity.Account;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    Account toAccount(AccountDto accountDto);

    AccountDto toAccountDto(Account account);
}