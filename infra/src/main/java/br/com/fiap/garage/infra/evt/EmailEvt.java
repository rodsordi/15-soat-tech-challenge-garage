package br.com.fiap.garage.infra.evt;

import br.com.fiap.garage.infra.def.EmailDef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter(onMethod_ = @Override)
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
@Builder
public class EmailEvt implements EmailDef.Request {

    private String recipient;
    private String subject;
    private String message;
}
