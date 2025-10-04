package com.myroslav.cosmickitties.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class CosmicWordValidator implements ConstraintValidator<CosmicWordCheck, String> {

    private static final Set<String> COSMIC_WORDS = Set.of(
            "star", "galaxy", "comet", "cosmic", "nebula", "lunar", "solar", "space", "astro", "planet" //, "almighty"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // @NotNull will handle null case
        String lower = value.toLowerCase();
        return COSMIC_WORDS.stream().anyMatch(lower::contains);
    }
}
