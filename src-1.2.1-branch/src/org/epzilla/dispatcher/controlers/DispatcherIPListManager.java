package org.epzilla.dispatcher.controlers;

import org.epzilla.common.discovery.dispatcher.DispatcherDiscoveryManager;
import org.epzilla.dispatcher.dataManager.ClusterLeaderIpListManager;
import org.epzilla.dispatcher.rmi.DispImpl;
import org.epzilla.leader.LeaderElectionInitiator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: Chathura
 * Date: May 31, 2010
 * Time: 3:33:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class DispatcherIPListManager {
    public static void Initialize() {
        addDispatcherIps();
        addClusterIps();
//        final java.util.Timer timer1 = new java.util.Timer();
//        timer1.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                DispatcherUIController.clearDispatcherIpList();
//                HashSet<String> ipList = LeaderElectionInitiator.getSubscribedNodeList();
//                if (ipList.size() == 0) {
//                    try {
//                        String currentList = DispatcherUIController.getIpList();
//                        InetAddress inetAddress = InetAddress.getLocalHost();
//                        String ipAddress = inetAddress.getHostAddress();
//                        if (!currentList.contains(ipAddress))
//                            DispatcherUIController.appendDispatcherIPs(ipAddress);
//                    } catch (UnknownHostException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    String currentList = DispatcherUIController.getIpList();
//                    for (Object dispList : ipList) {
//                        String ip = (String) dispList;
//                        if (!currentList.contains(ip))
//                            DispatcherUIController.appendDispatcherIPs(ip);
//                    }
//                    System.gc();
//                }
//            }
//        }, 5000, 30000);
    }

    public static void addDispatcherIps() {
        final java.util.Timer timer1 = new java.util.Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                DispatcherUIController.clearDispatcherIpList();
                HashSet<String> ipList = LeaderElectionInitiator.getSubscribedNodeList();
                if (ipList != null) {
                    if (ipList.size() == 0) {
                        try {
                            String currentList = DispatcherUIController.getIpList();
                            InetAddress inetAddress = InetAddress.getLocalHost();
                            String ipAddress = inetAddress.getHostAddress();
                            if (!currentList.contains(ipAddress))
                                DispatcherUIController.appendDispatcherIPs(ipAddress);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    } else {
                        String currentList = DispatcherUIController.getIpList();
                        for (Object dispList : ipList) {
                            String ip = (String) dispList;
                            if (!currentList.contains(ip))
                                DispatcherUIController.appendDispatcherIPs(ip);
                        }
                        System.gc();
                    }
                }
            }
        }, 5000, 30000);
    }

    public static void addClusterIps() {
        final java.util.Timer timer1 = new java.util.Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                Hashtable<Integer, String> leaders = DispatcherDiscoveryManager.getDispatcherPublisher().getSubscribers();
                for (int key : leaders.keySet()) {

                    ClusterLeaderIpListManager.removeIP(leaders.get(key));
                    ClusterLeaderIpListManager.addIP("" + key, leaders.get(key));
                }

                System.gc();

            }
        }, 5000, 30000);
    }
}
