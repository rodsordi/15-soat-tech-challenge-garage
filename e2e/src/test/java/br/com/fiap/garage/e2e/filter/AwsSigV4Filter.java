package br.com.fiap.garage.e2e.filter;

import br.com.fiap.garage.e2e.config.E2eConfig;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.Region;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AwsSigV4Filter implements Filter {

    private final String serviceName;
    private final Region region;
    private final AwsCredentialsProvider credentialsProvider;

    public AwsSigV4Filter() {
        this("lambda", E2eConfig.getProperty("aws.region", "us-east-1"));
    }

    public AwsSigV4Filter(String serviceName, String region) {
        this.serviceName = serviceName;
        this.region = Region.of(region);
        this.credentialsProvider = buildCredentialsProvider();
    }

    private AwsCredentialsProvider buildCredentialsProvider() {
        var profileName = E2eConfig.getProperty("aws.profile", "default");
        var customPath = E2eConfig.getProperty("aws.credentials.path", null);

        try {
            if (customPath != null && !customPath.isBlank()) {
                var profileFile = ProfileFile.builder()
                        .content(Paths.get(customPath))
                        .type(ProfileFile.Type.CREDENTIALS)
                        .build();

                return ProfileCredentialsProvider.builder()
                        .profileFile(profileFile)
                        .profileName(profileName)
                        .build();
            }

            return DefaultCredentialsProvider.builder()
                    .profileName(profileName)
                    .build();
        } catch (Exception e) {
            System.err.println("[WARN] Failed to initialize ProfileCredentialsProvider: " + e.getMessage());
            return DefaultCredentialsProvider.create();
        }
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {

        try {
            var uri = URI.create(requestSpec.getURI());
            var method = SdkHttpMethod.fromValue(requestSpec.getMethod());

            byte[] bodyBytes = new byte[0];
            if (requestSpec.getBody() != null) {
                var rawBody = requestSpec.getBody();
                if (rawBody instanceof byte[]) {
                    bodyBytes = (byte[]) rawBody;
                } else {
                    bodyBytes = rawBody.toString().getBytes(StandardCharsets.UTF_8);
                }
            }

            final byte[] finalBodyBytes = bodyBytes;

            var sdkRequestBuilder = SdkHttpFullRequest.builder()
                    .method(method)
                    .uri(uri)
                    .contentStreamProvider(() -> new ByteArrayInputStream(finalBodyBytes));

            // Copy existing headers
            requestSpec.getHeaders().forEach(h -> sdkRequestBuilder.putHeader(h.getName(), h.getValue()));

            var sdkRequest = sdkRequestBuilder.build();
            var credentials = credentialsProvider.resolveCredentials();

            var signerParams = Aws4SignerParams.builder()
                    .awsCredentials(credentials)
                    .signingName(serviceName)
                    .signingRegion(region)
                    .build();

            var signedRequest = Aws4Signer.create().sign(sdkRequest, signerParams);

            // Inject signed AWS headers into REST Assured request
            signedRequest.headers().forEach((name, values) -> {
                if (name.equalsIgnoreCase("Authorization")
                        || name.toLowerCase().startsWith("x-amz-")) {
                    requestSpec.header(name, String.join(",", values));
                }
            });

        } catch (Exception e) {
            System.err.println("[WARN] AwsSigV4Filter failed to sign request: " + e.getMessage());
        }

        return ctx.next(requestSpec, responseSpec);
    }
}
