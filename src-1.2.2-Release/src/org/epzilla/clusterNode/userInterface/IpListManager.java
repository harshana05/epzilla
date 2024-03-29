package org.epzilla.clusterNode.userInterface;

import org.epzilla.leader.LeaderElectionInitiator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: Chathura
 * Date: May 31, 2010
 * Time: 2:14:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class IpListManager {

    public static void Initialize() {
        initClientIpList();
        initNodeIpList();
    }

    private static void initClientIpList() {
        final java.util.Timer timer1 = new java.util.Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                HashSet<String> ipList = LeaderElectionInitiator.getSubscribedNodeList();
                HashSet<String> nodeList = LeaderElectionInitiator.getNodes();

                String currentList = NodeUIController.getIpList();
                String cList = NodeUIController.getNodeList();

                if (ipList != null) {
                    for (Iterator i = ipList.iterator(); i.hasNext();) {
                        String ip = (String) i.next();
                        if (!currentList.contains(ip))
                            NodeUIController.appendTextToIPList(ip);
                    }
                }
                System.gc();
            }
        }, 10000, 60000);
    }

    private static void initNodeIpList() {
        final java.util.Timer timer1 = new java.util.Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                HashSet<String> nodeList = LeaderElectionInitiator.getNodes();
                String currentList = NodeUIController.getNodeList();

                if (nodeList != null) {
                    for (Iterator i = nodeList.iterator(); i.hasNext();) {
                        String ip = (String) i.next();
                        if (!currentList.contains(ip))
                            NodeUIController.appendTextToNodeList(ip);
                    }
                }
                System.gc();
            }
        }, 10000, 60000);
    }
}
