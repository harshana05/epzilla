package org.epzilla.client.controlers;

import org.epzilla.client.xml.ClientTimeSettings;
import org.epzilla.dispatcher.rmi.DispInterface;
import org.epzilla.testObjectGenerator.EventTriggerGenerator;
import org.epzilla.util.Logger;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Team epZilla
 * Date: Mar 8, 2010
 * Time: 12:40:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientInit extends Thread {
    private static String clientID;
    private static DispInterface di;
    private static Thread trigger;
    private static Thread events;
    private static HashMap<String, Object> dispMap = new HashMap<String, Object>();
    private static volatile boolean isLive = true;
    private static int eventsSeqID = 1;
    private static boolean dynamicLookup = false;
    private static int sendingIntervalEvent;
    private static int initIntervalEvent;
    private static int initIntervalTrigger;
    private static int sendingIntervalTrigger;

    public ClientInit() {
    }

    public static void lookUp(String ip, String name) throws MalformedURLException, NotBoundException, RemoteException {
        if (!dispMap.containsKey(ip)) {
            String url = "rmi://" + ip + "/" + name;
            DispInterface di = (DispInterface) Naming.lookup(url);
            setDispatcherObj(di);
            dispMap.put(ip, di);
        }
    }

    public static void initSend(String ip, String name, String clientID) throws MalformedURLException, NotBoundException, RemoteException {
        lookUp(ip, name);
        ClientInit.clientID = clientID;
        isLive = true;
        dynamicLookup = false;
        loadSettings();
        initSendTriggerStream(ip);
        initSendEventsStream(ip);
        trigger.start();
        events.start();
        ClientUIControler.appendResults("Start Event/ Trigger sending process....");
    }

    public static void initSendTriggerStream(final String ip) {
        trigger = new Thread(new Runnable() {
            String response = null;
            int triggerSeqID = 1;

            @Override
            public void run() {
                try {
                    Thread.sleep(initIntervalTrigger);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                di = (DispInterface) dispMap.get(ip);

                ArrayList<String> triggers = new ArrayList<String>();
                for (int i = 0; i < 100; i++) {
                    triggers.add(EventTriggerGenerator.getNextTrigger());
                }
                try {
                    response = di.uploadTriggersToDispatcher(triggers, clientID, triggerSeqID);
                } catch (RemoteException e) {
                    ClientUIControler.appendResults("Connection to the Dispatcher service failed, trigger sending stoped, Perform Lookup operation..." + "\n");
                    isLive = false;
                }

                if (response != null) {
                    ClientUIControler.appendResults("Dispatcher Received the Trigger Stream" + "\n");
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                while (isLive) {
                    triggers = new ArrayList<String>();
                    for (int i = 0; i < 5; i++) {
                        triggers.add(EventTriggerGenerator.getNextTrigger());
                    }
                    try {
                        response = di.uploadTriggersToDispatcher(triggers, clientID, triggerSeqID);
                    } catch (RemoteException e) {
                        ClientUIControler.appendResults("Connection to the Dispatcher service failed, trigger sending stoped, Perform Lookup operation..." + "\n");
                        isLive = false;
                        initDLookup();
                    }

                    if (response != null) {
                        ClientUIControler.appendResults("Dispatcher Received the Trigger Stream" + "\n");
                    }
                    try {
                        Thread.sleep(sendingIntervalTrigger);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void initSendEventsStream(final String ip) {
        events = new Thread(new Runnable() {

            String response;

            @Override
            public void run() {
                try {
                    Thread.sleep(initIntervalEvent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                di = (DispInterface) dispMap.get(ip);

                while (isLive) {
                    String event = EventTriggerGenerator.getNextEvent();
                    try {
                        response = di.uploadEventsToDispatcher(event, clientID, eventsSeqID);
                        eventsSeqID++;
                        try {
                            Thread.sleep(sendingIntervalEvent);
                        } catch (InterruptedException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    } catch (RemoteException e) {
                        isLive = false;
                        ClientUIControler.appendResults("Connection to the Dispatcher service failed, events sending stoped, Perform Lookup operation.." + "\n");
                        initDLookup();
                    }

                    if (response != null) {
                        Logger.log("Dispatcher Recieved the events from the client and the response is " + response);
                    }
                }
            }
        });
    }

    private static void initDLookup() {
//        if (!dynamicLookup) {
//            DynamicLookup.dynamicLookup();
//            dynamicLookup = true;
//        }
    }

    private static void loadSettings() {
        ArrayList<String[]> settingsList = ClientTimeSettings.getClientTimeIntervals("client_timeIntervals.xml");
        String[] settings = settingsList.get(0);
        if (settings != null) {
            initIntervalEvent = Integer.valueOf(settings[0]);
            sendingIntervalEvent = Integer.valueOf(settings[1]);
            initIntervalTrigger = Integer.valueOf(settings[2]);
            sendingIntervalTrigger = Integer.valueOf(settings[3]);
        }
    }

    public static void stopEventTriggerStream() {
        isLive = false;
    }

    private static void setDispatcherObj(Object obj) {
        di = (DispInterface) obj;
    }
}
