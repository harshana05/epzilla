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
package org.epzilla.clusterNode.rmi;

import jstm.core.Site;
import jstm.core.TransactedList;
import jstm.core.Transaction;
import org.epzilla.clusterNode.NodeController;
import org.epzilla.clusterNode.accConnector.DeriveEventSender;
import org.epzilla.clusterNode.clusterInfoObjectModel.TriggerObject;
import org.epzilla.clusterNode.dataManager.EventsCounter;
import org.epzilla.clusterNode.dataManager.EventsManager;
import org.epzilla.clusterNode.dataManager.TriggerManager;
import org.epzilla.clusterNode.processor.EventProcessor;
import org.epzilla.clusterNode.userInterface.NodeUIController;
import org.epzilla.dispatcher.rmi.TriggerRepresentation;
import org.epzilla.util.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * This class is the implementation of the Cluster Implementation
 * Author: Chathura
 * Date: Mar 11, 2010
 * Modified: May 11, 2010
 * Time: 10:49:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class ClusterImpl extends UnicastRemoteObject implements ClusterInterface {

    public static String[] accIpArray = null;

    static {
        try {
            File f = new File("Accumulators.xml");
            BufferedReader br = new BufferedReader(new FileReader(f));

            org.epzilla.clusterNode.xml.XMLElement xe = new org.epzilla.clusterNode.xml.XMLElement();
            StringBuilder sb = new StringBuilder("");
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            xe.parseString(sb.toString());
            ArrayList<org.epzilla.clusterNode.xml.XMLElement> ch = xe.getChildren();
            accIpArray = new String[ch.size()];
            int i = 0;
            for (org.epzilla.clusterNode.xml.XMLElement x : ch) {
                String ip = x.getAttribute("ip");
                accIpArray[i] = ip;
                i++;
            }

            System.out.println("accumulators:" + Arrays.toString(accIpArray));
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("running.");
        for (String x : accIpArray) {
            System.out.println(x);
        }
    }

    public ClusterImpl() throws RemoteException {
    }

    /*
   Accept trigger stream by Leader node
    */

//    public String acceptTiggerStream(ArrayList<TriggerRepresentation> tList, String clusterID, String clientID) throws RemoteException {
//        try {
//            for (int i = 0; i < tList.size(); i++) {
//                TriggerManager.addTriggerToList(tList.get(i), clientID, "0");
//            }
//            return "OK";
//        } catch (Exception e) {
//            System.err.println("Trigger adding failure");
//        }
//        return null;
//    }

    @Override
    public String acceptTiggerStream(List<TriggerRepresentation> tList) throws RemoteException {
        try {
            for (TriggerRepresentation tr : tList) {
                TriggerManager.addTriggerToList(tr);
            }
            return "OK";
        } catch (Exception e) {
            System.err.println("Trigger adding failure");
        }
        return null;


    }


    /**
     * events from dispatcher.
     *
     * @param event
     * @param clusterID
     * @return
     * @throws RemoteException
     */
    public String acceptEventStream(byte[] event, String clusterID) throws RemoteException {
        try {
            String eventS = new String(event);
            EventsManager.addEvents(eventS);
            EventsCounter.setInEventCount();
            Logger.log(eventS);
            return "OK";
        } catch (Exception e) {
            Logger.log("Events adding failure");
        }
        return null;
    }

    // todo - add acc. ip

    /**
     * events from cluster leaders.
     *
     * @param event
     * @throws RemoteException
     */
    public void addEventStream(String event) throws RemoteException {
        try {
             EventsCounter.setInEventCount();
            for (String aip : accIpArray) {
          String result =      EventProcessor.getInstance().processEvent(event);
                DeriveEventSender.sendDeriveEvent(aip, result.getBytes());

            }
        } catch (Exception e) {
            Logger.error("error adding event in cluster node", e);
        }

    }

    public boolean deleteTriggers(ArrayList<TriggerRepresentation> list, String clusterID, String clientID) throws RemoteException {
        // trigger deleting logic here

        try {
//        TriggerManager.triggerAdding().get(1)
            List<TriggerObject> toRemoveList = new LinkedList();
            TransactedList<TriggerObject> tlist = TriggerManager.getTriggers();

            for (TriggerObject to : tlist) {


                boolean del = false;
                for (TriggerRepresentation t : list) {
                    if (t.getClientId().equals(to.getclientID()) && t.getTriggerId().equals(to.gettriggerID())) {
                        del = true;
                        break;
                    }
                }

                if (del) {
                    toRemoveList.add(to);
                }
            }

            if (Site.getLocal().getPendingCommitCount() < Site.MAX_PENDING_COMMIT_COUNT) {
                Site.getLocal().allowThread();
                Transaction transaction = Site.getLocal().startTransaction();
                TriggerManager.getTriggers().removeAll(toRemoveList);
                transaction.commit();
            } else {
                return false;
            }


        } catch (Exception e) {
            return false;
        }


        return true;
    }

    @Override
    public void initNodeProcess() throws RemoteException {
        // init UI of the processing node
        NodeController.initUI();
        NodeController.setUiVisible();
        NodeController.initSTM();
    }

    @Override
    public void sleepNodeProcess() throws RemoteException {
        NodeUIController.deactiveUI();
    }
//
//    @Override
//    public boolean deleteTriggers(HashMap<String, ArrayList<String>> rep) throws RemoteException {
//        try {
////        TriggerManager.triggerAdding().get(1)
//            List<TriggerObject> toRemoveList = new LinkedList();
//            TransactedList<TriggerObject> tlist = TriggerManager.triggerAdding();
//
//            for (TriggerObject to : tlist) {
//                ArrayList<String> tr = rep.get(to.getclientID());
//                if (tr != null) {
//                    if (tr.contains(to.gettrigger())) {
//                        toRemoveList.add(to);
//                    }
//                }
//            }
//
//            if (Site.getLocal().getPendingCommitCount() < Site.MAX_PENDING_COMMIT_COUNT) {
//                Site.getLocal().allowThread();
//                Transaction transaction = Site.getLocal().startTransaction();
//                TriggerManager.triggerAdding().removeAll(toRemoveList);
//                transaction.commit();
//            } else {
//                return false;
//            }
//
//
//        } catch (Exception e) {
//            return false;
//        }
//        return true;  //To change body of implemented methods use File | Settings | File Templates.
//    }

}
