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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;
import org.log4database.EventMapper;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OTrackedMap;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;

import junit.framework.Assert;

public class TestOrientDBAppender {

	private final static Logger log = Logger.getLogger(TestOrientDBAppender.class);
	private final static String LOG4J_PROPS = "src/test/resources/log4j.properties";
	
	private final static String DB_HOSTNAME = "localhost";
	private final static String DB_PORT = "2424";
	private final static String DB_DATABASE_NAME = "log";
	private final static String DB_TABLE_NAME = "log";
	private final static String DB_USER_NAME = "root";
	private final static String DB_PASSWORD = "root";
	
	OrientDBAppender appender;
	ODatabaseDocumentTx database;
	
	
	public TestOrientDBAppender(){
		PropertyConfigurator.configure(LOG4J_PROPS);
		
		
		appender=(OrientDBAppender) log.getRootLogger().getAppender("OrientDB");
		
		database = new ODatabaseDocumentTx("remote:" + DB_HOSTNAME + ":" + DB_PORT + "/" + DB_DATABASE_NAME);
		database.open(DB_USER_NAME, DB_PASSWORD);
		ODatabaseRecordThreadLocal.INSTANCE.set(database);
	}
	
	
	@Test
	public void testAppendNormalEvent(){
		Category cat=Category.getRoot();
		long date=System.currentTimeMillis();
		Level level=Level.DEBUG;
		String msg="this is a message";
		String thread=Thread.currentThread().getName();
		database.command(new OCommandSQL("delete from "+DB_TABLE_NAME)).execute();
		LoggingEvent event= new LoggingEvent(null, cat,date , level, msg,thread , null, null, null, null);
		appender.append(event);
		appender.finalize();
		List<ODocument> list=database.command(new OCommandSQL("select * from "+DB_TABLE_NAME+" limit 1")).execute();
		ODocument result=list.get(0);
		Assert.assertEquals(new Date(date), result.field(EventMapper.TIMESTAMP));
		Assert.assertEquals(level.toString(),result.field(EventMapper.LEVEL));
		Assert.assertEquals(msg,result.field(EventMapper.MESSAGE));
		Assert.assertEquals(cat.getName(),result.field(EventMapper.LOGGER_NAME));
		Assert.assertEquals(thread,result.field(EventMapper.THREAD));
	}
	
	@Test
	public void testAppendLocalInformation(){
		String msg="this is a message";

		String thread=Thread.currentThread().getName();
		database.command(new OCommandSQL("delete from "+DB_TABLE_NAME)).execute();
		
		log.error(msg);
		appender.finalize();
		List<ODocument> list=database.command(new OCommandSQL("select * from "+DB_TABLE_NAME+" limit 1")).execute();
		ODocument result=list.get(0);
		
		Assert.assertEquals(msg,result.field(EventMapper.MESSAGE));
		Assert.assertEquals("TestOrientDBAppender.java",result.field(EventMapper.FILE_NAME));
		Assert.assertEquals("testAppendLocalInformation",result.field(EventMapper.METHOD));
		//Assert.assertEquals("81",result.field(EventMapper.KEY_LINE_NUMBER));
		Assert.assertEquals("class org.apache.log4j.spi.LocationInfo",result.field(EventMapper.CLASS));
		Assert.assertEquals("org.log4database.orientdb.TestOrientDBAppender",result.field(EventMapper.LOGGER_NAME));
	}
	
	@Test
	public void testAppendMDC(){
		String msg="this is a message";
		 MDC.put("uuid", "1000");
	     MDC.put("recordAssociation", "xyz");

		String thread=Thread.currentThread().getName();
		database.command(new OCommandSQL("delete from "+DB_TABLE_NAME)).execute();
		
		log.error(2);
		appender.finalize();
		List<ODocument> list=database.command(new OCommandSQL("select * from "+DB_TABLE_NAME+" limit 1")).execute();
		ODocument result=list.get(0);
		OTrackedMap map=result.field(EventMapper.PROPERTIES);
		Assert.assertEquals("1000",map.get("uuid"));
		Assert.assertEquals("xyz",map.get("recordAssociation"));
	}
	
	@Test
	public void testAppendThrowable(){

		String thread=Thread.currentThread().getName();
		database.command(new OCommandSQL("delete from "+DB_TABLE_NAME)).execute();
		try{
			InputStream in= new FileInputStream("a.txt");
		}
		catch(Exception e){
			log.error("cannot open file",e);
		}
		
		appender.finalize();
		List<ODocument> list=database.command(new OCommandSQL("select * from "+DB_TABLE_NAME+" limit 1")).execute();
		ODocument result=list.get(0);
		
	}
	
	
}
