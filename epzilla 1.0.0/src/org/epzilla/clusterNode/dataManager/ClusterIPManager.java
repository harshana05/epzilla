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
package org.epzilla.clusterNode.dataManager;

import jstm.core.Site;
import jstm.core.TransactedList;
import jstm.core.Transaction;
import org.epzilla.clusterNode.clusterInfoObjectModel.NodeIPObject;
import org.epzilla.util.Logger;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Dishan
 * Date: Mar 4, 2010
 * Time: 10:24:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class ClusterIPManager {
    private static TransactedList<NodeIPObject> ipList = new TransactedList<NodeIPObject>();
    static int count = 0;


    public static void addIP(String clusterID, String ip) {
        if (getIpList() != null) {
            if (Site.getLocal().getPendingCommitCount() < Site.MAX_PENDING_COMMIT_COUNT) {
                Site.getLocal().allowThread();
                Transaction transaction = Site.getLocal().startTransaction();
                NodeIPObject obj = new NodeIPObject();
                obj.setIP(ip);
                obj.setclusterID(clusterID);
                getIpList().add(obj);
                transaction.commit();
                count++;
            }
        }
    }

    public static void setNodeStatus(String nodeIP, boolean status) {
        if (getIpList() != null) {
            if (Site.getLocal().getPendingCommitCount() < Site.MAX_PENDING_COMMIT_COUNT) {
                Site.getLocal().allowThread();
                Transaction transaction = Site.getLocal().startTransaction();
                for (int i = 0; i < getIpList().size(); i++) {
                    if (getIpList().get(i).getIP().equals(nodeIP)) {
                        getIpList().get(i).setIsActive(status);
                        break;
                    }
                }
                transaction.commit();

            }
        }
    }

    public static boolean getNodeStatus(String nodeIP) {
        boolean status = false;
        synchronized (ipList) {
            if (getIpList() != null && nodeIP != null) {
                for (int i = 0; i < getIpList().size(); i++) {
                    if (getIpList().get(i).getIP().equals(nodeIP)) {
                        status = getIpList().get(i).getIsActive();
                        break;
                    }
                }
            }
        }
        return status;
    }


    public static void removeIP(String ip) {
        if (getIpList() != null) {
            if (Site.getLocal().getPendingCommitCount() < Site.MAX_PENDING_COMMIT_COUNT) {
                Site.getLocal().allowThread();
                Transaction transaction = Site.getLocal().startTransaction();
                for (int i = 0; i < getIpList().size(); i++) {
                    if (getIpList().get(i).getIP().equals(ip)) {
                        getIpList().remove(i);
                        break;
                    }
                }
                transaction.commit();
                count++;
            }
        }
    }
    /*
   method to clear the cluster ip list
    */

    public static void clearIPList() {
        if (getIpList() != null) {
            try {
                if (Site.getLocal().getPendingCommitCount() < Site.MAX_PENDING_COMMIT_COUNT) {
                    Site.getLocal().allowThread();
                    Transaction transaction = Site.getLocal().startTransaction();
                    ipList.clear();
                    transaction.commit();
                }
            } catch (Exception e) {
                Logger.error("", e);
            }
        }
    }
    //add by chathura
 static   ArrayList<String> ipArr = new ArrayList<String>();

    public static ArrayList<String> getNodeIpList() {
        if (getIpList() != null) {
            for (int i = 0; i < ipList.size(); i++) {
                if (!"IP".equalsIgnoreCase(ipList.get(i).getIP())) {
                    ipArr.add(ipList.get(i).getIP());
                    System.out.println("adding ip to rr list:" + ipList.get(i).getIP());
                }
            }
        }
        return ipArr;
    }

    public static TransactedList<NodeIPObject> getIpList() {
        return ipList;
    }

    public static void setIpList(TransactedList<NodeIPObject> ipList) {
        ClusterIPManager.ipList = ipList;
    }
}
