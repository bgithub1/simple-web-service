package com.billybyte.simplewebservice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.management.ManagementFactory;


import javax.jws.WebService;

import com.thoughtworks.xstream.XStream;

@WebService(endpointInterface="com.billybyte.simplewebservice.SimpleService")   
public class SimpleServiceImpl<K,T> implements SimpleService {
	private final XStream xstream;
	private final SimpleWebServiceProcessRequestInterface<K, T> requestProcessor;
//	private final T nullReturn;
	public SimpleServiceImpl(XStream xstream,
			SimpleWebServiceProcessRequestInterface<K, T> requestProcessor/*, T nullReturn*/ ){
		this.xstream = xstream;
		this.requestProcessor = requestProcessor;
//		this.nullReturn = nullReturn;
	}
	
	public XStream getXstream(){
		return xstream;
	}
	
	   
	@SuppressWarnings("unchecked")
	@Override
	public byte[] getXml(byte[] xml) {
		byte[] retBytes=null;
		T ret  = null;
		
		try {
			if(isNullData(xml))return returnNull();
//			ByteInputStream bIn = new ByteInputStream(xml, xml.length);
			ByteArrayInputStream bIn = new ByteArrayInputStream(xml,0,xml.length);
			K requestKey = (K) getXstream().fromXML(bIn);
//			System.out.println("debug 2");
			try {
				ret = requestProcessor.processRequest(requestKey, xstream);
			} catch (Exception e) {
				System.out.println(this.getClass()+ " " + e.getMessage());
			}
//			ByteOutputStream bOut = new ByteOutputStream();
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			if(ret==null){
				return returnNull();
			}
			getXstream().toXML(ret, bOut);
//			retBytes = bOut.getBytes();
			retBytes = bOut.toByteArray();
			return retBytes;
		} catch (Exception e) {
			// DO NOTHING
			System.err.println(this.getClass()+" error processing "+ret.toString());
			System.err.println(this.getClass()+" "+e.getMessage());
			return returnNull();
		}

		
	}

	static byte[] returnNull(){
		byte[] retBytes = new byte[1];
		retBytes[0]=0;
		return retBytes;
		
	}
	
	static boolean isNullData(byte[] returnedData){
		if(returnedData==null) return true;
		if(returnedData.length==1 && returnedData[0]==0)return true;
		return false;
	}
	
	@Override
	public String getRunTimeMxBeanName() {
		return ManagementFactory.getRuntimeMXBean().getName();
	}

	@Override
	public String getFullServerAddress() {
		return ServerList.getFullAddress(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String[] getStrings(String[] xml) {
		String[] returnString ={"Null String"};
		T ret  = null;
		System.out.println(this.getClass().getName()+" xml = "+xml);
		
		try {
			if(xml==null )return returnString;
			if(xml.length<1) return returnString;
			returnString = new String[xml.length];
			try {
				for(int i = 0;i<xml.length;i++){
					String key= xml[i];
					K requestKey = (K) getXstream().fromXML(key);
					ret = requestProcessor.processRequest(requestKey, xstream);
					if(ret==null){
						returnString[i]="Null Data";
					}else{
						returnString[i] = ret.toString();
					}
					
				}
			} catch (Exception e) {
				System.out.println(this.getClass()+ " " + e.getMessage());
			}
			return returnString;
		} catch (Exception e) {
			// DO NOTHING
			System.err.println(this.getClass()+" error processing "+ret.toString());
			System.err.println(this.getClass()+" "+e.getMessage());
			return returnString;
		}
	}

	@Override
	public int add10(int num) {
		return num+10;
	}



}
