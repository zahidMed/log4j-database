package org.log4database.common;

import java.util.Date;

import org.apache.log4j.spi.LoggingEvent;
import org.log4database.EventMapper;

/**
 * Log4j log events converter that converts events into a simple event
 * this object can be used by many appenders in order to store it.
 * @see {@link Event}
 * @author ZAHID Mohammed<zahid.med@gmail.com>
 */
public class BasicEventMapper extends EventMapper{

	
	public Object mapEvent(Integer appId,LoggingEvent loggingEvent){
		Event event= new Event();
		event.setAppId(appId);
		event.setTimestamp(new Date(loggingEvent.getTimeStamp()));
		event.setLevel(loggingEvent.getLevel().toString());
		event.setThread(loggingEvent.getThreadName());
		event.setLoggerName(loggingEvent.getLoggerName());
		event.setFileName(loggingEvent.getLocationInformation().getFileName());
		event.setMethod(loggingEvent.getLocationInformation().getMethodName());
		if(loggingEvent.getLocationInformation().getLineNumber()!=null && loggingEvent.getLocationInformation().getLineNumber().matches("\\d+"))
			event.setLineNumber(Integer.parseInt(loggingEvent.getLocationInformation().getLineNumber()));
		event.setClassName(loggingEvent.getLocationInformation().getClassName());
		//event.setClassName(loggingEvent.getLocationInformation().getClass().toString());
		event.setMessage(String.valueOf(loggingEvent.getMessage()));
		event.setProperties(loggingEvent.getProperties());
    	return event;
    }
	
}
