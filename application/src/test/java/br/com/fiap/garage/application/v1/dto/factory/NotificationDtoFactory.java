package br.com.fiap.garage.application.v1.dto.factory;

import br.com.fiap.garage.application.v1.dto.NotificationDto;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.application.v1.dto.factory.EmailDtoFactory.*;
import static java.util.UUID.fromString;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class NotificationDtoFactory {

    public static Request create_NotificationDto_Request() {
        return new Request(NotificationDto.Request.builder());
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Request {

        private final NotificationDto.Request.RequestBuilder builder;

        public NotificationDto.Request withAllFields() {
            var result = builder
                    // Self
                    .externalId(fromString("c38d7c09-e064-43ea-ba87-f7ff76113e34"))
                    // Composition
                    .email(create_EmailDto_Request().withAllFields())
                    .build();
            // And
            assertThatObject(result)
                    .hasNoEmptyFields();
            return result;
        }

        public NotificationDto.Request valid() {
            return builder
                    .email(create_EmailDto_Request().valid())
                    .build();
        }

        public NotificationDto.Request initiatedEmpty() {
            return builder.build();
        }
    }

    public static Response create_NotificationDto_Response() {
        return new Response(NotificationDto.Response.builder());
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Response {

        private final NotificationDto.Response.ResponseBuilder builder;

        public NotificationDto.Response withAllFields() {
            var result = builder
                    // Self
                    .id(fromString("a7c8e9d0-1234-4567-890a-bcdef1234567"))
                    .externalId(fromString("da5c2284-23e6-4957-b66a-2aaa52b56645"))
                    // Composition
                    .email(create_EmailDto_Response().withAllFields())
                    // Inheritance (AuditableEntity context)
                    .createdAt(newDateTime("27/04/2026 10:00:00"))
                    .updatedAt(newDateTime("27/04/2026 11:30:00"))
                    .build();
            // And
            assertThatObject(result)
                    .hasNoEmptyFields();
            return result;
        }

        public NotificationDto.Response valid() {
            return builder
                    .id(fromString("a7c8e9d0-1234-4567-890a-bcdef1234567"))
                    .email(create_EmailDto_Response().valid())
                    .build();
        }

        public NotificationDto.Response initiatedEmpty() {
            return builder.build();
        }
    }

    public static Representation create_NotificationDto_Representation() {
        return new Representation(NotificationDto.Representation.builder());
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Representation {

        private final NotificationDto.Representation.RepresentationBuilder builder;

        public NotificationDto.Representation withAllFields() {
            var result = builder
                    // Self
                    .id(fromString("a7c8e9d0-1234-4567-890a-bcdef1234567"))
                    .externalId(fromString("1a4cf8fc-8dfa-4ba5-9ee0-9a787e113caf"))
                    // Composition
                    .email(create_EmailDto_Representation().withAllFields())
                    // Inheritance (AuditableEntity context)
                    .createdAt(newDateTime("27/04/2026 10:00:00"))
                    .build();
            // And
            assertThatObject(result)
                    .hasNoEmptyFields();
            return result;
        }

        public NotificationDto.Representation valid() {
            return builder
                    .id(fromString("a7c8e9d0-1234-4567-890a-bcdef1234567"))
                    .email(create_EmailDto_Representation().valid())
                    .build();
        }

        public NotificationDto.Representation initiatedEmpty() {
            return builder.build();
        }
    }
}