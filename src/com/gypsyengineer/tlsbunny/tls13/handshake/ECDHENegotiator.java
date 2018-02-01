package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.KeyShareEntryImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.NamedGroupImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.UncompressedPointRepresentationImpl;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.Arrays;
import javax.crypto.KeyAgreement;

public class ECDHENegotiator implements Negotiator {

    private final NamedGroupImpl.SecpImpl group;
    private final SecpParameters secpParameters;
    private final KeyAgreement keyAgreement;
    private final KeyPairGenerator generator;

    private ECDHENegotiator(NamedGroupImpl.SecpImpl group, SecpParameters secpParameters, 
            KeyAgreement keyAgreement, KeyPairGenerator generator) {
        
        this.group = group;
        this.secpParameters = secpParameters;
        this.keyAgreement = keyAgreement;
        this.generator = generator;
    }

    @Override
    public KeyShareEntryImpl createKeyShareEntry() throws Exception {
        KeyPair kp = generator.generateKeyPair();
        keyAgreement.init(kp.getPrivate());
        
        return KeyShareEntryImpl.wrap(
                group, createUPR(((ECPublicKey) kp.getPublic()).getW(), group));
    }

    @Override
    public void processKeyShareEntry(KeyShareEntryImpl entry) throws Exception {
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
    
    public static ECDHENegotiator create(NamedGroupImpl.SecpImpl group) 
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec spec = new ECGenParameterSpec(group.getCurve());
        generator.initialize(spec);

        return new ECDHENegotiator(
                group, 
                SecpParameters.create(group), 
                KeyAgreement.getInstance("ECDH"),
                generator);
    }

    public static ECPoint convertToECPoint(KeyShareEntryImpl entry, NamedGroupImpl.SecpImpl group) 
            throws IOException {
        
        UncompressedPointRepresentationImpl upr = 
                UncompressedPointRepresentationImpl.parse(
                        entry.getKeyExchange().bytes(), 
                        getCoordinateLength(group));
        
        return new ECPoint(new BigInteger(upr.getX()), new BigInteger(upr.getY()));
    }

    public static UncompressedPointRepresentationImpl createUPR(
            ECPoint point, NamedGroupImpl.SecpImpl group) {

        int coordinate_length = getCoordinateLength(group);
        return new UncompressedPointRepresentationImpl(
                toBytes(point.getAffineX(), coordinate_length), 
                toBytes(point.getAffineY(), coordinate_length));
    }

    public static int getCoordinateLength(NamedGroupImpl.SecpImpl group) {
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


