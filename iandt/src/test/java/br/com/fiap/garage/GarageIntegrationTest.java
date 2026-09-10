package br.com.fiap.garage;

import br.com.fiap.commons.iandt.setup.LocalStackSetup;
import br.com.fiap.commons.iandt.setup.PostgresSetup;
import io.awspring.cloud.sns.core.SnsTemplate;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.data.repository.CrudRepository;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import tools.jackson.databind.json.JsonMapper;

import br.com.fiap.garage.config.SecurityTestConfig;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.springframework.core.env.Profiles.of;
import static software.amazon.awssdk.services.sqs.model.QueueAttributeName.QUEUE_ARN;

@Slf4j
@Import(SecurityTestConfig.class)
public abstract class GarageIntegrationTest implements PostgresSetup, LocalStackSetup {

    @Autowired
    private Environment env;

    @LocalServerPort
    private Integer port;

    @Autowired
    private List<CrudRepository<?, ?>> repositories;

    @Autowired
    protected JsonMapper json;

    @Autowired
    private SnsClient snsClient;

    @Autowired
    private SqsAsyncClient sqsAsyncClient;

    @Autowired
    private SnsTemplate snsTemplate;

    @Value("${message.notification-creation.topic}")
    private String notificationCreationTopic;

    @Value("${message.notification-creation.queue}")
    private String notificationCreationQueue;

    protected String authorization;

    @BeforeEach
    void beforeEach() {
        RestAssured.baseURI = format("http://localhost:%s/api", port);

        if (env.acceptsProfiles(of("int_test"))) {
            log.info("Deleting all test data");
            for (var i = repositories.size() - 1; i >= 0; i--) {
                var repository = repositories.get(i);
                repository.deleteAll();
            }
        }

        createTopicsAndQueues();

        authenticate();
    }

    private void createTopicsAndQueues() {
        var createTopicRes = snsClient.createTopic(t -> t.name(notificationCreationTopic));
        var topicArn = createTopicRes.topicArn();

        var createQueueRes = sqsAsyncClient.createQueue(q -> q.queueName(notificationCreationQueue)).join();
        var queueUrl = createQueueRes.queueUrl();

        var queueArn = sqsAsyncClient.getQueueAttributes(q -> q.queueUrl(queueUrl)
                .attributeNames(QUEUE_ARN)).join().attributes().get(QUEUE_ARN);

        snsClient.subscribe(s -> s.topicArn(topicArn)
                .protocol("sqs")
                .attributes(Map.of("RawMessageDelivery", "true"))
                .endpoint(queueArn));
    }

    private void authenticate() {
        authorization = "Bearer test-integration-token";
    }
}
