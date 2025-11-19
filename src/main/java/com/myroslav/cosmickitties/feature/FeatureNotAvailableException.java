package com.myroslav.cosmickitties.feature;

public class FeatureNotAvailableException extends RuntimeException {
    public FeatureNotAvailableException(String featureKey) {
        super("Feature '" + featureKey + "' is notf available");
    }
}
