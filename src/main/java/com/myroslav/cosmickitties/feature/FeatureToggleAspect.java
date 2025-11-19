package com.myroslav.cosmickitties.feature;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Aspect that intercepts methods annotated with @FeatureToggle.
 */
@Aspect
@Component
public class FeatureToggleAspect {

    private final FeatureToggleService featureToggleService;

    public FeatureToggleAspect(FeatureToggleService featureToggleService) {
        this.featureToggleService = featureToggleService;
    }

    @Around("@annotation(featureToggle)")
    public Object checkFeature(ProceedingJoinPoint joinPoint, FeatureToggle featureToggle) throws Throwable {
        if (featureToggleService.isEnabled(featureToggle.value())) {
            return joinPoint.proceed();
        } else {
            throw new FeatureNotAvailableException("Feature " + featureToggle.value() + " is not available");
        }
    }
}