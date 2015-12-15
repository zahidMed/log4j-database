package org.log4database.cassandra;



import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;
import org.log4database.EventMapper;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.orientechnologies.orient.core.db.record.OTrackedMap;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;

import junit.framework.Assert;


public class TestCassandraAppender {

	private final static Logger log = Logger.getLogger("TestCassandra");
	private final static String LOG4J_PROPS = "src/test/resources/cassandra_log4j.properties";
	
	CassandraAppender appender=null;
	
	public TestCassandraAppender(){
		PropertyConfigurator.configure(LOG4J_PROPS);
//		appender= new CassandraAppender();
//		appender.setUserName("cassandra");
//		appender.setPassword("cassandra");
//		appender.activateOptions();
		appender=(CassandraAppender) log.getRootLogger().getAppender("Cassandra");
		appender.getSession().execute("DROP KEYSPACE IF EXISTS log;");
		appender.getSession().execute("DROP TABLE IF EXISTS log.log;");
		appender.getSession().execute("CREATE KEYSPACE log WITH replication " + 
			      "= {'class':'SimpleStrategy', 'replication_factor':3};");
		
		appender.getSession().execute(
			      "CREATE TABLE log.log (" +
			    		"id uuid primary key," + 
			            "appId int," + 
			            "timestamp timestamp," + 
			            "level text," + 
			            "thread text," + 
			            "loggerName text," + 
			            "fileName text," + 
			            "method text," + 
			            "lineNumber int," + 
			            "class text," + 
			            "message text," + 
			            "properties map<text,text>);");
		
		
	}
	
	
	@Test
	public void testAppendNormalEvent(){
		Category cat=Category.getRoot();
		long date=System.currentTimeMillis();
		Level level=Level.DEBUG;
		String msg="this is a message";
		String thread=Thread.currentThread().getName();
		appender.getSession().execute("TRUNCATE log.log;");
		LoggingEvent event= new LoggingEvent(null, cat,date , level, msg,thread , null, null, null, null);
		appender.append(event);
		//appender.finalize();
		ResultSet results= appender.getSession().execute("SELECT * from log.log");
		
		List<Row> list=results.all();
		//Assert.assertEquals(true,true);
		if(list!=null && list.size()>0)
		{
			Row row=list.get(0);
			Assert.assertEquals(new Date(date), row.getDate(EventMapper.TIMESTAMP));
			Assert.assertEquals(level.toString(),row.getString(EventMapper.LEVEL));
			Assert.assertEquals(msg,row.getString(EventMapper.MESSAGE));
			Assert.assertEquals(cat.getName(),row.getString(EventMapper.LOGGER_NAME));
			Assert.assertEquals(thread,row.getString(EventMapper.THREAD));
		}
		else
			Assert.fail();
		/*
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
		*/
		
	}
	
	
	@Test
	public void testAppendLocalInformation(){
		String msg="this is a message";

		
		appender.getSession().execute("TRUNCATE log.log;");
		
		log.error(msg);
		ResultSet results= appender.getSession().execute("SELECT * from log.log");
		List<Row> list=results.all();
		if(list!=null && list.size()>0)
		{
			Row row=list.get(0);
			Assert.assertEquals(msg,row.getString(EventMapper.MESSAGE));
			Assert.assertEquals("TestCassandraAppender.java",row.getString(EventMapper.FILE_NAME));
			Assert.assertEquals("testAppendLocalInformation",row.getString(EventMapper.METHOD));
			//Assert.assertEquals(110,row.getInt(EventMapper.LINE_NUMBER));
			Assert.assertEquals("org.log4database.cassandra.TestCassandraAppender",row.getString(EventMapper.CLASS));
			Assert.assertEquals("TestCassandra",row.getString(EventMapper.LOGGER_NAME));
		}
		else
			Assert.fail();
	}
	
	
	@Test
	public void testAppendMDC(){
		String msg="this is a message";
		 MDC.put("uuid", "1000");
	     MDC.put("recordAssociation", "xyz");

		String thread=Thread.currentThread().getName();
		appender.getSession().execute("TRUNCATE log.log;");
		
		log.error(2);
		
		ResultSet results= appender.getSession().execute("SELECT * from log.log");
		List<Row> list=results.all();
		if(list!=null && list.size()>0)
		{
			Row row=list.get(0);
			Map<String,String> map=row.getMap(EventMapper.PROPERTIES, String.class, String.class);
			Assert.assertEquals("1000",map.get("uuid"));
			Assert.assertEquals("xyz",map.get("recordAssociation"));
		}
		else
			Assert.fail();
		
	}
	
}
