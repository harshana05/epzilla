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
package org.epzilla.accumulator.service;


import jstm.core.TransactedList;
import org.epzilla.accumulator.dataManager.EventManager;
import org.epzilla.accumulator.generated.SharedDerivedEvent;
import org.epzilla.accumulator.global.DerivedEvent;
import org.epzilla.accumulator.notificationSys.ClientNotifier;
import org.epzilla.accumulator.notificationSys.NotificationManager;
import org.epzilla.accumulator.stm.STMAccess;
import org.epzilla.accumulator.userinterface.AccumulatorUIControler;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: Rajeev
 * Date: Feb 28, 2010
 * Time: 7:59:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class AccumulatorServiceImpl extends UnicastRemoteObject implements AccumulatorService {

    public AccumulatorServiceImpl() throws RemoteException {
//        super(0);

    }

    public boolean receiveDerivedEvent(DerivedEvent event) throws RemoteException {
//        System.out.println("accumulator service was called...!");

        // todo -
        // add to stm
//        TransactedList<SharedDerivedEvent> list = STMAccess.clientMap.get(event.getClientId());
//        if (list == null) {
//            list = new TransactedList<SharedDerivedEvent>();
//            STMAccess.clientMap.put(event.getClientId(), list);
//        }
//        list.add(EventConverter.toSharedDerivedEvent(event));


        return true;
        // notify dispatcher
        // notify client. - done by dispatcher.

//        return false;
    }

    public boolean isEventAvailable(long srcId, int clientId) throws RemoteException {
        TransactedList<SharedDerivedEvent> list = STMAccess.clientMap.get(clientId);

        if (list != null) {
            DerivedEvent de = new DerivedEvent();
            de.setSrcId(srcId);
            de.setClientId(clientId);
            return list.contains(de);
        }

        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    int count = 0;

    public void receiveDeriveEvent(byte[] deriveEvent) throws RemoteException {
        String eventS = new String(deriveEvent);
                                    // todo uncomment.
//        EventManager.setEventSegement(eventS);
//        count++;
        AccumulatorUIControler.appendEventResults(eventS);
        NotificationManager.setAlertCount();  // set count of the processed events

        //add logic to send the result to the client
//        StringTokenizer st = new StringTokenizer(eventS, ":");
//        String result = st.nextToken();  //trigger
//        String clientID = st.nextToken(); //client ID

        ClientNotifier.addAlertMessage(eventS);
    }
}
