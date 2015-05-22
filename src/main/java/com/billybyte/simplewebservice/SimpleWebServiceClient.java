package com.billybyte.simplewebservice;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

//import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
//import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.thoughtworks.xstream.XStream;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class SimpleWebServiceClient<K,T> {
	private final XStream xstream;
	private Service service;
	private SimpleService simple;
	private final String publicIpAddressOfWebService;
	private final String portNumberOfWebService;
	private boolean wired=false;
	private boolean usingDirectServiceCalls=false;
//	private boolean clientAndServerInSameProcess=false;
	private SimpleService inProcessService=null;
	
	public void setUsingDirectServerCalls(boolean usingDirectServerCalls){
		this.usingDirectServiceCalls = usingDirectServerCalls;
	}
	
	public boolean isWired(){
		return wired;
	}
	
	@SuppressWarnings("unused")
	private SimpleWebServiceClient(){
		this.xstream = null;
		this.publicIpAddressOfWebService = null;
		this.portNumberOfWebService = null;
	}
	public SimpleWebServiceClient(XStream xstream,
			String publicIpAddressOfWebService,
			String portNumberOfWebService){
		this.xstream = xstream;
		this.publicIpAddressOfWebService = publicIpAddressOfWebService;
		this.portNumberOfWebService = portNumberOfWebService;
		
	}
	private Object wire_Lock = new Object();
	private void wire(){

		synchronized (wire_Lock) {
			if (isWired())
				return;
			this.wired = true;
			URL url = null;
			try {
				url = new URL(publicIpAddressOfWebService + ":"
						+ portNumberOfWebService + "/simple?wsdl");
			} catch (MalformedURLException e) {
				throw new IllegalStateException(e);
			}
			// Specify the qualified name
			QName name = new QName("http://simplewebservice.billybyte.com/",
					"SimpleServiceImplService");
			// Create the service
			service = Service.create(url, name);
			simple = service.getPort(SimpleService.class);
			// See if the create of this client wants to allow for direct calls to the service,
			//   which will bypass the soap/ip based called to an out-of-process server
			// This is useful when the server service wants to call itself, via a client.
			if (usingDirectServiceCalls) {
				// See if the caller is in the same jvm as the  server.
				String fullAddressOfServer = simple.getFullServerAddress();
				this.inProcessService = ServerList
						.getInProcessService(fullAddressOfServer);
			}
		}
		
	}
	
	private final Object dataFromService_Lock = new Object();
	@SuppressWarnings("unchecked")
	public T getDataFromService(K key){
		wire(); // this will actually only be done once

		T t;
		synchronized (dataFromService_Lock) {
			if (key == null) {
				throw new IllegalArgumentException(this.getClass().getName()
						+ " getData key is null");
			}
//			ByteOutputStream bs = new ByteOutputStream();
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			xstream.toXML(key, bs);
//			byte[] argToSend = bs.getBytes();
			byte[] argToSend = bs.toByteArray();
			if (argToSend == null) {
				throw new IllegalArgumentException(this.getClass().getName()
						+ " getData key can't be made into a byte[] arrray: "
						+ key.toString());
			}
			if (argToSend.length < 1) {
				throw new IllegalArgumentException(this.getClass().getName()
						+ " getData key becomes a zero lenght byte[] arrray: "
						+ key.toString());
			}
			byte[] returnedData = null;//= simple.getXml(argToSend);
			if (this.inProcessService != null) {
				returnedData = inProcessService.getXml(argToSend);
			} else {
				if (simple == null) {
					System.err
							.println(this.getClass().getName()
									+ " : simle service not created yet.  Can't do service call");
					return null;
				}
				if (argToSend == null || argToSend.length < 1) {
					System.err
							.println(this.getClass().getName()
									+ " : No data to send to service.  Can't do service call");
				}
				try {
					returnedData = simple.getXml(argToSend);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (SimpleServiceImpl.isNullData(returnedData))
				return null;
//			ByteInputStream bIn = new ByteInputStream(returnedData,
//					returnedData.length);
			ByteArrayInputStream bIn = new ByteArrayInputStream(returnedData, 0, returnedData.length);
			Object o = xstream.fromXML(bIn);
			t = (T) o;
		}

		return t;
	}
	
	public String[] getStrings(String[] keys){
		wire();
		
		return simple.getStrings(keys);
	}
	
	public int add10(int num){
		wire();
		return simple.add10(num);
	}
	
	/**
	 * send and receive xml
	 * @param xml
	 * @return
	 */
	public String getStringFromService(String xml){
		wire(); // this will actually only be done once

		synchronized (dataFromService_Lock) {
			if (xml == null) {
				throw new IllegalArgumentException(this.getClass().getName()
						+ " getData key is null");
			}
//			ByteOutputStream bs = new ByteOutputStream();
//			bs.writeAsAscii(xml);
//			byte[] argToSend = bs.getBytes();
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			byte buf[] = xml.getBytes(); 
			try {
				bs.write(buf);
			} catch (IOException e1) {
				throw new IllegalStateException(e1);
			}
			byte[] argToSend = bs.toByteArray();
			if (argToSend == null) {
				throw new IllegalArgumentException(this.getClass().getName()
						+ " getData key can't be made into a byte[] arrray: "
						+ xml);
			}
			if (argToSend.length < 1) {
				throw new IllegalArgumentException(this.getClass().getName()
						+ " getData key becomes a zero lenght byte[] arrray: "
						+ xml);
			}
			byte[] returnedData = null;//= simple.getXml(argToSend);
			if (this.inProcessService != null) {
				returnedData = inProcessService.getXml(argToSend);
			} else {
				if (simple == null) {
					System.err
							.println(this.getClass().getName()
									+ " : simle service not created yet.  Can't do service call");
					return null;
				}
				if (argToSend == null || argToSend.length < 1) {
					System.err
							.println(this.getClass().getName()
									+ " : No data to send to service.  Can't do service call");
				}
				try {
					returnedData = simple.getXml(argToSend);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (SimpleServiceImpl.isNullData(returnedData))
				return null;
			return new String(returnedData);
		}

		
	}
	
}
