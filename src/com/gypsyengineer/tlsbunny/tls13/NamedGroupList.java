package com.gypsyengineer.tlsbunny.tls13;

import com.gypsyengineer.tlsbunny.tls.Entity;
import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;
import java.nio.ByteBuffer;

public class NamedGroupList implements Entity {

    public static final int NAMED_GROUP_LIST_LENGTH_BYTES = 2;
    public static final int MIN_EXPECTED_LENGTH = 2;
    public static final int MAX_EXPECTED_LENGTH = 65535;

    private Vector<NamedGroup> named_group_list;

    private NamedGroupList(Vector<NamedGroup> named_group_list) {
        this.named_group_list = named_group_list;
    }

    public void set(NamedGroup group) {
        named_group_list = Vector.wrap(MAX_EXPECTED_LENGTH, group);
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
                Vector.wrap(NAMED_GROUP_LIST_LENGTH_BYTES, namedGroups));
    }

    public static NamedGroupList parse(ByteBuffer buffer) {
        Vector<NamedGroup> named_group_list = Vector.parse(
                buffer,
                NAMED_GROUP_LIST_LENGTH_BYTES,
                (buf) -> NamedGroup.parse(buf));

        return new NamedGroupList(named_group_list);
    }

}
