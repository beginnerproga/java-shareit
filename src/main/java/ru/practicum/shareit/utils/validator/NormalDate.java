package ru.practicum.shareit.utils.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

    @Target({ElementType.TYPE, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = NormalDateValidator.class)
    @Documented
    public @interface NormalDate {

        String message() default "{NormalDate.invalid}";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

    }

