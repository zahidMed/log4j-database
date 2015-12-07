package org.log4database;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;



public abstract class DataBaseAppender  extends AppenderSkeleton {

	private EventMapper eventMapper;
	
	@Override
	protected void append(LoggingEvent event) {
		
	}
	
	
	protected abstract void saveQueue();
	
	public void close() {
		// TODO Auto-generated method stub
	}

	public boolean requiresLayout() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public EventMapper getEventMapper() {
		return eventMapper;
	}

	public void setEventMapper(EventMapper eventMapper) {
		this.eventMapper = eventMapper;
	}
}
