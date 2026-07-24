package br.com.fiap.garage.infra.mapper;

import br.com.fiap.commons.exception.FieldNotFoundException;
import br.com.fiap.commons.exception.InternalErrorException;
import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.entity.*;
import br.com.fiap.garage.infra.evt.EmailEvt;
import br.com.fiap.garage.infra.evt.NotificationEvt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.text.NumberFormat.getCurrencyInstance;
import static org.apache.commons.lang3.StringUtils.replaceEach;

@Slf4j
@Component
public class NotificationEvtMapper {

    private static final NumberFormat NF = getCurrencyInstance(Locale.of("pt", "BR"));

    @Value("${web.garage-web-page-url}")
    private String garageWebPageUrl;

    @Value("${email.estimate-customer-approval-email-subject}")
    private String estimateCustomerApprovalEmailSubject;

    @Value("${email.body-template-file-name}")
    private String emailBodyTemplateFileName;

    public NotificationEvt convert(WorkOrder workOrder) {
        if (workOrder == null)
            return null;

        var workOrderId = Optional.of(workOrder)
                .map(WorkOrder::getId)
                .orElseThrow(() -> new FieldNotFoundException(WorkOrder.class, "id"));

        var customer = Optional.of(workOrder)
                .map(WorkOrder::getVehicle)
                .map(Vehicle::getCustomer)
                .orElseThrow(() -> new ResourceNotFoundException(Customer.class));

        var recipient = Optional.of(customer)
                .map(User::getEmail)
                .orElseThrow(() -> new FieldNotFoundException(Customer.class, "email"));

        var emailBody = buildEmailBody(workOrder);
        log.info(emailBody);

        return NotificationEvt.builder()
                .externalId(workOrderId)
                .email(EmailEvt.builder()
                        .recipient(recipient)
                        .subject(estimateCustomerApprovalEmailSubject)
                        .message(emailBody)
                        .build())
                .build();
    }

    private String buildEmailBody(WorkOrder workOrder) {
        var workOrderId = Optional.of(workOrder)
                .map(WorkOrder::getId)
                .orElseThrow(() -> new FieldNotFoundException(WorkOrder.class, "id"));

        var customer = Optional.of(workOrder)
                .map(WorkOrder::getVehicle)
                .map(Vehicle::getCustomer)
                .orElseThrow(() -> new ResourceNotFoundException(Customer.class));

        var customerName = Optional.of(customer)
                .map(User::getName)
                .orElseThrow(() -> new FieldNotFoundException(Customer.class, "name"));

        var employeeName = Optional.of(workOrder)
                .map(WorkOrder::getEmployee)
                .map(User::getName)
                .orElseThrow(() -> new FieldNotFoundException(Employee.class, "name"));

        var services = buildEstimatedServiceList(workOrder.getEstimatedServices());

        var totalAmount = Optional.of(workOrder)
                .map(WorkOrder::getTotalAmount)
                .orElseThrow(() -> new FieldNotFoundException(WorkOrder.class, "totalAmount"));

        var customerUsername = Optional.of(customer)
                .map(User::getUsername)
                .orElseThrow(() -> new FieldNotFoundException(Customer.class, "username"));

        var emailBody = loadEmailFile();

        var map = new HashMap<String, String>();
        map.put("{customerName}", customerName);
        map.put("{services}", services);
        map.put("{garageWebPageUrl}", garageWebPageUrl);
        map.put("{workOrderId}", workOrderId.toString());
        map.put("{employeeName}", employeeName);
        map.put("{totalAmount}", NF.format(totalAmount));
        map.put("{customerUsername}", customerUsername);

        return replaceEach(emailBody, map.keySet().toArray(new String[0]), map.values().toArray(new String[0]));
    }

    private String buildEstimatedServiceList(Set<EstimatedService> estimatedServices) {
        var services = estimatedServices.stream()
                .map(es -> {
                    var result = MessageFormat.format("\t\t\t\t\t<li>{0}: {1} - {2}</li>",
                            es.getName(),
                            NF.format(es.getCost()),
                            es.getDescription());
                    if (!CollectionUtils.isEmpty(es.getEstimatedMaterials()))
                        result +=  "\n" + buildEstimatedMaterialList(es.getEstimatedMaterials());
                    return result;
                })
                .collect(Collectors.joining("\n"));
        return MessageFormat.format("\t\t\t\t<ul>\n{0}\n\t\t\t\t</ul>", services);
    }

    private String buildEstimatedMaterialList(Set<EstimatedMaterial> estimatedMaterials) {
        var materials = estimatedMaterials.stream()
                .map(em -> MessageFormat.format("\t\t\t\t\t\t<li>{0}: {1} - {2}</li>",
                        em.getName(),
                        NF.format(em.getCost()),
                        em.getDescription()))
                .collect(Collectors.joining("\n"));
        return MessageFormat.format("\t\t\t\t\t<ul>\n{0}\n\t\t\t\t\t<</ul>", materials);
    }

    private String loadEmailFile() {
        var is = ResourceLoader.class.getResourceAsStream(emailBodyTemplateFileName);
        if (is == null)
            throw new InternalErrorException(emailBodyTemplateFileName + " not found!");
        return new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));
    }
}
