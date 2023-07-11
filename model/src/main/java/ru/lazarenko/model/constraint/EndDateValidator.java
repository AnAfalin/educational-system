package ru.lazarenko.model.constraint;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class EndDateValidator implements ConstraintValidator<EndDateIsAfterStartDate, Object> {

    private String startDateFieldName;
    private String endDateFieldName;

    @Override
    public void initialize(EndDateIsAfterStartDate constraintAnnotation) {
        startDateFieldName = "startDate";
        endDateFieldName = "endDate";
    }

    public boolean isValid(Object object, ConstraintValidatorContext context) {

        final Object startDateObject = new BeanWrapperImpl(object)
                .getPropertyValue(startDateFieldName);
        final Object endDateObject = new BeanWrapperImpl(object)
                .getPropertyValue(endDateFieldName);

        if (startDateObject == null || endDateObject == null) {
            return true;
        }

        LocalDate startDate = LocalDate.parse(startDateObject.toString());
        LocalDate endDate = LocalDate.parse(endDateObject.toString());

        return endDate.isAfter(startDate);
    }

}