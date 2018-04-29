package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.struct.KeyShareEntry;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.struct.UncompressedPointRepresentation;
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

public class ECDHENegotiator extends AbstractNegotiator {

    private final SecpParameters secpParameters;
    private final KeyAgreement keyAgreement;
    private final KeyPairGenerator generator;

    private ECDHENegotiator(NamedGroup.Secp group, SecpParameters secpParameters, 
            KeyAgreement keyAgreement, KeyPairGenerator generator, StructFactory factory) {
        
        super(group, factory);
        this.secpParameters = secpParameters;
        this.keyAgreement = keyAgreement;
        this.generator = generator;
    }

    @Override
    public KeyShareEntry  createKeyShareEntry() throws Exception {
        KeyPair kp = generator.generateKeyPair();
        keyAgreement.init(kp.getPrivate());
        ECPoint W = ((ECPublicKey) kp.getPublic()).getW();
        
        return factory.createKeyShareEntry(
                group, 
                createUPR(W).encoding());
    }
    
    @Override
    public void processKeyShareEntry(KeyShareEntry entry) throws Exception {
        if (!group.equals(entry.getNamedGroup())) {
            throw new IllegalArgumentException();
        }

        PublicKey key = KeyFactory.getInstance("EC").generatePublic(
                new ECPublicKeySpec(
                        convertToECPoint(entry), 
                        secpParameters.ecParameterSpec));

        keyAgreement.doPhase(key, true);
    }

    @Override
    public byte[] generateSecret() {
        return keyAgreement.generateSecret();
    }
    
    private NamedGroup.Secp getGroup() {
        return (NamedGroup.Secp) group;
    }
    
    private ECPoint convertToECPoint(KeyShareEntry entry) 
            throws IOException {
        
        UncompressedPointRepresentation upr = 
                factory.parser().parseUncompressedPointRepresentation(
                        entry.getKeyExchange().bytes(), 
                        getCoordinateLength(getGroup()));
        
        return new ECPoint(new BigInteger(upr.getX()), new BigInteger(upr.getY()));
    }
    
    private UncompressedPointRepresentation createUPR(ECPoint point) {
        int coordinate_length = getCoordinateLength(getGroup());
        return factory.createUncompressedPointRepresentation(
                toBytes(point.getAffineX(), coordinate_length), 
                toBytes(point.getAffineY(), coordinate_length));
    }
    
    public static ECDHENegotiator create(NamedGroup.Secp group, StructFactory factory) 
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec spec = new ECGenParameterSpec(group.getCurve());
        generator.initialize(spec);

        return new ECDHENegotiator(
                group, 
                SecpParameters.create(group), 
                KeyAgreement.getInstance("ECDH"),
                generator, factory);
    }

    private static int getCoordinateLength(NamedGroup.Secp group) {
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


