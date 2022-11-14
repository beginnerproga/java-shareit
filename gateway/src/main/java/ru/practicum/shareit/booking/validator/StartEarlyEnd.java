package ru.practicum.shareit.booking.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateStartAndEndValidator.class)
@Documented
public @interface StartEarlyEnd {

    String message() default "{StartEarlyEnd.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
