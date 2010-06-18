package org.epzilla.clusterNode.nodeControler;

import org.epzilla.clusterNode.NodeController;
import org.epzilla.clusterNode.rmi.ClusterInterface;
import org.epzilla.leader.LeaderElectionInitiator;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: chathura
 * Date: May 25, 2010
 * Time: 1:45:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class WakeNode {
    private static String serviceName = "CLUSTER_NODE";

    public static void wake() {
        try {
            HashSet<String> nodeList = new HashSet<String>();
            nodeList = LeaderElectionInitiator.getNodes();
            if (nodeList.size() < 3) {
                String leaderIP = NodeController.getLeaderIP();
                for (Iterator i = nodeList.iterator(); i.hasNext();) {
                    String ip = (String) i.next();
                    if (!ip.equalsIgnoreCase(leaderIP)) {
                        nodeInit(ip);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void nodeInit(String nodeIP) {
        try {
            ClusterInterface nodeObj = initService(nodeIP, serviceName);
            nodeObj.initNodeProcess();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static ClusterInterface initService(String serverIp, String serviceName) throws MalformedURLException, NotBoundException, RemoteException {
        String url = "rmi://" + serverIp + "/" + serviceName;
        return (ClusterInterface) Naming.lookup(url);
    }
}
