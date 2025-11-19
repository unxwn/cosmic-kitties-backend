package com.myroslav.cosmickitties.feature;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeatureToggle {
    /**
     * Logical name of the feature key WITHOUT the 'feature.' prefix and without '.enabled'.
     * Example: for property feature.cosmo-cats.enabled use @FeatureToggle("cosmo-cats")
     */
    String value();
}
