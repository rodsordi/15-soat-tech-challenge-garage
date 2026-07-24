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

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import static br.com.fiap.garage.application.v1.dto.factory.EmployeeDtoFactory.create_EmployeeDto_Request;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.core.env.Profiles.of;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static software.amazon.awssdk.services.sqs.model.QueueAttributeName.QUEUE_ARN;

@Slf4j
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
        var requestBody = create_EmployeeDto_Request()
                .withAllFields();
        setField(requestBody, "username", "admin@garage.com");
        setField(requestBody, "cpf", "904.434.710-10");
        setField(requestBody, "email", "admin@garage.com");
        setField(requestBody, "password", "abcd1234");

        var response = given()
                .log().all()
                .contentType(JSON)
                .body(json.writeValueAsString(requestBody))
                .post("/v1/employees")
                .then()
                .log().all()
                .extract()
                .response();
        assertThat(response.statusCode())
                .isEqualTo(201);

        response = given()
                .log().all()
                .contentType(JSON)
                .body("""
                        {
                          "username": "admin@garage.com",
                          "password": "abcd1234"
                        }
                        """)
                .post("/auth/login")
                .then()
                .log().all()
                .extract()
                .response();
        assertThat(response.statusCode()).isEqualTo(200);
        authorization = MessageFormat.format("Bearer {0}", response.jsonPath().getString("token"));
    }
}
