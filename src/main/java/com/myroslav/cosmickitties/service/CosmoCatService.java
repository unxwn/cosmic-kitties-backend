package com.myroslav.cosmickitties.service;

import com.myroslav.cosmickitties.feature.FeatureToggle;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Example service - returns a list of Cosmo Cats.
 * Protected by the feature toggle 'cosmo-cats' (property: feature.cosmo-cats.enabled).
 */
@Service
public class CosmoCatService {

    @FeatureToggle("cosmo-cats")
    public List<String> getCosmoCats() {
        return List.of("Rocket the Voyager", "Stella the Starclimber", "Nebula");
    }
}
