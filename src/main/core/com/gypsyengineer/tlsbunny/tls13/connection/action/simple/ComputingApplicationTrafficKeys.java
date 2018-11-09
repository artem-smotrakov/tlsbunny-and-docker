package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Side;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEADException;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.tls13.handshake.Context.ZERO_HASH_VALUE;
import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class ComputingApplicationTrafficKeys
        extends AbstractAction<ComputingApplicationTrafficKeys> {

    private Side side;

    @Override
    public String name() {
        return String.format("computing application traffic keys (%s)", side);
    }

    public ComputingApplicationTrafficKeys side(Side side) {
        this.side = side;
        return this;
    }

    public ComputingApplicationTrafficKeys server() {
        side = Side.server;
        return this;
    }

    public ComputingApplicationTrafficKeys client() {
        side = Side.client;
        return this;
    }

    @Override
    public ComputingApplicationTrafficKeys run() throws IOException, AEADException {
        if (side == null) {
            throw whatTheHell("side not specified! (null)");
        }

        Handshake[] messages = context.messagesForApplicationKeys();

        context.client_application_traffic_secret_0 = context.hkdf.deriveSecret(
                context.master_secret,
                context.c_ap_traffic,
                messages);
        context.server_application_traffic_secret_0 = context.hkdf.deriveSecret(
                context.master_secret,
                context.s_ap_traffic,
                messages);
        context.exporter_master_secret = context.hkdf.deriveSecret(
                context.master_secret,
                context.exp_master,
                messages);
        context.resumption_master_secret = context.hkdf.deriveSecret(
                context.master_secret,
                Context.res_master,
                messages);
        context.client_application_write_key = context.hkdf.expandLabel(
                context.client_application_traffic_secret_0,
                Context.key,
                ZERO_HASH_VALUE,
                context.suite.keyLength());
        context.client_application_write_iv = context.hkdf.expandLabel(
                context.client_application_traffic_secret_0,
                Context.iv,
                ZERO_HASH_VALUE,
                context.suite.ivLength());
        context.server_application_write_key = context.hkdf.expandLabel(
                context.server_application_traffic_secret_0,
                Context.key,
                ZERO_HASH_VALUE,
                context.suite.keyLength());
        context.server_application_write_iv = context.hkdf.expandLabel(
                context.server_application_traffic_secret_0,
                Context.iv,
                ZERO_HASH_VALUE,
                context.suite.ivLength());

        context.applicationDataEncryptor = AEAD.createEncryptor(
                context.suite.cipher(),
                encryptorKey(),
                encryptorIv());
        context.applicationDataDecryptor = AEAD.createDecryptor(
                context.suite.cipher(),
                decryptorKey(),
                decryptorIv());

        return this;
    }

    private byte[] encryptorKey() {
        return side == Side.client
                ? context.client_application_write_key
                : context.server_application_write_key;
    }

    private byte[] encryptorIv() {
        return side == Side.client
                ? context.client_application_write_iv
                : context.server_application_write_iv;
    }

    private byte[] decryptorKey() {
        return side == Side.client
                ? context.server_application_write_key
                : context.client_application_write_key;
    }

    private byte[] decryptorIv() {
        return side == Side.client
                ? context.server_application_write_iv
                : context.client_application_write_iv;
    }

}
