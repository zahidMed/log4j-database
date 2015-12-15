package org.log4database.common;

import java.util.Date;

import org.apache.log4j.spi.LoggingEvent;
import org.log4database.EventMapper;

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
