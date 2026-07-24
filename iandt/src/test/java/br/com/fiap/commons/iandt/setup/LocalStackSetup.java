package br.com.fiap.commons.iandt.setup;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.localstack.LocalStackContainer;

import static org.testcontainers.utility.DockerImageName.parse;

public interface LocalStackSetup {

    LocalStackContainer LOCAL_STACK = new LocalStackContainer(parse("localstack/localstack:3.5.0"))
            .withServices("s3", "sqs", "sns")
            .withEnv("SKIP_SSL_CERT_DOWNLOAD", "true");

    @BeforeAll
    static void beforeAllLocalStack() {
        LOCAL_STACK.start();
    }

    @DynamicPropertySource
    static void configurePropertiesLocalstack(DynamicPropertyRegistry registry) {
        // AWS
        registry.add("spring.cloud.aws.region.static", LOCAL_STACK::getRegion);
        registry.add("spring.cloud.aws.credentials.access-key", LOCAL_STACK::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", LOCAL_STACK::getSecretKey);
        // AWS_SQS
        registry.add("spring.cloud.aws.sqs.endpoint", () -> LOCAL_STACK.getEndpoint().toString());
        registry.add("spring.cloud.aws.sqs.account", () -> "000000000000");
        // AWS_SQS
        registry.add("spring.cloud.aws.sns.endpoint", () -> LOCAL_STACK.getEndpoint().toString());
        registry.add("spring.cloud.aws.sns.account", () -> "000000000000");
        // AWS_S3
        registry.add("spring.cloud.aws.s3.endpoint", () -> LOCAL_STACK.getEndpoint().toString());
        registry.add("spring.cloud.aws.s3.account", () -> "000000000000");
    }
}
