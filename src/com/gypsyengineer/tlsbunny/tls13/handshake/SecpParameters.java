package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import static com.gypsyengineer.tlsbunny.utils.Convertor.hex2int;
import java.math.BigInteger;
import java.security.spec.ECField;
import java.security.spec.ECFieldFp;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.HashMap;
import java.util.Map;

class SecpParameters {
    
    public final ECGenParameterSpec ecGenParameterSpec;
    public final ECParameterSpec ecParameterSpec;

    public SecpParameters(String name, BigInteger p, BigInteger a, BigInteger b, 
            ECPoint g, BigInteger n, int h) {
        
        ecGenParameterSpec = new ECGenParameterSpec(name);
        ECField field = new ECFieldFp(p);
        EllipticCurve curve = new EllipticCurve(field, a, b);
        ecParameterSpec = new ECParameterSpec(curve, g, n, h);
    }

    private static final BigInteger SECP256R1_N = hex2int(
            "FFFFFFFF00000000FFFFFFFFFFFFFFFFBCE6FAADA7179E84F3B9CAC2FC632551");

    private static final BigInteger SECP256R1_P = hex2int(
            "FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF");

    private static final BigInteger SECP256R1_A = hex2int(
            "FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFC");

    private static final BigInteger SECP256R1_B = hex2int(
            "5AC635D8AA3A93E7B3EBBD55769886BC651D06B0CC53B0F63BCE3C3E27D2604B");

    private static final BigInteger SECP256R1_G_X = hex2int(
            "6B17D1F2E12C4247F8BCE6E563A440F277037D812DEB33A0F4A13945D898C296");

    private static final BigInteger SECP256R1_G_Y = hex2int(
            "4FE342E2FE1A7F9B8EE7EB4A7C0F9E162BCE33576B315ECECBB6406837BF51F5");

    private static final ECPoint SECP256R1_G = new ECPoint(SECP256R1_G_X, SECP256R1_G_Y);

    private static final int SECP256R1_H = 1;

    private static final Map<NamedGroup.Secp, SecpParameters> PARAMETERS = new HashMap<>();
    static {
        PARAMETERS.put(
                NamedGroup.secp256r1,
                new SecpParameters("secp256r1", 
                        SECP256R1_P,
                        SECP256R1_A, 
                        SECP256R1_B, 
                        SECP256R1_G, 
                        SECP256R1_N, 
                        SECP256R1_H));
    }

    public static SecpParameters create(NamedGroup.Secp group) {
        return PARAMETERS.get(group);
    }
    
}
