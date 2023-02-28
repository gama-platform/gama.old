/**
* Name: SQLConnection
* Author: thaitruongminh
* Description: How to create a connection to a database in GAMA
* Tags: database
 */
model test_connection

global {
	map<string, string> MySQL <- ['host'::'localhost', 'dbtype'::'mysql', 'database'::'', 'port'::'8889', 'user'::'root', 'passwd'::'root'];
	map<string, string> SQLITE <- ['dbtype'::'sqlite', 'database'::'../includes/meteo.db'];

	// Note that the postgis extension needs to be installed in the postgres database.
	// Enable the postGIS extension in the database with: CREATE EXTENSION postgis;
	// https://postgis.net/install/
	map<string, string> POSTGRES <- ['host'::'localhost', 'dbtype'::'postgres', 'database'::'postgres', 'port'::'5432', 'user'::'postgres', 'passwd'::''];
	
	init {
		
		write "This model will work only if the corresponding database is installed and the database management server launched." color: #red;

		write "TESTS CONNECTIONS WITH SQLSKILL";
		create DB_connection_tester;
		
		write "";
		write "TESTS CONNECTIONS WITH AgentDB";			
		create AgentDB_MySQL;	
		create AgentDB_SQLITE;
		create AgentDB_POSTGRESQL;		
		
		ask AgentDB_MySQL {do die;}
		ask AgentDB_SQLITE {do die;}
		ask AgentDB_POSTGRESQL {do die;}
	}

}

species DB_connection_tester skills: [SQLSKILL] {
	init {
		write "Connection to MySQL is " +  testConnection(MySQL);
		write "Connection to SQLITE is " +  testConnection(SQLITE);
		write "Connection to POSTGRESQL is " +  testConnection(POSTGRES);
	}
}

species AgentDB_MySQL parent: AgentDB {
	init {
		write "Connection to MySQL with AgenDB is " +  testConnection(MySQL);
	}
}

species AgentDB_SQLITE parent: AgentDB {
	init {
		write "Connection to SQLITE with AgenDB is " +  testConnection(SQLITE);
	}
}

species AgentDB_POSTGRESQL parent: AgentDB {
	init {
		write "Connection to POSTGRESQL with AgenDB is " +  testConnection(POSTGRES);
	}
}

experiment default_expr type: gui { }  