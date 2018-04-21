package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;

public interface NewSessionTicket extends Struct {

    int NONCE_LENGTH_BYTES = 1;
    int TICKET_LENTGH_BYTES = 2;
    int EXTENSIONS_LENGTH_BYTES = 2;
}
