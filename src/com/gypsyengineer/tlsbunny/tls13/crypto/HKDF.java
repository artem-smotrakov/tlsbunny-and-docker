package com.gypsyengineer.tlsbunny.tls13.crypto;

import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.HkdfLabel;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Mac;
import static com.gypsyengineer.tlsbunny.utils.Utils.concatenate;
import javax.crypto.spec.SecretKeySpec;

public class HKDF {

    private final int hashLen;
    private final Mac mac;
    private final TranscriptHash transcriptHash;
    private final StructFactory factory;

    private HKDF(int hashLength, Mac mac, TranscriptHash transcriptHash, 
            StructFactory factory) {
        
        this.hashLen = hashLength;
        this.mac = mac;
        this.transcriptHash = transcriptHash;
        this.factory = factory;
    }

    public int getHashLength() {
        return hashLen;
    }

    // HKDF-Extract(salt, IKM) -> PRK
    public byte[] extract(byte[] salt, byte[] IKM) throws InvalidKeyException {
        return hmac(salt, IKM);
    }

    // HKDF-Expand(PRK, info, L) -> OKM
    public byte[] expand(byte[] PRK, byte[] info, int L) throws InvalidKeyException {
        int maxLen = 255 * hashLen;
        if (L > maxLen) {
            throw new IllegalArgumentException();
        }

        int N = (int) Math.ceil((double) L / hashLen);
        ByteBuffer T = ByteBuffer.allocate(N * hashLen);
        byte[] t = new byte[0];
        mac.reset();
        mac.init(new SecretKeySpec(PRK, mac.getAlgorithm()));
        for (int i = 1; i <= N; i++) {
            t = mac.doFinal(concatenate(t, info, new byte[] { (byte) i }));
            T.put(t);
        }

        return Arrays.copyOf(T.array(), L);
    }

    //HKDF-Expand-Label(Secret, Label, HashValue, Length) =
    //        HKDF-Expand(Secret, HkdfLabel, Length)
    public byte[] expandLabel(byte[] secret, byte[] label, byte[] hashValue, int length)
            throws IOException, InvalidKeyException {

        return expand(secret, 
                createHkdfLabel(length, label, hashValue).encoding(), 
                length);
    }

    // Derive-Secret(Secret, Label, Messages) =
    //        HKDF-Expand-Label(Secret, Label,
    //                          Transcript-Hash(Messages), Hash.length)
    public byte[] deriveSecret(byte[] secret, byte[] label, Handshake... messages)
            throws IOException, InvalidKeyException {

        return expandLabel(secret, label, transcriptHash.compute(messages), hashLen);
    }

    // HMAC-Hash() function
    public byte[] hmac(byte[] key, byte[] input) throws InvalidKeyException {
        mac.reset();
        mac.init(new RawKey(key, mac.getAlgorithm()));
        return mac.doFinal(input);
    }

    public static HKDF create(String algorithm, StructFactory factory) 
            throws NoSuchAlgorithmException, IOException {

        return new HKDF(
                MessageDigest.getInstance(algorithm).getDigestLength(),
                getHmac(algorithm),
                TranscriptHash.create(algorithm), 
                factory);
    }

    private static Mac getHmac(String hashAlgorithm) throws NoSuchAlgorithmException {
        return Mac.getInstance("Hmac" + hashAlgorithm.replace("-", ""));
    }

    private HkdfLabel createHkdfLabel(int length, byte[] label, byte[] hashValue) {
        byte[] tls13_label = concatenate("tls13 ".getBytes(), label);
        if (tls13_label.length > HkdfLabel.MAX_LABEL_LENGTH) {
            throw new IllegalArgumentException();
        }

        if (hashValue.length > HkdfLabel.MAX_HASH_VALUE_LENGTH) {
            throw new IllegalArgumentException();
        }

        return factory.createHkdfLabel(length, tls13_label, hashValue);
    }

}
