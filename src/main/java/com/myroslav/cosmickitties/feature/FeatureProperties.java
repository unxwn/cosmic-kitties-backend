package com.myroslav.cosmickitties.feature;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "features")
public class FeatureProperties extends HashMap<String, Map<String, Object>> { }