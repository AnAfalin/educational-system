package ru.lazarenko.securitymanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.lazarenko.securitymanager.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(value = "select u from User u left join fetch u.roles where u.username=:username")
    Optional<User> findByUsername(String username);
}