/*The MIT License (MIT)
 * 
 * Copyright (c) 2015 ZAHID Mohammed <zahid.med@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.log4database.orientdb;


import java.util.Map;
import org.apache.log4j.spi.LoggingEvent;
import org.json.simple.JSONObject;
import org.log4database.EventMapper;



/**
 * Log4j log events converter that converts events into a JSONObject
 * this object can be used in OrientDB query in order to store it.
 * @author ZAHID Mohammed<zahid.med@gmail.com>
 *
 */
public class OrientdbEventMapper extends EventMapper{

	/**
	 * @see EventMapper#mapEvent(Integer, org.apache.log4j.spi.LoggingEvent)
	 */
	@Override
	public Object mapEvent(Integer appId,LoggingEvent loggingEvent) {
		// TODO Auto-generated method stub
		JSONObject obj= new JSONObject();
		
		putIfNotNull(obj,APP_ID, appId);
		putIfNotNull(obj,TIMESTAMP, loggingEvent.timeStamp);
		putIfNotNull(obj,LEVEL, loggingEvent.getLevel().toString());
		putIfNotNull(obj,THREAD, loggingEvent.getThreadName());
		if(loggingEvent.getMessage()!=null)
			putIfNotNull(obj,MESSAGE, loggingEvent.getMessage().toString());
		putIfNotNull(obj,FILE_NAME, loggingEvent.getLocationInformation().getFileName());
		putIfNotNull(obj,METHOD, loggingEvent.getLocationInformation().getMethodName());
		putIfNotNull(obj,LINE_NUMBER, loggingEvent.getLocationInformation().getLineNumber());
		putIfNotNull(obj,CLASS, loggingEvent.getLocationInformation().getClass().toString());
	    
		addProperties(obj, loggingEvent.getProperties());
		
		
	    putIfNotNull(obj,LOGGER_NAME, loggingEvent.getLoggerName());
		
		return obj;
	}
	
	/**
	 * Method that puts the couple <name,value> JSONObject object if value is not null 
	 * @param obj the JSONObject in which the couple <name,value> will be set
	 * @param name the key
	 * @param value the value
	 */
	void putIfNotNull(JSONObject obj,String name, Object value){
		if(value==null) return;
		if(value instanceof String){
			if(((String)value).length()==0)
			return;
		}
		obj.put(name, value);
	}

	/**
	 * Method that add the log4j event properties into a JSONObject
	 * @param obj JSONObject
	 * @param properties @see {@link LoggingEvent#getProperties()}
	 */
	 protected void addProperties(JSONObject obj, final Map<Object, Object> properties) {
		 
	        if (properties != null && properties.size()>0) 
	        {

	        	JSONObject prop = new JSONObject();
	            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
	            	putIfNotNull(prop, entry.getKey().toString(), entry.getValue().toString());
	            }
	            obj.put(PROPERTIES, prop);
	        }
	    }
	 
}
