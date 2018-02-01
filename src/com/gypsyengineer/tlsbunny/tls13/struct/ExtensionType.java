package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.ExtensionTypeImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;

public interface ExtensionType extends Struct {

    int ENCODING_LENGTH = 2;
    ExtensionType application_layer_protocol_negotiation = new ExtensionTypeImpl(16);
    ExtensionType certificate_authorities = new ExtensionTypeImpl(47);
    ExtensionType client_certificate_type = new ExtensionTypeImpl(19);
    ExtensionType cookie = new ExtensionTypeImpl(44);
    ExtensionType early_data = new ExtensionTypeImpl(42);
    ExtensionType heartbeat = new ExtensionTypeImpl(15);
    ExtensionType key_share = new ExtensionTypeImpl(40);
    ExtensionType max_fragment_length = new ExtensionTypeImpl(1);
    ExtensionType oid_filters = new ExtensionTypeImpl(48);
    ExtensionType padding = new ExtensionTypeImpl(21);
    ExtensionType post_handshake_auth = new ExtensionTypeImpl(49);
    ExtensionType pre_shared_key = new ExtensionTypeImpl(41);
    ExtensionType psk_key_exchange_modes = new ExtensionTypeImpl(45);
    ExtensionType server_certificate_type = new ExtensionTypeImpl(20);
    ExtensionType server_name = new ExtensionTypeImpl(0);
    ExtensionType signature_algorithms = new ExtensionTypeImpl(13);
    ExtensionType signed_certificate_timestamp = new ExtensionTypeImpl(18);
    ExtensionType status_request = new ExtensionTypeImpl(5);
    ExtensionType supported_groups = new ExtensionTypeImpl(10);
    ExtensionType supported_versions = new ExtensionTypeImpl(43);
    ExtensionType use_srtp = new ExtensionTypeImpl(14);

    int getCode();
    void setCode(int code);
}
