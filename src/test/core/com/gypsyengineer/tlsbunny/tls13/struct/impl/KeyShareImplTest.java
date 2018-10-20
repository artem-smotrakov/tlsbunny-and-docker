package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.KeyShare;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class KeyShareImplTest {

    @Test
    public void clientHello() throws IOException {
        StructFactory factory = StructFactory.getDefault();

        KeyShare.ClientHello one = factory.createKeyShareForClientHello(
                factory.createKeyShareEntry(NamedGroup.secp256r1, new byte[32]));
        assertNotNull(one);
        assertEquals(one, one);
        assertNotEquals(one, null);
        assertEquals(
                one.getClientShares(),
                Vector.wrap(2, factory.createKeyShareEntry(NamedGroup.secp256r1, new byte[32])));

        Struct struct = one.copy();
        assertTrue(struct instanceof KeyShare);
        KeyShare two = (KeyShare) struct;
        assertFalse(one == two);
        assertEquals(one, two);
        assertEquals(one.hashCode(), two.hashCode());
        assertEquals(one.encodingLength(), two.encodingLength());
        assertArrayEquals(one.encoding(), two.encoding());

        KeyShareImpl.ClientHelloImpl three = new KeyShareImpl.ClientHelloImpl();
        assertNotEquals(one, three);
    }

    @Test
    public void serverHello() throws IOException {
        StructFactory factory = StructFactory.getDefault();

        KeyShare clientHelloKeyShare = factory.createKeyShareForClientHello(
                factory.createKeyShareEntry(NamedGroup.secp256r1, new byte[32]));

        KeyShare.ServerHello one = factory.createKeyShareForServerHello(
                factory.createKeyShareEntry(NamedGroup.secp256r1, new byte[32]));
        assertNotNull(one);
        assertEquals(one, one);
        assertNotEquals(one, null);
        assertNotEquals(one, clientHelloKeyShare);
        assertEquals(
                one.getServerShare(),
                factory.createKeyShareEntry(NamedGroup.secp256r1, new byte[32]));

        Struct struct = one.copy();
        assertTrue(struct instanceof KeyShare);
        KeyShare two = (KeyShare) struct;
        assertFalse(one == two);
        assertEquals(one, two);
        assertEquals(one.hashCode(), two.hashCode());
        assertEquals(one.encodingLength(), two.encodingLength());
        assertArrayEquals(one.encoding(), two.encoding());

        KeyShareImpl.ClientHelloImpl three = new KeyShareImpl.ClientHelloImpl();
        assertNotEquals(one, three);
    }

    @Test
    public void helloRetryRequest() throws IOException {
        StructFactory factory = StructFactory.getDefault();

        KeyShare clientHelloKeyShare = factory.createKeyShareForClientHello(
                factory.createKeyShareEntry(NamedGroup.secp256r1, new byte[32]));

        KeyShare.HelloRetryRequest one = new KeyShareImpl.HelloRetryRequestImpl(NamedGroup.secp256r1);
        assertNotNull(one);
        assertEquals(one, one);
        assertNotEquals(one, null);
        assertNotEquals(one, clientHelloKeyShare);
        assertEquals(one.getSelectedGroup(), NamedGroup.secp256r1);

        Struct struct = one.copy();
        assertTrue(struct instanceof KeyShare);
        KeyShare two = (KeyShare) struct;
        assertFalse(one == two);
        assertEquals(one, two);
        assertEquals(one.hashCode(), two.hashCode());
        assertEquals(one.encodingLength(), two.encodingLength());
        assertArrayEquals(one.encoding(), two.encoding());

        KeyShareImpl.ClientHelloImpl three = new KeyShareImpl.ClientHelloImpl();
        assertNotEquals(one, three);
    }
}
