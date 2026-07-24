package br.com.fiap.garage.application.v1.dto.factory;

import br.com.fiap.garage.application.v1.dto.EmailDto;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class EmailDtoFactory {

    public static Request create_EmailDto_Request() {
        return new Request(EmailDto.Request.builder());
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Request {

        private final EmailDto.Request.RequestBuilder builder;

        public EmailDto.Request withAllFields() {
            var result = builder
                    .recipient("customer@example.com")
                    .subject("Service Update")
                    .message("Your vehicle is ready for pickup.")
                    .build();
            // And
            assertThatObject(result)
                    .hasNoEmptyFields();
            return result;
        }

        public EmailDto.Request valid() {
            return builder
                    .recipient("customer@example.com")
                    .subject("Service Update")
                    .message("Your vehicle is ready.")
                    .build();
        }

        public EmailDto.Request initiatedEmpty() {
            return builder.build();
        }
    }

    public static Response create_EmailDto_Response() {
        return new Response(EmailDto.Response.builder());
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Response {

        private final EmailDto.Response.ResponseBuilder builder;

        public EmailDto.Response withAllFields() {
            var result = builder
                    .recipient("customer@example.com")
                    .bcc("archive@garage.com")
                    .subject("Service Update")
                    .message("Your vehicle is ready for pickup.")
                    .createdAt(newDateTime("27/04/2026 10:00:00"))
                    .updatedAt(newDateTime("27/04/2026 10:00:00"))
                    .build();
            // And
            assertThatObject(result)
                    .hasNoEmptyFields();
            return result;
        }

        public EmailDto.Response valid() {
            return builder
                    .recipient("customer@example.com")
                    .subject("Service Update")
                    .build();
        }

        public EmailDto.Response initiatedEmpty() {
            return builder.build();
        }
    }

    public static Representation create_EmailDto_Representation() {
        return new Representation(EmailDto.Representation.builder());
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Representation {

        private final EmailDto.Representation.RepresentationBuilder builder;

        public EmailDto.Representation withAllFields() {
            var result = builder
                    .recipient("customer@example.com")
                    .subject("Service Update")
                    .createdAt(newDateTime("27/04/2026 10:00:00"))
                    .build();
            // And
            assertThatObject(result)
                    .hasNoEmptyFields();
            return result;
        }

        public EmailDto.Representation valid() {
            return builder
                    .recipient("customer@example.com")
                    .subject("Service Update")
                    .build();
        }

        public EmailDto.Representation initiatedEmpty() {
            return builder.build();
        }
    }
}