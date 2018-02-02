package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;

// TODO: make it immutable
public interface NamedGroupList extends Struct {

    int LENGTH_BYTES = 2;

    void set(NamedGroup group);
    void set(Vector<NamedGroup> named_group_list);
}
