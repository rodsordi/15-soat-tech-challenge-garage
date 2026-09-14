package br.com.fiap.garage.e2e.aws;

import br.com.fiap.garage.e2e.config.E2eConfig;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;

public final class AwsEndpointResolver {

    private static final String SSM_API_GATEWAY_PARAM = "/garage/api-gateway/url";
    private static final String SSM_LAMBDA_AUTH_PARAM = "/garage/lambda/auth-url";

    private static String cachedGarageBaseUri;
    private static String cachedLambdaAuthUrl;

    private AwsEndpointResolver() {
    }

    public static synchronized String resolveGarageBaseUri() {
        if (cachedGarageBaseUri != null && !cachedGarageBaseUri.isBlank()) {
            return cachedGarageBaseUri;
        }

        cachedGarageBaseUri = resolveSsmParameter(SSM_API_GATEWAY_PARAM, null);
        return cachedGarageBaseUri;
    }

    public static synchronized String resolveLambdaAuthUrl() {
        if (cachedLambdaAuthUrl != null && !cachedLambdaAuthUrl.isBlank()) {
            return cachedLambdaAuthUrl;
        }

        cachedLambdaAuthUrl = resolveSsmParameter(SSM_LAMBDA_AUTH_PARAM, null);
        return cachedLambdaAuthUrl;
    }

    public static String resolveSsmParameter(String paramName, String defaultValue) {
        var regionName = E2eConfig.getProperty("aws.region", "us-east-1");
        try (SsmClient ssm = SsmClient.builder()
                .region(Region.of(regionName))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {

            var response = ssm.getParameter(GetParameterRequest.builder()
                    .name(paramName)
                    .build());

            var value = response.parameter().value();
            System.out.println("[INFO] Discovered SSM Parameter '" + paramName + "': " + value);
            return value;
        } catch (Exception e) {
            System.err.println("[WARN] Failed to resolve SSM Parameter '" + paramName + "' from AWS: " + e.getMessage());
            return defaultValue;
        }
    }

    public static synchronized void clearCache() {
        cachedGarageBaseUri = null;
        cachedLambdaAuthUrl = null;
    }
}
