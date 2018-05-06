package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.struct.KeyShareEntry;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.struct.UncompressedPointRepresentation;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
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
    public KeyShareEntry  createKeyShareEntry() throws NegotiatorException {
        try {
            KeyPair kp = generator.generateKeyPair();
            keyAgreement.init(kp.getPrivate());
            ECPoint W = ((ECPublicKey) kp.getPublic()).getW();

            // TODO: consider adding a self-check to make sure that the key
            //       passes checks defined by NIST and Section 5.2.2 of X9.62
            //       in other words:
            //
            //   validate(w);

            return factory.createKeyShareEntry(
                    group,
                    createUPR(W).encoding());
        } catch (InvalidKeyException | IOException e) {
            throw new NegotiatorException(e);
        }
    }
    
    @Override
    public void processKeyShareEntry(KeyShareEntry entry) throws NegotiatorException {
        if (!group.equals(entry.getNamedGroup())) {
            output.achtung("expected group: %s", group);
            output.achtung("received group: %s", entry.getNamedGroup());
            throw new NegotiatorException("unexpected group");
        }

        try {
            ECPoint point = convertToECPoint(entry);
            validate(point);

            PublicKey key = KeyFactory.getInstance("EC").generatePublic(
                    new ECPublicKeySpec(
                            point,
                            secpParameters.ecParameterSpec));

            keyAgreement.doPhase(key, true);
        } catch (NoSuchAlgorithmException | IOException | InvalidKeyException
                | InvalidKeySpecException e) {
            throw new NegotiatorException(e);
        }
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

        // TODO: is it correct to use BigInteger(int, byte[]) to set the sign +1
        //       instead of BigInteger(byte[]) ?
        return new ECPoint(
                new BigInteger(1, upr.getX()),
                new BigInteger(1, upr.getY()));
    }
    
    private UncompressedPointRepresentation createUPR(ECPoint point) {
        int coordinate_length = getCoordinateLength(getGroup());
        return factory.createUncompressedPointRepresentation(
                toBytes(point.getAffineX(), coordinate_length), 
                toBytes(point.getAffineY(), coordinate_length));
    }

    // validates an EC public key defined by section 5.6.2.3.2 of NIST SP 56A
    // and section 5.2.2 of X9.62:
    // - https://nvlpubs.nist.gov/nistpubs/SpecialPublications/NIST.SP.800-56Ar2.pdf
    // - http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.202.2977&rep=rep1&type=pdf
    private void validate(ECPoint point) throws NegotiatorException {
        // step #1: verify the public key is not the point at infinity
        BigInteger x = point.getAffineX();
        BigInteger y = point.getAffineY();
        if (x == null || y == null) {
            output.info("x = %s", x.toString());
            output.info("y = %s", y.toString());
            //throw new NegotiatorException("point is at infinity");
        }

        // step #2: verify x and y are in range [0, p-1]
        BigInteger p = getP();
        if (x.signum() == -1 || x.compareTo(p) != -1) {
            output.achtung("x = %s", x.toString());
            output.achtung("p = %s", p.toString());
            throw new NegotiatorException("x is out of range [0, p-1]");
        }
        if (y.signum() == -1 || y.compareTo(p) != -1) {
            output.achtung("y = %s", y.toString());
            output.achtung("p = %s", p.toString());
            throw new NegotiatorException("y is out of range [0, p-1]");
        }

        // step #3: verify that y^2 == x^3 + ax + b (mod p)
        BigInteger a = secpParameters.ecParameterSpec.getCurve().getA();
        BigInteger b = secpParameters.ecParameterSpec.getCurve().getA();
        BigInteger lhs = y.multiply(y).mod(p);
        BigInteger rhs = x.multiply(x).add(a).multiply(x).add(b).mod(p);
        if (!lhs.equals(rhs)) {
            output.achtung("point is not on the curve");
            throw new NegotiatorException("point is not on the curve");
        }
    }

    private BigInteger getP() throws NegotiatorException {
        ECField field = secpParameters.ecParameterSpec.getCurve().getField();
        if (field instanceof ECFieldFp) {
            return ((ECFieldFp) field).getP();
        }

        throw new NegotiatorException("only curves over prime order fields are supported");
    }
    
    public static ECDHENegotiator create(NamedGroup.Secp group, StructFactory factory) 
            throws NegotiatorException {

        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec spec = new ECGenParameterSpec(group.getCurve());
            generator.initialize(spec);

            return new ECDHENegotiator(
                    group,
                    SecpParameters.create(group),
                    KeyAgreement.getInstance("ECDH"),
                    generator, factory);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new NegotiatorException(e);
        }
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
