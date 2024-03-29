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
package org.epzilla.dispatcher.objectModelGenerator;

import jstm.core.TransactedList;
import jstm.generator.*;
import jstm.generator.Package;


public class ModelGenerationEngine {

    /**
     * Generate the Object Model for the Dispatcher STM
     * @return
     */
    private static ObjectModelDefinition create() {
        ObjectModelDefinition model = new ObjectModelDefinition("DispatcherObjectModel");
        Package pack = new Package("org.epzilla.dispatcher.dispatcherObjectModel");
        model.RootPackage = pack;

        Structure simple = new Structure("TriggerInfoObject");
        simple.Fields.add(new Field(String.class, "triggerID"));
        simple.Fields.add(new Field(int.class, "dispatcherId"));

        simple.Fields.add(new Field(String.class, "clientID"));
        simple.Fields.add(new Field(String.class, "clusterID"));
        simple.Fields.add(new Field(String.class, "virtualClusterID"));
        simple.Fields.add(new Field(String.class, "trigger"));
        simple.Fields.add(new Field(String.class, "stratumId"));

        // needed when redistribution happens
        simple.Fields.add(new Field(String.class, "oldStratumId"));
        simple.Fields.add(new Field(String.class, "oldClusterId"));


        Structure client = new Structure("ClientInfoObject");
        client.Fields.add(new Field(String.class, "clientID"));
        client.Fields.add(new Field(String.class, "clientIP"));

        Structure leaderIP = new Structure("LeaderInfoObject");
        leaderIP.Fields.add(new Field(String.class, "clusterID"));
        leaderIP.Fields.add(new Field(String.class, "leaderIP"));

        Structure performanceInfo = new Structure("PerformanceInfoObject");
        performanceInfo.Fields.add(new Field(String.class, "nodeIP"));
        performanceInfo.Fields.add(new Field(String.class, "CPUusageAverage"));
        performanceInfo.Fields.add(new Field(String.class, "MemUsageAverage"));
        performanceInfo.Fields.add(new Field(String.class, "NetworkUsageAverage"));

        // contains a dynamic  trigger structure.
        Structure clientTriggerStructure = new Structure("TriggerDependencyStructure");
        clientTriggerStructure.Fields.add(new Field(String.class, "clientId"));
        clientTriggerStructure.Fields.add(new Field(int.class, "virtualStrata"));
        clientTriggerStructure.Fields.add(new Field(TransactedList.class, "OutputStructure"));
        clientTriggerStructure.Fields.add(new Field(TransactedList.class, "InputStructure"));

        Structure marker = new Structure("ShareMarker");
        marker.Fields.add(new Field(String.class, "id"));

        pack.Structures.add(simple);
        pack.Structures.add(client);
        pack.Structures.add(leaderIP);
        pack.Structures.add(clientTriggerStructure);
        pack.Structures.add(marker);
        pack.Structures.add(performanceInfo);
        return model;
    }


    public static void main(String[] args) {
        ObjectModelDefinition model = create();
        Generator generator = new Generator(model);
        generator.writeFiles("./src/", Generator.Target.Java5, false);

    }

}
