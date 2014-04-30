package com.billybyte.simplewebservice;

import com.thoughtworks.xstream.XStream;

public interface SimpleWebServiceProcessRequestInterface<K,T> {
	/**
	 * 
	 * @param requestKey - key of Type K to pass to the user to process
	 * @param xstream - xstream instance for deserialization of the key,
	 * 			and serialization of the return object T
	 * @return
	 */
	public T processRequest(K requestKey, XStream xstream);

}
