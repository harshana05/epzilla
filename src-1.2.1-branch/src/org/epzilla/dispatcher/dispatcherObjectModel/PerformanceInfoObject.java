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

package org.epzilla.dispatcher.dispatcherObjectModel;

import jstm.core.*;

public class PerformanceInfoObject extends jstm.core.TransactedStructure {

    public PerformanceInfoObject() {
        super(FIELD_COUNT);
    }

    public String getnodeIP() {
        return (String) getField(0);
    }

    public void setnodeIP(String value) {
        setField(0, value);
    }

    public String getCPUusageAverage() {
        return (String) getField(1);
    }

    public void setCPUusageAverage(String value) {
        setField(1, value);
    }

    public String getMemUsageAverage() {
        return (String) getField(2);
    }

    public void setMemUsageAverage(String value) {
        setField(2, value);
    }

    public String getNetworkUsageAverage() {
        return (String) getField(3);
    }

    public void setNetworkUsageAverage(String value) {
        setField(3, value);
    }

    public static final int NODEIP_INDEX = 0;

    public static final String NODEIP_NAME = "nodeIP";

    public static final int CPUUSAGEAVERAGE_INDEX = 1;

    public static final String CPUUSAGEAVERAGE_NAME = "CPUusageAverage";

    public static final int MEMUSAGEAVERAGE_INDEX = 2;

    public static final String MEMUSAGEAVERAGE_NAME = "MemUsageAverage";

    public static final int NETWORKUSAGEAVERAGE_INDEX = 3;

    public static final String NETWORKUSAGEAVERAGE_NAME = "NetworkUsageAverage";

    public static final int FIELD_COUNT = 4;

    @Override
    public String getFieldName(int index) {
        return getFieldNameStatic(index);
    }

    public static String getFieldNameStatic(int index) {
        switch (index) {
            case 0:
                return "nodeIP";
            case 1:
                return "CPUusageAverage";
            case 2:
                return "MemUsageAverage";
            case 3:
                return "NetworkUsageAverage";
            default:
                throw new java.lang.IllegalArgumentException();
        }
    }

    // Internal

    @Override
    protected int getClassId() {
        return 5;
    }

    @Override
    public String getObjectModelUID() {
        return "2sG6AVQttNcts9YIS+QbNw";
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

            for (int i = 0; i < 4; i++)
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
                    writer.writeString((String) values[2]);
                }
            }

            if (values[3] != null) {
                if (values[3] == Removal.Instance)
                    writer.writeShort((short) -4);
                else {
                    writer.writeShort((short) 4);
                    writer.writeString((String) values[3]);
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
            reads = new boolean[4];

            for (int i = 0; i < 4; i++)
                reads[i] = reader.readBoolean();

            index = reader.readShort();
        }

        if (index == 1) {
            if (values == null)
                values = new Object[4];

            values[0] = reader.readString();
            index = reader.readShort();
        } else if (index == -1) {
            if (values == null)
                values = new Object[4];

            values[0] = Removal.Instance;
            index = reader.readShort();
        }

        if (index == 2) {
            if (values == null)
                values = new Object[4];

            values[1] = reader.readString();
            index = reader.readShort();
        } else if (index == -2) {
            if (values == null)
                values = new Object[4];

            values[1] = Removal.Instance;
            index = reader.readShort();
        }

        if (index == 3) {
            if (values == null)
                values = new Object[4];

            values[2] = reader.readString();
            index = reader.readShort();
        } else if (index == -3) {
            if (values == null)
                values = new Object[4];

            values[2] = Removal.Instance;
            index = reader.readShort();
        }

        if (index == 4) {
            if (values == null)
                values = new Object[4];

            values[3] = reader.readString();
            index = reader.readShort();
        } else if (index == -4) {
            if (values == null)
                values = new Object[4];

            values[3] = Removal.Instance;
            index = reader.readShort();
        }

        ((TransactedStructure.Version) version).setReads(reads);
        ((TransactedStructure.Version) version).setWrites(values);
    }
}
