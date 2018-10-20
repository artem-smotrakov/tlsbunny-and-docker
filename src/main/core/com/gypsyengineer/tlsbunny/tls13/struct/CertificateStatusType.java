package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;

public interface CertificateStatusType extends Struct {

    int ENCODING_LENGTH = 1;

    CertificateStatusType ocsp = StructFactory.getDefault().createCertificateStatusType(1);

    int getCode();
}
