package com.myroslav.cosmickitties.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CosmicWordValidator.class)
@Documented
public @interface CosmicWordCheck {
    String message() default "product name must contain a cosmic term (e.g. star, galaxy, comet, cosmic, nebula, lunar, solar, space, astro, planet, almighty)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
