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

package org.log4database;

import org.apache.log4j.spi.LoggingEvent;

/**
 * Class that converts log4j event to the appropriate object or SQL query that can be
 * stored in a database, cloud storage system or data store
 * @author ZAHID Mohammed<zahid.med@gmail.com>
 *
 */
public class EventMapper {

	public static final String APP_ID = "appId";
    public static final String TIMESTAMP = "timestamp";
    public static final String LEVEL = "level";
    public static final String THREAD = "thread";
    public static final String MESSAGE = "message";
    public static final String LOGGER_NAME = "loggerName";

    public static final String FILE_NAME = "fileName";
    public static final String METHOD = "method";
    public static final String LINE_NUMBER = "lineNumber";
    public static final String CLASS = "class";

    public static final String FQCN = "fullyQualifiedClassName";
    public static final String PACKAGE = "package";
    public static final String CLASS_NAME = "className";


    public static final String PROPERTIES = "properties";
	
    /**
     * Method that converts (maps)log4j event
     * @param appId the application identifier, this parameter is used when having
     * many applications using the same table for storing logs
     * @param loggingEvent log4j event
     * @return return an object that corresponds to the used database record. 
     */
    public Object mapEvent(Integer appId,LoggingEvent loggingEvent){
    	return null;
    }
}
