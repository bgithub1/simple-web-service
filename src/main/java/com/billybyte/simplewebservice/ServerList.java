package com.billybyte.simplewebservice;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerList {
	private static final Map<String,SimpleService> fullAddressToServiceMap = new ConcurrentHashMap<String,SimpleService>();
	private static final Map<SimpleService,String> serviceInstanceTofullAddress = new ConcurrentHashMap<SimpleService,String>();
	
	static final SimpleService getInProcessService(String fullAddress){
		return fullAddressToServiceMap.get(fullAddress);
	}


	static String getFullAddress(String ip, String port){
		String fullAddress = ip+":"+port+"/simple";
		if(!fullAddress.toLowerCase().contains("http://")){
			fullAddress = "http://"+fullAddress;
		}
		return fullAddress;
	}
	
	public static final void registerInProcessService(String ip, String port, SimpleService serviceToAdd){
		fullAddressToServiceMap.put(getFullAddress(ip,port), serviceToAdd);
		serviceInstanceTofullAddress.put(serviceToAdd, getFullAddress(ip,port));
	}
	
	static final String getFullAddress(SimpleService serviceInstance){
		return serviceInstanceTofullAddress.get(serviceInstance);
	}
	
}
