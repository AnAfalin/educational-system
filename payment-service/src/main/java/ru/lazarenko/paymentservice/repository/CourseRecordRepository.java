package ru.lazarenko.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.lazarenko.paymentservice.entity.CourseRecord;

public interface CourseRecordRepository  extends JpaRepository<CourseRecord, Integer> {
}
