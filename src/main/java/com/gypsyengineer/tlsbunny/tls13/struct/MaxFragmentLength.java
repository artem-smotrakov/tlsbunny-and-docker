package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;

/**
 * See RFC 6066
 */
public interface MaxFragmentLength extends Struct {

    int ENCODING_LENGTH = 1;

    MaxFragmentLength two_pow_nine = StructFactory.getDefault().createMaxFragmentLength(1);
    MaxFragmentLength two_pow_ten = StructFactory.getDefault().createMaxFragmentLength(2);
    MaxFragmentLength two_pow_eleven = StructFactory.getDefault().createMaxFragmentLength(3);
    MaxFragmentLength two_pos_twelwe = StructFactory.getDefault().createMaxFragmentLength(4);

    int getCode();
}
