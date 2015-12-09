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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.log4database.orientdb.OrientdbEventMapper;



/**
 * Abstract log4j appender that put mapped log4j events in a queue and store them.
 *  The abstract method saveQueue should be implemented according to the used
 *  database or data store.
 * @author ZAHID Mohammed<zahid.med@gmail.com>
 *
 */
public abstract class DataBaseAppender  extends AppenderSkeleton {

	private EventMapper eventMapper= new OrientdbEventMapper();
	protected List queue= new ArrayList();

	//configuration properties
    protected String hostname;
    protected String port;
    protected String databaseName;
    protected String table;
    protected String userName=null;
    protected String password=null;
    protected String queueLength = null;
    protected String applicationId=null;
	
	//
    /**
     * numerical value of queueLength
     * @see {@link DataBaseAppender}{@link #queueLength}
     */
	protected int maxQueueLength=10;
	
	/**
	 * numerical value of applicationId
	 * @see {@link DataBaseAppender}{@link #applicationId}
	 */
	protected Integer id=null;
	
	/**
	 * @see org.apache.log4j.AppenderSkeleton#doAppend(org.apache.log4j.spi.LoggingEvent)
	 */
	@Override
	public void append(LoggingEvent event) {
		
		Object obj=eventMapper.mapEvent(id,event);
		if(obj!=null)
		{
			queue.add(obj);
			if(queue.size()>=maxQueueLength)
			{
				saveQueue();
				queue= new ArrayList();
			}
		}
	}
	
	/**
	 * Abstract method that save objects in the queue into the used database or data store
	 */
	protected abstract void saveQueue();
	
	/**
	 * @see org.apache.log4j.AppenderSkeleton#finalize()
	 */
	@Override
	public void finalize(){
		saveQueue();
		queue= new ArrayList();
		close();
	}
	
	/**
	 * Abstract method that closes the database or data store connection 
	 */
	abstract public void close();

	public boolean requiresLayout() {
		return false;
	}
	
	public EventMapper getEventMapper() {
		return eventMapper;
	}

	public void setEventMapper(EventMapper eventMapper) {
		this.eventMapper = eventMapper;
	}


	public String getHostname() {
		return hostname;
	}


	public void setHostname(String hostname) {
		this.hostname = hostname;
	}


	public String getPort() {
		return port;
	}


	public void setPort(String port) {
		this.port = port;
	}


	public String getDatabaseName() {
		return databaseName;
	}


	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}


	public String getTable() {
		return table;
	}


	public void setTable(String table) {
		this.table = table;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getQueueLength() {
		return queueLength;
	}


	public void setQueueLength(String queueLength) {
		this.queueLength = queueLength;
	}


	public int getMaxQueueLength() {
		return maxQueueLength;
	}


	public void setMaxQueueLength(int maxQueueLength) {
		this.maxQueueLength = maxQueueLength;
	}


	public String getApplicationId() {
		return applicationId;
	}


	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}



}
