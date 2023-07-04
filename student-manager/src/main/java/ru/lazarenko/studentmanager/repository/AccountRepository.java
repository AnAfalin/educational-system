package ru.lazarenko.studentmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lazarenko.studentmanager.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {

}