package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.struct.KeyShareEntry;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.UncompressedPointRepresentation;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.Arrays;
import javax.crypto.KeyAgreement;

public class ECDHENegotiator implements Negotiator {

    private final NamedGroup.Secp group;
    private final SecpParameters secpParameters;
    private final KeyAgreement keyAgreement;
    private final UncompressedPointRepresentation upr;

    private ECDHENegotiator(NamedGroup.Secp group, SecpParameters secpParameters, 
            KeyAgreement keyAgreement, UncompressedPointRepresentation upr) {
        
        this.group = group;
        this.secpParameters = secpParameters;
        this.keyAgreement = keyAgreement;
        this.upr = upr;
    }

    @Override
    public KeyShareEntry createKeyShareEntry() {
        return KeyShareEntry.wrap(group, upr);
    }

    @Override
    public void processKeyShareEntry(KeyShareEntry entry) throws Exception {
        if (!group.equals(entry.getNamedGroup())) {
            throw new IllegalArgumentException();
        }

        PublicKey key = KeyFactory.getInstance("EC").generatePublic(
                new ECPublicKeySpec(
                        convertToECPoint(entry, group), 
                        secpParameters.ecParameterSpec));

        keyAgreement.doPhase(key, true);
    }

    @Override
    public byte[] generateSecret() {
        return keyAgreement.generateSecret();
    }
    
    public static ECDHENegotiator create(NamedGroup.Secp group) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec spec = new ECGenParameterSpec(group.getCurve());
        generator.initialize(spec);
        KeyPair kp = generator.generateKeyPair();

        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
        keyAgreement.init(kp.getPrivate());

        return new ECDHENegotiator(
                group, 
                SecpParameters.create(group), 
                keyAgreement, 
                createUPR(((ECPublicKey) kp.getPublic()).getW(), group));
    }

    public static ECPoint convertToECPoint(KeyShareEntry entry, NamedGroup.Secp group) 
            throws IOException {
        
        UncompressedPointRepresentation upr = 
                UncompressedPointRepresentation.parse(
                        entry.getKeyExchange().bytes(), 
                        getCoordinateLength(group));
        
        return new ECPoint(new BigInteger(upr.getX()), new BigInteger(upr.getY()));
    }

    public static UncompressedPointRepresentation createUPR(
            ECPoint point, NamedGroup.Secp group) {

        int coordinate_length = getCoordinateLength(group);
        return new UncompressedPointRepresentation(
                toBytes(point.getAffineX(), coordinate_length), 
                toBytes(point.getAffineY(), coordinate_length));
    }

    public static int getCoordinateLength(NamedGroup.Secp group) {
        switch (group.getCurve()) {
            case "secp256r1":
                return 32;
            case "secp384r1":
                return 48;
            case "secp521r1":
                return 66;
        }
        
        throw new IllegalArgumentException();
    }

    private static byte[] toBytes(BigInteger n, int length) {
        byte[] bytes = n.toByteArray();

        if (bytes.length > length) {
            int i = 0;
            while (i < bytes.length && bytes[i] == 0) {
                i++;
            }
            
            if (i == bytes.length) {
                throw new IllegalArgumentException();
            }
            
            return Arrays.copyOfRange(bytes, i, bytes.length);
        } 
        
        if (bytes.length < length) {
            byte[] result = new byte[length];
            System.arraycopy(bytes, 0, result, length - bytes.length, 
                    bytes.length);

            return result;
        }

        return bytes;
    }

}


