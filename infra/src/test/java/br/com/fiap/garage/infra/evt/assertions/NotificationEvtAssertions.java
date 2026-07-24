package br.com.fiap.garage.infra.evt.assertions;

import br.com.fiap.garage.infra.evt.NotificationEvt;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.infra.evt.assertions.EmailEvtAssertions.assertThat_EmailEvt;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RequiredArgsConstructor(access = PRIVATE)
public final class NotificationEvtAssertions {

    private final NotificationEvt actual;

    public static NotificationEvtAssertions assertThat_NotificationEvt(NotificationEvt actual) {
        assertThat(actual).isNotNull();
        return new NotificationEvtAssertions(spy(actual));
    }

    /**
     * @see br.com.fiap.garage.domain.entity.factory.WorkOrderFactory
     * .withAllFields()
     */
    public void wasConvertedFrom_WorkOrder() {
        // Self
        assertThat(actual.getExternalId())
                .hasToString("e48ad20c-69dd-4382-b567-0e02b2c3d480");

        // Composition
        assertThat_EmailEvt(actual.getEmail())
                .wasConvertedFrom_WorkOrder();

        // And
        assertThatObject(actual)
                .hasAllGetMethodsVerifiedOnceAtLeast();
    }
}