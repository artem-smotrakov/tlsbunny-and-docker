package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.Cookie;

import java.io.IOException;

public class CookieImpl implements Cookie {

    private Vector<Byte> cookie;

    CookieImpl(Vector<Byte> cookie) {
        this.cookie = cookie;
    }

    @Override
    public Vector<Byte> getCookie() {
        return cookie;
    }

    @Override
    public int encodingLength() {
        return cookie.encodingLength();
    }

    @Override
    public byte[] encoding() throws IOException {
        return cookie.encoding();
    }

    @Override
    public CookieImpl copy() {
        return new CookieImpl((Vector<Byte>) cookie.copy());
    }
}
