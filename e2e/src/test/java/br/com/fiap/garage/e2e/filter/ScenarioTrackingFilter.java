package br.com.fiap.garage.e2e.filter;

import br.com.fiap.garage.e2e.context.ScenarioTestContext;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class ScenarioTrackingFilter implements Filter {

    private final ScenarioTestContext context;

    public ScenarioTrackingFilter(ScenarioTestContext context) {
        this.context = context;
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext filterContext) {
        Response response = filterContext.next(requestSpec, responseSpec);
        if (context != null) {
            context.recordRequest(requestSpec.getMethod(), requestSpec.getURI(), response.getStatusCode());
        }
        return response;
    }
}