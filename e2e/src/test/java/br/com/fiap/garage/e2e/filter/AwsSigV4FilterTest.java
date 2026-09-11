package br.com.fiap.garage.e2e.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("AWS SigV4 Filter Tests")
class AwsSigV4FilterTest {

    @Test
    @DisplayName("Should initialize AwsSigV4Filter with default lambda service and region")
    void shouldInitializeWithDefaults() {
        assertThatCode(AwsSigV4Filter::new).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should initialize AwsSigV4Filter with custom service and region")
    void shouldInitializeWithCustomParams() {
        var filter = new AwsSigV4Filter("lambda", "us-east-1");
        assertThat(filter).isNotNull();
    }
}
