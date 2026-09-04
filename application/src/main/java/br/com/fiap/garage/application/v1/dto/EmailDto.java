package br.com.fiap.garage.application.v1.dto;

import br.com.fiap.garage.application.adapter.def.EmailDef;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class EmailDto {

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @Schema(name = "Email.Request")
    public static class Request implements EmailDef.Request {

        private String recipient;
        private String subject;
        private String message;
    }

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @Schema(name = "Email.Response")
    public static class Response implements EmailDef.Response {

        private String recipient;
        private String bcc;
        private String subject;
        private String message;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @Schema(name = "Email.Representation")
    public static class Representation implements EmailDef.Representation {

        private String recipient;
        private String subject;
        private LocalDateTime createdAt;
    }
}
