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
package org.epzilla.common.discovery.dispatcher;

import java.util.HashSet;
import java.util.Hashtable;

import org.epzilla.common.discovery.Constants;
import org.epzilla.common.discovery.IServicePublisher;
import org.epzilla.common.discovery.multicast.*;

/**
 * This is the dispatcher service publisher and all the diapcthers use this class to maintain the details about
 * other dispatchers and cluster leaders. 
 * @author Harshana Eranga Martin
 *
 */
public class DispatcherPublisher implements IServicePublisher {
	private static String DISPATCHER_SERVICE_NAME="DISPATCHER_SERVICE";
    private static String SUBSCRIBE_PREFIX="SUBSCRIBE_";
    private static String UNSUBSCRIBE_PREFIX="UNSUBSCRIBE_";
	private String multicastGroupIp="224.0.0.2";
	private int multicastPort=5005;
	private Hashtable<Integer, String> clusterLeaderIp=new Hashtable<Integer, String>();
	private HashSet<String> dispatcherList=new HashSet<String>();
	
	public DispatcherPublisher() {
	}

	public boolean addSubscription(String serviceClient, String serviceName) {
		if(serviceName.equalsIgnoreCase(SUBSCRIBE_PREFIX+DISPATCHER_SERVICE_NAME)){
			synchronized (clusterLeaderIp) {
				String  []arr=serviceClient.split(Constants.DISPATCHER_CLIENT_DELIMITER);
				clusterLeaderIp.put(Integer.parseInt(arr[0]), arr[1]);
				System.out.println("New Cluster Leader Subscribed: Cluster ID="+arr[0]+" IP="+arr[1]);
				return true;
			}
		}
			return false;
	}

	public boolean publishService() {
		MulticastSender broadcaster=new MulticastSender(multicastGroupIp,multicastPort);
		broadcaster.broadcastMessage(DISPATCHER_SERVICE_NAME);
		return true;
	}

	public boolean removeSubscrition(String serviceClient, String serviceName) {
		if(serviceName.equalsIgnoreCase(UNSUBSCRIBE_PREFIX+DISPATCHER_SERVICE_NAME)){
			synchronized (clusterLeaderIp){
				clusterLeaderIp.remove(Integer.parseInt(serviceClient.split(Constants.DISPATCHER_CLIENT_DELIMITER)[0]));
				return true;
			}
		}		
		return false;
	}
	
	public boolean removeLeaderSubscrition(String serviceClient, String serviceName) {
		if(serviceName.equalsIgnoreCase(UNSUBSCRIBE_PREFIX+DISPATCHER_SERVICE_NAME)){
			synchronized (clusterLeaderIp){
				clusterLeaderIp.remove(serviceClient);
				return true;
			}
		}		
		return false;
	}
	
	public boolean insertDispatcher(String dispatcherIp){
		synchronized (dispatcherList) {
			dispatcherList.add(dispatcherIp);
			System.out.println("New Dispatcher Discovered: "+dispatcherIp);
			return true;
		}
	}
	
	public boolean removeDispatcher(String dispatcherIp){
		synchronized (dispatcherList) {
			dispatcherList.remove(dispatcherIp);
			return true;
		}
	}
	
	public Hashtable<Integer, String> getSubscribers(){
		return clusterLeaderIp;
	}

	public HashSet<String> getDispatchers(){
		return dispatcherList;
	}
}
