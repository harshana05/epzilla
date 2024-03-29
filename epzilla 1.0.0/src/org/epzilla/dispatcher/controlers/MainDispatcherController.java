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
package org.epzilla.dispatcher.controlers;

import org.epzilla.dispatcher.sharedMemoryModule.DispatcherAsServer;
import org.epzilla.dispatcher.sharedMemoryModule.DispatcherAsClient;
import org.epzilla.dispatcher.dataManager.ClusterLeaderIpListManager;
import org.epzilla.dispatcher.dataManager.TriggerManager;
import org.epzilla.dispatcher.dataManager.NodeVariables;


import java.net.UnknownHostException;
import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA.
 * User: Dishan
 * Date: Feb 18, 2010
 * Time: 3:33:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainDispatcherController {

//
//    public static void main(String[] args) {
//        try {
//            InetAddress addr = InetAddress.getLocalHost();
//
//            // Get IP Address
//            String ipAddr = addr.getHostAddress();
//            NodeVariables.setNodeIP(ipAddr);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        run();
//    }



    public static void run() {
        try {

           
            runAsClient();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void runAsServer() {
//        RandomStringGenerator.generate(1000);
        boolean IsSuccessful = DispatcherAsServer.startServer();
        if (IsSuccessful) {
            DispatcherAsServer.loadTriggers();
            DispatcherAsServer.loadIPList();
            DispatcherAsServer.loadClientList();
            DispatcherAsServer.loadPerformanceInfoList();
            DispatcherAsServer.checkForOverloading();
            //Code for testing ONLY
//        TriggerManager.initTestTriggerStream();
//        ClusterLeaderIpListManager.loadSampleIPs();
        }

    }

   public static void runAsClient() {
        DispatcherAsClient.startClient();
        DispatcherAsClient.checkServerStatus();
    }



}
