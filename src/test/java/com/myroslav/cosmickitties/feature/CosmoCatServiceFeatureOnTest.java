package com.myroslav.cosmickitties.feature;

import com.myroslav.cosmickitties.service.CosmoCatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = "feature.cosmoCats.enabled=true")
class CosmoCatServiceFeatureOnTest {

    @Autowired
    private CosmoCatService cosmoCatService;

    @Test
    void getCosmoCats_whenFeatureEnabled_returnsList() {
        List<String> cats = cosmoCatService.getCosmoCats();
        assertThat(cats).isNotNull().isNotEmpty();
        assertThat(cats).contains("Vanya the Voyager");
    }
}
