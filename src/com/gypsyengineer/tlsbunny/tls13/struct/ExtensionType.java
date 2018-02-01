/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.ExtensionTypeImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;
import java.io.IOException;

/**
 *
 * @author artem
 */
public interface ExtensionType extends Struct {

    int ENCODING_LENGTH = 2;
    ExtensionTypeImpl application_layer_protocol_negotiation = new ExtensionTypeImpl(16);
    ExtensionTypeImpl certificate_authorities = new ExtensionTypeImpl(47);
    ExtensionTypeImpl client_certificate_type = new ExtensionTypeImpl(19);
    ExtensionTypeImpl cookie = new ExtensionTypeImpl(44);
    ExtensionTypeImpl early_data = new ExtensionTypeImpl(42);
    ExtensionTypeImpl heartbeat = new ExtensionTypeImpl(15);
    ExtensionTypeImpl key_share = new ExtensionTypeImpl(40);
    ExtensionTypeImpl max_fragment_length = new ExtensionTypeImpl(1);
    ExtensionTypeImpl oid_filters = new ExtensionTypeImpl(48);
    ExtensionTypeImpl padding = new ExtensionTypeImpl(21);
    ExtensionTypeImpl post_handshake_auth = new ExtensionTypeImpl(49);
    ExtensionTypeImpl pre_shared_key = new ExtensionTypeImpl(41);
    ExtensionTypeImpl psk_key_exchange_modes = new ExtensionTypeImpl(45);
    ExtensionTypeImpl server_certificate_type = new ExtensionTypeImpl(20);
    ExtensionTypeImpl server_name = new ExtensionTypeImpl(0);
    ExtensionTypeImpl signature_algorithms = new ExtensionTypeImpl(13);
    ExtensionTypeImpl signed_certificate_timestamp = new ExtensionTypeImpl(18);
    ExtensionTypeImpl status_request = new ExtensionTypeImpl(5);
    ExtensionTypeImpl supported_groups = new ExtensionTypeImpl(10);
    ExtensionTypeImpl supported_versions = new ExtensionTypeImpl(43);
    ExtensionTypeImpl use_srtp = new ExtensionTypeImpl(14);

    byte[] encoding() throws IOException;

    int encodingLength();

    boolean equals(Object obj);

    int getCode();

    int hashCode();

    void setCode(int code);
    
}
