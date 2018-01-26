package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Entity;
import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;
import java.nio.ByteBuffer;

public class NamedGroupList implements Entity {

    public static final int LENGTH_BYTES = 2;

    private Vector<NamedGroup> named_group_list;

    private NamedGroupList(Vector<NamedGroup> named_group_list) {
        this.named_group_list = named_group_list;
    }

    public void set(NamedGroup group) {
        named_group_list = Vector.wrap(LENGTH_BYTES, group);
    }

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
        return new NamedGroupList(
                Vector.wrap(LENGTH_BYTES, namedGroups));
    }

    public static NamedGroupList parse(ByteBuffer buffer) {
        Vector<NamedGroup> named_group_list = Vector.parse(buffer,
                LENGTH_BYTES,
                buf -> NamedGroup.parse(buf));

        return new NamedGroupList(named_group_list);
    }
    
    public static NamedGroupList parse(Vector<Byte> extension_data) 
            throws IOException {
        
        return parse(ByteBuffer.wrap(extension_data.bytes()));
    }

}
