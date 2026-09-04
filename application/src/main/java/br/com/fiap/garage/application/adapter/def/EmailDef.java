package br.com.fiap.garage.application.adapter.def;

import br.com.fiap.commons.def.AuditableDef;

/**
 * | dto            | Request     | Response             |
 * | Representation | Represented | RepresentedPersisted |
 * | Detailed       | Detailed    | DetailedPersisted    |
 */
public interface EmailDef {

    interface Represented {

        String getRecipient();

        String getSubject();
    }

    interface Detailed extends Represented {

        String getMessage();
    }

    interface RepresentedPersisted extends AuditableDef.RepresentedPersisted {


    }

    interface DetailedPersisted extends AuditableDef.DetailedPersisted, RepresentedPersisted {

        String getBcc();
    }

    interface Request extends Detailed {

    }

    interface Response extends Detailed, DetailedPersisted {

    }

    interface Representation extends Represented, RepresentedPersisted {

    }
}
