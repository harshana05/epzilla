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
package org.epzilla.leader.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.epzilla.leader.Epzilla;
import org.epzilla.leader.EpzillaLeaderPubSub;
import org.epzilla.leader.event.listner.IEpzillaEventListner;
import org.epzilla.leader.message.RejectReason;
import org.epzilla.leader.message.RmiMessageClient;
import org.epzilla.leader.message.RmiMessageHandler;

/**
 * This is the LeaderService implementation class. 
 * @author Harshana Eranga Martin 	 mailto: harshana05@gmail.com
 *
 */
public class LeaderServiceImpl extends UnicastRemoteObject implements LeaderInterface {
	
	private RmiMessageHandler messageHandler;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5270364201519480615L;

	public LeaderServiceImpl() throws RemoteException {
		super();
		messageHandler=new RmiMessageHandler();		
	}

	@Override
	public void addListener(final IEpzillaEventListner listener)
			throws RemoteException {
		final boolean result=EpzillaLeaderPubSub.addClientListner(listener);		
		Thread pulsator=new Thread(new Runnable() {
			public void run() {
				if(result)
					RmiMessageClient.sendPulse(listener.getData());
				else
					RmiMessageClient.sendRequestNotAccepted(listener.getData(), RejectReason.NOT_ALLOWED_TO_REGISTER_LISTNER_NOT_LEADER);				
			}
		});
		pulsator.start();
	}

	@Override
	public String getClusterLeader() throws RemoteException {
		return Epzilla.getClusterLeader();
	}

	@Override
	public String getStatus() throws RemoteException {
		return Epzilla.getStatus();
	}

	@Override
	public long getUID() throws RemoteException {
		return Epzilla.getUID();
	}

	@Override
	public boolean isDefaultLeader() throws RemoteException {
		return Epzilla.isDefaultLeader();
	}

	@Override
	public boolean isLeader() throws RemoteException {
		return Epzilla.isLeader();
	}

	@Override
	public boolean isLeaderElectionRunning() throws RemoteException {
		return Epzilla.isLeaderElectionRunning();
	}

	@Override
	public void receiveMessage(String message) throws RemoteException {
		messageHandler.enqueueMessage(message);
	}

	@Override
	public void removeListener(IEpzillaEventListner listener)
			throws RemoteException {
		EpzillaLeaderPubSub.removeClientListner(listener);
	}

}
