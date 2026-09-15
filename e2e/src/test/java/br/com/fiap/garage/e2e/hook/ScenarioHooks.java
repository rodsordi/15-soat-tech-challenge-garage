package br.com.fiap.garage.e2e.hook;

import br.com.fiap.garage.e2e.config.E2eConfig;
import br.com.fiap.garage.e2e.context.ScenarioTestContext;
import br.com.fiap.garage.e2e.filter.ScenarioTrackingFilter;
import io.cucumber.java.Before;
import io.restassured.RestAssured;

public class ScenarioHooks {

    private final ScenarioTestContext context;

    public ScenarioHooks(ScenarioTestContext context) {
        this.context = context;
    }

    @Before(order = 0)
    public void setupScenario() {
        context.reset();
        RestAssured.baseURI = E2eConfig.getBaseUri();
        context.setAuthorization(E2eConfig.getAuthToken());
        E2eConfig.configureRestAssuredFilters(new ScenarioTrackingFilter(context));
    }
}