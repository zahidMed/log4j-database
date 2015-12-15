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

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.ErrorCode;
import org.json.simple.JSONObject;
import org.log4database.DataBaseAppender;

import com.orientechnologies.orient.core.command.script.OCommandScript;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

/**
 * Log4j appender that stores log events in a queue then in OrientDB database
 * @see {@link OrientDBAppender}
 * @author ZAHID Mohammed<zahid.med@gmail.com>
 *
 */
public class OrientDBAppender extends DataBaseAppender {

	private final static String DEFAULT_DB_HOSTNAME = "localhost";
	private final static String DEFAULT_DB_PORT = "2480";
	private final static String DEFAULT_DB_DATABASE_NAME = "log";
	private final static String DEFAULT_DB_TABLE_NAME = "log";

	ODatabaseDocumentTx database;

	public OrientDBAppender() {
		hostname = DEFAULT_DB_HOSTNAME;
		port = DEFAULT_DB_PORT;
		databaseName = DEFAULT_DB_DATABASE_NAME;
		table = DEFAULT_DB_TABLE_NAME;
		eventMapper= new OrientdbEventMapper();
	}

	/**
	 * convert numerical configuration file properties and open connection
	 * pool to OrientDB database
	 * @see {@link AppenderSkeleton}{@link #activateOptions()}
	 */
	@Override
	public void activateOptions() {
		try{
			maxQueueLength=Integer.parseInt(queueLength);
		}
		catch(NumberFormatException e){
			errorHandler.error("queueLength must have a numerical value", e,
					ErrorCode.GENERIC_FAILURE);
		}
		try{
			id=Integer.parseInt(applicationId);
		}
		catch(NumberFormatException e){
			errorHandler.error("applicationId must have a numerical value", e,
					ErrorCode.GENERIC_FAILURE);
		}
		try {
			if (database != null)
				database.close();
			database = new ODatabaseDocumentTx("remote:" + hostname + ":" + port + "/" + databaseName);
			database.open(userName, password);
			ODatabaseRecordThreadLocal.INSTANCE.set(database);
		} catch (Exception e) {
			errorHandler.error("Unexpected exception while initialising OrientdbDbAppender.", e,
					ErrorCode.GENERIC_FAILURE);
		}

	}

	/**
	 * Method that creates query for OrientDB from the JSONObject event in the queue
	 * and executes it in the database
	 * @see {@link DataBaseAppender}{@link #saveQueue()} 
	 */
	@Override
	protected void saveQueue() {
		// TODO Auto-generated method stub
		if (queue.size() == 0)
			return;
		StringBuffer cmd = new StringBuffer("begin\n");
		for (Object obj : queue) {
			cmd.append("insert into " + table + " content " + ((JSONObject) obj).toString() + "\n");
		}
		cmd.append("commit\n");
		//System.out.println(cmd);
		try
		{
			database.command(new OCommandScript("sql", cmd.toString())).execute();
		}
		catch(Exception e){
			errorHandler.error("Unexpected exception while saving events", e,
					ErrorCode.GENERIC_FAILURE);
		}
	}
	
	/**
	 * Close the opened connection to OrientDB database
	 * @see {@link OrientDBAppender}{@link #close()}
	 */
	@Override
	public void close() {
		if (database != null && !database.isClosed())
			database.close();
	}

}
