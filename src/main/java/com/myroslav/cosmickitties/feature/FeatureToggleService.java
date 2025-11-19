package com.myroslav.cosmickitties.feature;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service responsible for reading feature toggle values.
 */
@Service
public class FeatureToggleService {

    private final FeatureProperties properties;

    public FeatureToggleService(FeatureProperties properties) {
        this.properties = properties;
    }

    /**
     * Check whether a feature is enabled. The annotation provides keys like "cosmo-cats"
     * and this method looks for feature.<key>.enabled.
     *
     * If a feature is missing in properties - treat as disabled (safe default).
     */
    public boolean isEnabled(String featureKey) {
        Map<String, Map<String, Object>> features = properties;
        if (features == null) return false;
        Map<String, Object> featureNode = features.get(featureKey);
        if (featureNode == null) return false;
        Object val = featureNode.get("enabled");
        if (val instanceof Boolean) return (Boolean) val;
        if (val instanceof String) return Boolean.parseBoolean((String) val);
        return false;
    }
}
