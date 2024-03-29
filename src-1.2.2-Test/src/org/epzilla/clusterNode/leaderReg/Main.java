package org.epzilla.clusterNode.leaderReg;

import org.epzilla.clusterNode.NodeController;
import org.epzilla.clusterNode.rmi.ClusterImpl;
import org.epzilla.clusterNode.rmi.ClusterInterface;
import org.epzilla.clusterNode.xml.ClusterSettingsReader;
import org.epzilla.dispatcher.rmi.DispInterface;
import org.epzilla.leader.LeaderElectionInitiator;
import org.epzilla.util.Logger;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Main {
    private static ClusterSettingsReader reader = new ClusterSettingsReader();
    private static int clusterID;
    private static String nodeStatus;
    private static String serviceName = "CLUSTER_NODE";
    private static DispInterface disObj;
    private static String ipAddress;
    private static LeaderElectionInitiator leaderElectionInitiator;

    public static void bindClusterNode(String serviceName) throws UnknownHostException, MalformedURLException, RemoteException {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new org.epzilla.clusterNode.leaderReg.OpenSecurityManager());
        }
        ClusterInterface clusterInt = new ClusterImpl();
        InetAddress inetAddress;
        inetAddress = InetAddress.getLocalHost();
        String ipAddress = inetAddress.getHostAddress();
        String name = "rmi://" + ipAddress + "/" + serviceName;
        Naming.rebind(name, clusterInt);
        Logger.log("Cluster Node successfully deployed.....");
    }

    private static void register() throws RemoteException, MalformedURLException, NotBoundException, UnknownHostException {
        String url = "rmi://" + "192.168.1.4" + "/" + "DISPATCHER_SERVICE192168001004";
        DispInterface service;
        service = (DispInterface) Naming.lookup(url);
        InetAddress inetAddress = InetAddress.getLocalHost();
        String ipAddress = inetAddress.getHostAddress();
        service.getLeaderIp(clusterID, ipAddress);      //cluster ID taken from the setting file clusterID_settings
        setDispObject(service);


        //DD for Client
//        org.epzilla.common.discovery.node.NodeDiscoveryManager nodeDiscMgr=new NodeDiscoveryManager(2);
//        NodeDiscoveryManager.setLeader(true);
//        NodeDiscoveryManager.setClusterLeader(InetAddress.getLocalHost().getHostAddress());
    }

    public static void setDispObject(Object obj) {
        disObj = (DispInterface) obj;
    }

    public static Object getDispObject() {
        return disObj;
    }

    //method to send performance info
    public static void sendInfo(int cpuUsg, int mmUsg) {
        try {
            disObj.performanceInfo(clusterID, cpuUsg, mmUsg);   //cluster ID taken from the setting file clusterID_settings
        } catch (RemoteException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private static void loadSettings() {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
            ipAddress = inetAddress.getHostAddress();
            NodeController.setThisIP(ipAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

//        try {
//            ArrayList<String[]> data = reader.getServerIPSettings("./src/settings/clusterID_settings.xml");
//            String[] ar = data.get(0);
//            String ID = ar[0];
//            nodeStatus = ar[1];
//            clusterID = Integer.parseInt(ID);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static void main(String[] args) {
        try {
            bindClusterNode(serviceName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        loadSettings();
//            if (nodeStatus == "default") {
//                initSTM();
//            }

        LeaderElectionInitiator.mainMethod();
        NodeController.initUI();
        startSTM();

//            register();
    }

    public static boolean triggerLEFromRemote() {
        return LeaderElectionInitiator.initiateLeaderElection();
    }


    public static void startSTM() {
//        LeaderElectionInitiator.mainMethod();
        String leader = "192.168.182.4";
        while (leader.equalsIgnoreCase("")) {
            leader = LeaderElectionInitiator.getLeader();
        }
        if (leader.equalsIgnoreCase(ipAddress)) {
            NodeController.setLeader(true);
            NodeController.setUiVisible();
            NodeController.initSTM();
        } else {
            NodeController.setLeader(false);
            NodeController.setLeaderIP(leader);
            NodeController.setUiVisible();
            NodeController.initSTM();
        }

    }

}
