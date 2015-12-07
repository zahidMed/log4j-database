package org.log4database;

import org.apache.log4j.spi.LoggingEvent;

public interface EventMapper {

	Object mapEvent(LoggingEvent loggingEvent);
}
