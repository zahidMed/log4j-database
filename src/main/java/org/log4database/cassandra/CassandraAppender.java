package org.log4database.cassandra;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.ErrorCode;
import org.log4database.DataBaseAppender;
import org.log4database.common.BasicEventMapper;
import org.log4database.common.Event;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

public class CassandraAppender extends DataBaseAppender {

	private final static String DEFAULT_DB_HOSTNAME = "localhost";
	private final static String DEFAULT_DB_PORT = "9042";
	private final static String DEFAULT_DB_DATABASE_NAME = "log";
	private final static String DEFAULT_DB_TABLE_NAME = "log";

	private Cluster cluster = null;
	private Session session;

	public CassandraAppender() {
		hostname = DEFAULT_DB_HOSTNAME;
		port = DEFAULT_DB_PORT;
		databaseName = DEFAULT_DB_DATABASE_NAME;
		table = DEFAULT_DB_TABLE_NAME;
		eventMapper= new BasicEventMapper();
	}

	/**
	 * convert numerical configuration file properties and open connection pool
	 * to OrientDB database
	 * 
	 * @see {@link AppenderSkeleton}{@link #activateOptions()}
	 */
	@Override
	public void activateOptions() {
		try {
			maxQueueLength = Integer.parseInt(queueLength);
		} catch (NumberFormatException e) {
			errorHandler.error("queueLength must have a numerical value", e, ErrorCode.GENERIC_FAILURE);
		}
		try {
			id = Integer.parseInt(applicationId);
		} catch (NumberFormatException e) {
			errorHandler.error("applicationId must have a numerical value", e, ErrorCode.GENERIC_FAILURE);
		}

		cluster = Cluster.builder().addContactPoint(hostname).build();
		session = cluster.connect();
	}

	@Override
	protected void saveQueue() 
	{
		// TODO Auto-generated method stub
		String query="insert into "+databaseName+"."+table+" (id,"
				+((id!=null)?"appId,":"")
				+"timestamp,"
				+ "level,"
				+ "thread,"
				+ "loggerName,"
				+ "fileName,"
				+ "method,"
				+ "lineNumber,"
				+ "class,"
				+ "message,"
				+ "properties) "+
		"values "+
		" (uuid(),"+((id!=null)?"?,":"")+"?,?,?,?,?,?,?,?,?,?);";
		BatchStatement batch = new BatchStatement();
		PreparedStatement ps= session.prepare(query);
		 
		 for(Object obj: queue){
			 Event event=(Event) obj;
			 if(id==null){
				 batch.add(ps.bind(
						 event.getTimestamp(),
						 event.getLevel(),
						 event.getThread(),
						 event.getLoggerName(),
						 event.getFileName(),
						 event.getMethod(),
						 event.getLineNumber(),
						 event.getClassName(),
						 event.getMessage(),
						 event.getProperties()));
			 }
			 else{
				 batch.add(ps.bind(
						 event.getAppId(),
						 event.getTimestamp(),
						 event.getLevel(),
						 event.getThread(),
						 event.getLoggerName(),
						 event.getFileName(),
						 event.getMethod(),
						 event.getLineNumber(),
						 event.getClassName(),
						 event.getMessage(),
						 event.getProperties()));
			 }
		 }
		 session.execute(batch);
		 
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		if (session != null && !session.isClosed())
			session.close();
		if (cluster != null && !cluster.isClosed())
			cluster.close();
	}

	public Cluster getCluster() {
		return cluster;
	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

}
