package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.OCSPStatusRequest;
import com.gypsyengineer.tlsbunny.tls13.struct.ResponderID;
import com.gypsyengineer.tlsbunny.utils.Utils;

import java.io.IOException;

public class OCSPStatusRequestImpl implements OCSPStatusRequest {

    private Vector<ResponderID> responder_id_list;
    private Vector<Byte> extensions;

    OCSPStatusRequestImpl(Vector<ResponderID> responder_id_list, Vector<Byte> extensions) {
        this.responder_id_list = responder_id_list;
        this.extensions = extensions;
    }

    @Override
    public Vector<ResponderID> getResponderIdList() {
        return responder_id_list;
    }

    @Override
    public Vector<Byte> getExtensions() {
        return extensions;
    }

    @Override
    public int encodingLength() {
        return Utils.getEncodingLength(responder_id_list, extensions);
    }

    @Override
    public byte[] encoding() throws IOException {
        return Utils.encoding(responder_id_list, extensions);
    }
}
