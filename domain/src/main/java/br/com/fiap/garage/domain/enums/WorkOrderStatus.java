package br.com.fiap.garage.domain.enums;

import br.com.fiap.garage.domain.entity.WorkOrder;
import br.com.fiap.garage.domain.entity.work_order.WorkOrderState;
import br.com.fiap.garage.domain.entity.work_order.state.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@Schema(example = "DIAGNOSING",
        description = """
                Work Order status. Owner: self
                * RECEIVED: Work order received by employee.
                * DIAGNOSING: Work order being diagnosed by employee.
                * WAITING_FOR_APPROVAL: Waiting the customer work order approval.
                * EXECUTING: Work order being executed by employee.
                * FINISHED: Work order finished.
                * RELEASED: Vehicle released to the customer.
                """)
@Getter
@RequiredArgsConstructor
public enum WorkOrderStatus {

    RECEIVED(ReceivedState::new),
    DIAGNOSING(DiagnosingState::new),
    WAITING_FOR_APPROVAL(WaitingForApprovalState::new),
    EXECUTING(ExecutingState::new),
    FINISHED(FinishedState::new),
    RELEASED(ReleasedState::new);

    private final Function<WorkOrder, WorkOrderState> state;

    public static Expression<Integer> ordinalExpression(Path<WorkOrderStatus> path, CriteriaBuilder cb) {
        var statusCase = cb.<Integer>selectCase();
        for (int i = 0; i < values().length; i++)
            statusCase = statusCase.when(cb.equal(path, values()[i]), i);
        return statusCase.otherwise(values().length);
    }
}
