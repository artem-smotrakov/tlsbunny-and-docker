package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroupList;
import java.io.IOException;

public class NamedGroupListImpl implements NamedGroupList {

    private Vector<NamedGroup> named_group_list;

    NamedGroupListImpl(Vector<NamedGroup> named_group_list) {
        this.named_group_list = named_group_list;
    }

    @Override
    public void set(NamedGroup group) {
        named_group_list = Vector.wrap(LENGTH_BYTES, group);
    }

    @Override
    public void set(Vector<NamedGroup> named_group_list) {
        this.named_group_list = named_group_list;
    }

    @Override
    public int encodingLength() {
        return named_group_list.encodingLength();
    }

    @Override
    public byte[] encoding() throws IOException {
        return named_group_list.encoding();
    }
    
}
