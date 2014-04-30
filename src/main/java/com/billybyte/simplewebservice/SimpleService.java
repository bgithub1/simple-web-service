package com.billybyte.simplewebservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style=Style.RPC)
public interface SimpleService {
   @WebMethod 
   byte[] getXml(byte[] xml);
   @WebMethod
   String getRunTimeMxBeanName();
   //normally, use this the line below:
   //ManagementFactory.getRuntimeMXBean().getName() 
   @WebMethod
   String getFullServerAddress();
   @WebMethod
   String[] getStrings(@WebParam(name="stringkeys")String[] keys);
   @WebMethod
   int add10(int num);
}
