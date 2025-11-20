package com.myroslav.cosmickitties.feature;

import com.myroslav.cosmickitties.service.CosmoCatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = "features.cosmo-cats.enabled=false")
class CosmoCatServiceFeatureOffTest {

    @Autowired
    private CosmoCatService cosmoCatService;

    @Test
    void getCosmoCats_whenFeatureDisabled_throwsFeatureNotAvailable() {
        assertThatThrownBy(() -> cosmoCatService.getCosmoCats())
                .isInstanceOf(FeatureNotAvailableException.class)
                .hasMessageContaining("cosmo-cats");
    }
}
