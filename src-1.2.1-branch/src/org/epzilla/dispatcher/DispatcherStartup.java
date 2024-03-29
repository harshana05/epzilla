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
package org.epzilla.dispatcher;

import net.epzilla.stratification.restruct.RestructuringDaemon;
import org.epzilla.dispatcher.controlers.DispatcherUIController;
import org.epzilla.dispatcher.controlers.MainDispatcherController;
import org.epzilla.dispatcher.dataManager.NodeVariables;
import org.epzilla.dispatcher.loadAnalyzer.CpuMemAnalyzer;
import org.epzilla.dispatcher.rmi.DispImpl;
import org.epzilla.dispatcher.rmi.DispInterface;
import org.epzilla.dispatcher.rmi.OpenSecurityManager;
import org.epzilla.leader.LeaderElectionInitiator;
import org.epzilla.util.Logger;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;

/**
 * Created by IntelliJ IDEA.
 * This class is to bind the Dispatcher to its RMI registry
 * Initialize the Leader Election process
 * Author: Chathura
 * Date: Jun 16, 2010
 * Time: 5:36:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class DispatcherStartup {
    private static String serviceName = "DISPATCHER_SERVICE";
    private static String STMserverIP = "127.0.0.1";
    /*
    * bind the dispatcher to its own RMI registry
    */

    private void bindDispatcher(String serviceName) throws RemoteException, UnknownHostException, MalformedURLException {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new OpenSecurityManager());
        }
        DispInterface dispInt = new DispImpl();
        InetAddress inetAddress;
        inetAddress = InetAddress.getLocalHost();

        String ipAddress = inetAddress.getHostAddress();
        NodeVariables.setNodeIP(ipAddress);
        String id = dispIdGen(ipAddress);
        String disServiceName = serviceName + id;
        String name = "rmi://" + ipAddress + "/" + disServiceName;
        Naming.rebind(name, dispInt);
    }

    /*
      * generate dispatcher id
      */

    private String dispIdGen(String addr) {
        String[] addrArray = addr.split("\\.");
        String temp = "";
        String value = "";
        for (int i = 0; i < addrArray.length; i++) {
            temp = addrArray[i];
            while (temp.length() != 3) {
                temp = '0' + temp;
            }
            value += temp;
        }
        return value;
    }

    public static void main(String[] args) {
        try {
            DispatcherStartup main = new DispatcherStartup();
            main.bindDispatcher(serviceName);

            //added leader election init
            LeaderElectionInitiator.mainMethod();


//        LeaderElectionInitiator.mainMethod();
            DispatcherUIController.InitializeUI();
            InitSTM();


            CpuMemAnalyzer.Initialize();
            //To run dispatcher as STM client
            //MainDispatcherController.runAsClient();
            //Logger.log("running as client...");


        } catch (Exception e) {
            Logger.error("", e);
        }
    }

    public static void InitSTM() {
        String leader = "";
        while (leader.equalsIgnoreCase("")) {
            leader = LeaderElectionInitiator.getLeader();

        }
        if (leader.equalsIgnoreCase(NodeVariables.getNodeIP())) {

            //To run as Dispatcher as STM server
            DispatcherUIController.setUIVisibility(true);
            MainDispatcherController.runAsServer();
            Logger.log("running as server...");
            try {
                if (RestructuringDaemon.isRestructuring()) {
                    RestructuringDaemon.forceRestructuring();
                }
            } catch (Exception e) {
                Logger.error("", e);
            }

            RestructuringDaemon.start();

        } else {

            DispatcherUIController.setUIVisibility(true);
            NodeVariables.setCurrentServerIP(leader);
            MainDispatcherController.runAsClient();
        }
    }

    public static boolean triggerLEFromRemote() {
        return LeaderElectionInitiator.initiateLeaderElection();
    }
}
