/***************************************************************************************************
Copyright 2009 - 2010 Harshana Eranga Martin, Dishan Metihakwala, Rajeev Sampath, Chathura Randika

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
****************************************************************************************************/
//==============================================================================
//                                                                              
//  THIS FILE HAS BEEN GENERATED BY JSTM                                        
//                                                                              
//==============================================================================

package org.epzilla.clusterNode.clusterInfoObjectModel;

import jstm.core.*;

public class NodeStatusObject extends jstm.core.TransactedStructure {

    public NodeStatusObject() {
        super(FIELD_COUNT);
    }

    public String getclusterID() {
        return (String) getField(0);
    }

    public void setclusterID(String value) {
        setField(0, value);
    }

    public String getIP() {
        return (String) getField(1);
    }

    public void setIP(String value) {
        setField(1, value);
    }

    public Boolean getIsActive() {
        return (Boolean) getField(2);
    }

    public void setIsActive(Boolean value) {
        setField(2, value);
    }

    public static final int CLUSTERID_INDEX = 0;

    public static final String CLUSTERID_NAME = "clusterID";

    public static final int IP_INDEX = 1;

    public static final String IP_NAME = "IP";

    public static final int ISACTIVE_INDEX = 2;

    public static final String ISACTIVE_NAME = "IsActive";

    public static final int FIELD_COUNT = 3;

    @Override
    public String getFieldName(int index) {
        return getFieldNameStatic(index);
    }

    public static String getFieldNameStatic(int index) {
        switch (index) {
            case 0:
                return "clusterID";
            case 1:
                return "IP";
            case 2:
                return "IsActive";
            default:
                throw new java.lang.IllegalArgumentException();
        }
    }

    // Internal

    @Override
    protected int getClassId() {
        return 3;
    }

    @Override
    public String getObjectModelUID() {
        return "+rp4BfckzlY1xKPgc7d8Qg";
    }

    private static final int[] NON_TRANSIENT_FIELDS = new int[] {  };

    @Override
    protected int[] getNonTransientFields() {
        return NON_TRANSIENT_FIELDS;
    }

    @Override
    protected void serialize(TransactedObject.Version version, Writer writer) throws java.io.IOException {
        boolean[] reads = ((TransactedStructure.Version) version).getReads();
        Object[] values = ((TransactedStructure.Version) version).getWrites();

        if (reads != null) {
            writer.writeShort(Short.MAX_VALUE);

            for (int i = 0; i < 3; i++)
                writer.writeBoolean(reads[i]);
        }

        if (values != null) {
            if (values[0] != null) {
                if (values[0] == Removal.Instance)
                    writer.writeShort((short) -1);
                else {
                    writer.writeShort((short) 1);
                    writer.writeString((String) values[0]);
                }
            }

            if (values[1] != null) {
                if (values[1] == Removal.Instance)
                    writer.writeShort((short) -2);
                else {
                    writer.writeShort((short) 2);
                    writer.writeString((String) values[1]);
                }
            }

            if (values[2] != null) {
                if (values[2] == Removal.Instance)
                    writer.writeShort((short) -3);
                else {
                    writer.writeShort((short) 3);
                    writer.writeBoolean(((java.lang.Boolean) values[2]).booleanValue());
                }
            }
        }

        writer.writeShort((short) 0);
    }

    @SuppressWarnings("null")
    @Override
    protected void deserialize(TransactedObject.Version version, Reader reader) throws java.io.IOException {
        boolean[] reads = null;
        Object[] values = null;

        short index = reader.readShort();

        if (index == Short.MAX_VALUE) {
            reads = new boolean[3];

            for (int i = 0; i < 3; i++)
                reads[i] = reader.readBoolean();

            index = reader.readShort();
        }

        if (index == 1) {
            if (values == null)
                values = new Object[3];

            values[0] = reader.readString();
            index = reader.readShort();
        } else if (index == -1) {
            if (values == null)
                values = new Object[3];

            values[0] = Removal.Instance;
            index = reader.readShort();
        }

        if (index == 2) {
            if (values == null)
                values = new Object[3];

            values[1] = reader.readString();
            index = reader.readShort();
        } else if (index == -2) {
            if (values == null)
                values = new Object[3];

            values[1] = Removal.Instance;
            index = reader.readShort();
        }

        if (index == 3) {
            if (values == null)
                values = new Object[3];

            values[2] = new java.lang.Boolean(reader.readBoolean());
            index = reader.readShort();
        } else if (index == -3) {
            if (values == null)
                values = new Object[3];

            values[2] = Removal.Instance;
            index = reader.readShort();
        }

        ((TransactedStructure.Version) version).setReads(reads);
        ((TransactedStructure.Version) version).setWrites(values);
    }
}
