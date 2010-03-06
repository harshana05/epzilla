//==============================================================================
//                                                                              
//  THIS FILE HAS BEEN GENERATED BY JSTM                                        
//                                                                              
//==============================================================================

package org.epzilla.clusterNode.clusterInfoObjectModel;

import jstm.core.*;

public final class ClusterObjectModel extends jstm.core.ObjectModel {

    public static final String UID = "xBrwuzjF9k/6NeznYLAicA";

    public static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ObjectModelDefinition xsi:noNamespaceSchemaLocation=\"http://www.xstm.net/schemas/xstm-0.3.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Name>ClusterObjectModel</Name><RootPackage name=\"clusterInfoObjectModel\"><Packages/><Structures><Structure name=\"TriggerObject\"><Fields><Field transient=\"false\" name=\"clientID\"><Type name=\"java.lang.String\"/></Field><Field transient=\"false\" name=\"triggerID\"><Type name=\"java.lang.String\"/></Field><Field transient=\"false\" name=\"trigger\"><Type name=\"java.lang.String\"/></Field></Fields><Methods/></Structure></Structures></RootPackage></ObjectModelDefinition>";

    public ClusterObjectModel() {
    }

    @Override
    public String getUID() {
        return UID;
    }

    @Override
    public String getXML() {
        return XML;
    }

    @Override
    public int getClassCount() {
        return 1;
    }

    @Override
    public TransactedObject createInstance(int classId, Connection route) {
        switch (classId) {
            case 0:
                return new TriggerObject();
        }

        throw new IllegalArgumentException("Unknown class id: " + classId);
    }
}
