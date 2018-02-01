package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroupList;

public class NamedGroupListImpl implements NamedGroupList {

    private Vector<NamedGroup> named_group_list;

    private NamedGroupListImpl(Vector<NamedGroup> named_group_list) {
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
    
    public static NamedGroupList create(NamedGroup... namedGroups) {
        return new NamedGroupListImpl(
                Vector.wrap(LENGTH_BYTES, namedGroups));
    }

    public static NamedGroupList parse(ByteBuffer buffer) {
        Vector<NamedGroup> named_group_list = Vector.parse(buffer,
                LENGTH_BYTES,
                buf -> NamedGroupImpl.parse(buf));

        return new NamedGroupListImpl(named_group_list);
    }
    
    public static NamedGroupList parse(Vector<Byte> extension_data) 
            throws IOException {
        
        return parse(ByteBuffer.wrap(extension_data.bytes()));
    }

}
