package com.gypsyengineer.tlsbunny.tls13.handshake;

public class Constants {

    // TODO: these constants shouldn't be public

    public static final byte[] exp_master      = "exp master".getBytes();
    public static final byte[] ext_binder      = "ext binder".getBytes();
    public static final byte[] res_binder      = "res binder".getBytes();
    public static final byte[] c_e_traffic     = "c e traffic".getBytes();
    public static final byte[] e_exp_master    = "e exp master".getBytes();
    public static final byte[] derived         = "derived".getBytes();
    public static final byte[] c_hs_traffic    = "c hs traffic".getBytes();
    public static final byte[] s_hs_traffic    = "s hs traffic".getBytes();
    public static final byte[] c_ap_traffic    = "c ap traffic".getBytes();
    public static final byte[] s_ap_traffic    = "s ap traffic".getBytes();
    public static final byte[] res_master      = "res master".getBytes();
    public static final byte[] key             = "key".getBytes();
    public static final byte[] iv              = "iv".getBytes();
    public static final byte[] finished        = "finished".getBytes();

    public static final byte[] ZERO_SALT = new byte[0];
    public static final byte[] ZERO_HASH_VALUE = new byte[0];
}
