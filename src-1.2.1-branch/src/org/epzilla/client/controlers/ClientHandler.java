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
package org.epzilla.client.controlers;

import org.epzilla.client.rmi.ClientCallbackImpl;
import org.epzilla.client.rmi.ClientCallbackInterface;
import org.epzilla.dispatcher.rmi.DispInterface;
import org.epzilla.nameserver.NameService;
import org.epzilla.util.Logger;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Vector;
/**
 * Created by IntelliJ IDEA.
 * This class use to handle the client side operations, and creating RMI connections
 * to the NameServer and Dispatchers.
 * Author: Chathura
 * Date: Jan 18, 2010
 * Time: 11:12:36 PM
 */
public class ClientHandler {

    private static Vector<String> dispIP = new Vector<String>();
    private static String dispDetails = "";
    private static ClientCallbackInterface obj;
    private static NameService service;
    private static DispInterface disObj;

    /**
     * This method get is use to get the Dispatcher information from the NameServer
     * @param serverIp
     * @param serviceName
     * @param clientIp
     * @return
     * @throws MalformedURLException
     * @throws RemoteException
     * @throws NotBoundException
     */
    public static Vector<String> getServiceIp(String serverIp, String serviceName, String clientIp) throws MalformedURLException, RemoteException, NotBoundException {
        dispIP.removeAllElements();
        initNameService(serverIp, serviceName);
        try {
            dispDetails = service.getDispatcherIP();
            if (!dispDetails.equals(" ")) {
                dispIP.add(dispDetails);
            }
        } catch (Exception e) {
            Logger.error("URL error:", e);
        }
        Logger.log(dispIP);
        return dispIP;
    }

    /**
     * Get client id generated from the Name Server
     * @param ip
     * @return
     * @throws RemoteException
     */
    public static String getClientsID(String ip) throws RemoteException {
        return service.getClientID(ip);
    }

    /**
     * Client register for call backs from the Dispatcher
     * @param ip
     * @param serviceName
     * @throws NotBoundException
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws UnknownHostException
     */
    public void regForCallback(String ip, String serviceName) throws NotBoundException, RemoteException, MalformedURLException, UnknownHostException {
        boolean dispatcherInit = false;
        if (!dispatcherInit) {
            initDispatcher(ip, serviceName);
            ClientCallbackInterface obj = new ClientCallbackImpl();
            disObj.registerCallback(obj);
            setClientObject(obj);
        } else if (dispatcherInit) {
            ClientCallbackInterface obj = new ClientCallbackImpl();
            disObj.registerCallback(obj);
            setClientObject(obj);
        }
    }
    /*
   Client unregistering from call backs
    */

    public void unregisterCallback(String ip, String serviceName) throws MalformedURLException, RemoteException, NotBoundException, UnknownHostException {
        disObj.unregisterCallback((ClientCallbackInterface) getclientObject());
    }

    /**
     * Client register in the Dispatcher
     * @param ip
     * @param id
     * @throws RemoteException
     */
    public static void registerClient(String ip, String id) throws RemoteException {
        disObj.registerClients(ip, id);
    }

    /**
     *  Client unregister from the Dispatcher
     * @param ip
     * @param id
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws UnknownHostException
     * @throws NotBoundException
     */
    public static void unRegisterClient(String ip, String id) throws RemoteException, MalformedURLException, UnknownHostException, NotBoundException {
        disObj.unRegisterClients(ip, id);
    }

    /**
     * Initialize the reference to the Name Service, create remote object
     * @param serverIp
     * @param serviceName
     * @throws MalformedURLException
     * @throws RemoteException
     * @throws NotBoundException
     */
    private static void initNameService(String serverIp, String serviceName) throws MalformedURLException, RemoteException, NotBoundException {
        String url = "rmi://" + serverIp + "/" + serviceName;
        NameService r = (NameService) Naming.lookup(url);
        setNameServiceObj(r);
    }

    /**
     * Initialize Dispatcher service, create remote object
     * @param ip
     * @param serviceName
     * @throws MalformedURLException
     * @throws RemoteException
     * @throws NotBoundException
     */
    private static void initDispatcher(String ip, String serviceName) throws MalformedURLException, RemoteException, NotBoundException {
        String url = "rmi://" + ip + "/" + serviceName;
        DispInterface di = (DispInterface) Naming.lookup(url);
        setDispatcherObj(di);
    }

    public static void setClientObject(Object objClient) {
        obj = (ClientCallbackInterface) objClient;
    }

    public static Object getclientObject() {
        return obj;
    }

    public static void setNameServiceObj(Object objService) {
        service = (NameService) objService;
    }

    public static Object getNameSerObj() {
        return service;
    }

    public static void setDispatcherObj(Object obj) {
        disObj = (DispInterface) obj;
    }

    public static Object getDispatcherObj() {
        return disObj;
    }

}
