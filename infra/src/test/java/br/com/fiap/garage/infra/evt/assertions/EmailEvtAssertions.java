package br.com.fiap.garage.infra.evt.assertions;

import br.com.fiap.garage.infra.evt.EmailEvt;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RequiredArgsConstructor(access = PRIVATE)
public final class EmailEvtAssertions {

    private final EmailEvt actual;

    public static EmailEvtAssertions assertThat_EmailEvt(EmailEvt actual) {
        assertThat(actual).isNotNull();
        return new EmailEvtAssertions(spy(actual));
    }

    /**
     * @see br.com.fiap.garage.domain.entity.factory.WorkOrderFactory
     * .withAllFields()
     */
    public void wasConvertedFrom_WorkOrder() {
        // Self
        assertThat(actual.getRecipient())
                .isEqualTo("john.doe@fiap.com.br");
        assertThat(actual.getSubject())
                .isEqualTo("Email subject");
        assertThat(actual.getMessage())
                .isNotBlank();

        // And
        assertThatObject(actual)
                .hasAllGetMethodsVerifiedOnceAtLeast();
    }
}