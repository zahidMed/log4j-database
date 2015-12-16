#log4j-database
##What is it?

This is an open source library that extends log4j appender for storing log events in multiple databases and data stores such as SQL based ones (Mysql, Postresql ...), noSQL based databases (OrientDB, Cassandra, HBase, MongoDB...) and Cloud datastore (Google, Amazon ....).

This library allows the use of the same table/collection/class for storing events from many applications by adding the application identifier field. It uses also an event queue in order to execute a bulk insert and increase the event log performance.

The current version 1.1.0 handle OrientDB and Cassandra, the other ones are on the way :)

##OrientDBAppender
This appender was developed to support OrientDB version 2.0.10 or higher.

###OrientDBAppender dependencies

When using OrientDBAppender, add the following dependencies:

		<dependency>
			<groupId>log4j</groupId>
			<artifactId>log4j</artifactId>
			<version>1.2.17</version>
		</dependency>
		<dependency>
			<groupId>com.orientechnologies</groupId>
			<artifactId>orientdb-client</artifactId>
			<version>2.0.10</version>
		</dependency>
		<dependency>
			<groupId>com.orientechnologies</groupId>
			<artifactId>orientdb-core</artifactId>
			<version>2.0.10</version>
		</dependency>
		<dependency>
			<groupId>com.orientechnologies</groupId>
			<artifactId>orientdb-enterprise</artifactId>
			<version>2.0.10</version>
		</dependency>
		<dependency>
			<groupId>com.googlecode.json-simple</groupId>
			<artifactId>json-simple</artifactId>
			<version>1.1.1</version>
		</dependency>

###OrientDBAppender configuration

the appender **org.log4database.orientdb.OrientDBAppender** can be configured using the following parameters in log4j configuration file:

Property | Description
------------ | -------------
hostname | the database host name
port | database port. Default value 2424
databaseName | the database name, it should be a document database. Default value "log"
table | the class name used in to store events. Default value "log"
userName | the database user
password | the database password
applicationId | the application identifier when using the same class for storing events from many applications
queueLength | the queue capacity for keeping events before performing a bulk insert. Default value 10

###Log database configuration

Before using OrientDB based logging, a class should be created in a Document database to maintain all the log information. Following is the script for creating the log class.

    create class log
    CREATE PROPERTY log.appId integer // when using the property applicationId
    CREATE PROPERTY log.timestamp Datetime
    CREATE PROPERTY log.loggerName String
    CREATE PROPERTY log.level String
    CREATE PROPERTY log.thread String
    CREATE PROPERTY log.message String
    CREATE PROPERTY log.fileName String
    CREATE PROPERTY log.method String
    CREATE PROPERTY log.lineNumber integer
    CREATE PROPERTY log.class String
    CREATE PROPERTY log.properties Embeddedmap

###Sample configuration file

Following is a sample configuration file log4j.properties for OrientDBAppender which will be used to log messages to a log class.
    
    # Set the root logger to
    log4j.rootLogger=all, OrientDB
    
    log4j.appender.OrientDB=org.log4database.orientdb.OrientDBAppender
    log4j.appender.OrientDB.hostname=localhost
    log4j.appender.OrientDB.port=2424
    log4j.appender.OrientDB.table=log
    log4j.appender.OrientDB.databaseName=log
    log4j.appender.OrientDB.userName=user
    log4j.appender.OrientDB.password=user
    log4j.appender.OrientDB.applicationId=1
    log4j.appender.OrientDB.queueLength=30


###Sample code

The following Java class is a very simple example that initializes and then uses the Log4J-database library for Java applications.


	import org.apache.log4j.Logger;
	
	public class log4jExample{
	
		public static Logger logger=Logger.getLogger("OrientDB");
	
			public static void main(String[] arg){
			logger.debug("This is a debug message ");
			logger.info("This is an info message");
		}
	}
	
	
##CassandraAppender
This appender was developed to support Cassandra version 2.1.0 or higher.

###CassandraAppender dependencies

When using CassandraAppender, add the following dependencies:

		<dependency>
			<groupId>log4j</groupId>
			<artifactId>log4j</artifactId>
			<version>1.2.17</version>
		</dependency>
		<dependency>
			<groupId>com.datastax.cassandra</groupId>
			<artifactId>cassandra-driver-core</artifactId>
			<version>2.1.0</version>
		</dependency>
		
###CassandraAppender configuration

the appender **org.log4database.cassandra.CassandraAppender** can be configured using the following parameters in log4j configuration file:

Property | Description
------------ | -------------
hostname | the database host name
port | database port. Default value 9042
databaseName | the keyspace name. Default value "log"
table | the table name used in to store events. Default value "log"
userName | the database user
password | the database password
applicationId | the application identifier when using the same class for storing events from many applications
queueLength | the queue capacity for keeping events before performing a bulk insert. Default value 10

###Log database configuration

Before using OrientDB based logging, a class should be created in a database to maintain all the log information. Following is the script for creating the log table.

    CREATE TABLE log.log (
    id uuid ,
    appId int,
    timestamp timestamp,
    level text,
    thread text,
    loggerName text,
    fileName text,
    method text,
    lineNumber int,
    class text,
    message text,
    properties map<text,text>,
    PRIMARY KEY (uuid, appId,timestamp,level, ...)//add all the fields you will use in where statement when parsing logs
    );

###Sample configuration file

Following is a sample configuration file log4j.properties for CassandraAppender which will be used to log messages to a log class.
    
    # Set the root logger to
    log4j.rootLogger=all, Cassandra

    # Cassandra appender classname
    log4j.appender.Cassandra=org.log4database.cassandra.CassandraAppender
    log4j.appender.Cassandra.hostname=localhost
    log4j.appender.Cassandra.port=9042
    log4j.appender.Cassandra.table=log
    log4j.appender.Cassandra.databaseName=log
    log4j.appender.Cassandra.userName=cassandra
    log4j.appender.Cassandra.password=cassandra
    log4j.appender.Cassandra.applicationId=1
    log4j.appender.Cassandra.queueLength=10


###Sample code

The following Java class is a very simple example that initializes and then uses the Log4J-database library for Java applications.


	import org.apache.log4j.Logger;
	
	public class log4jExample{
	
		public static Logger logger=Logger.getLogger("Cassandra");
	
			public static void main(String[] arg){
			logger.debug("This is a debug message ");
			logger.info("This is an info message");
		}
	}