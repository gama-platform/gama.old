/**
* Name: SQLConnection
* Author: thaitruongminh
* Description: How to create a connection to a database in GAMA
* Tags: database
 */
model test_connection

global {
	map<string, string> SQLSERVER <- ['host'::'localhost', 'dbtype'::'sqlserver', 'database'::'', 'port'::'1433', 'user'::'sa', 'passwd'::'tmt'];
	map<string, string> MySQL <- ['host'::'localhost', 'dbtype'::'MySQL', 'database'::'', 'port'::'8889', 'user'::'root', 'passwd'::'root'];
	map<string, string> ORACLE <- ['host'::'localhost', 'dbtype'::'Oracle', 'database'::'', 'port'::'1433', 'user'::'sa', 'passwd'::'tmt'];
	map<string, string> POSTGRES <- ['host'::'localhost', 'dbtype'::'Postgres', 'database'::'postgres', 'port'::'5432', 'user'::'postgres', 'passwd'::''];
	map<string, string> SQLITE <- ['dbtype'::'sqlite', 'database'::'../../includes/meteo.db'];
	init {
		write "This model will work only if the corresponding database is installed and the database management server launched." color: #red;

		write "TESTS CONNECTIONS WITH SQLSKILL";
		create DB_connection_tester;
		
		write "";
		write "TESTS CONNECTIONS WITH AgentDB";			
		create AgentDB_SQLSERVER;
		create AgentDB_MySQL;	
		create AgentDB_SQLITE;
		create AgentDB_ORACLE;	
		create AgentDB_POSTGRESQL;		
	}

}

species DB_connection_tester skills: [SQLSKILL] {
	init {
		write "Current Time " + timeStamp();
		write "Connection to SQLSERVER is " +  testConnection(SQLSERVER);
		write "Connection to MySQL is " +  testConnection(MySQL);
		write "Connection to SQLITE is " +  testConnection(SQLITE);
		write "Connection to ORACLE is " +  testConnection(ORACLE);
		write "Connection to POSTGRESQL is " +  testConnection(POSTGRES);
	}
}

species AgentDB_SQLSERVER parent: AgentDB {
	init {
		write "Connection to SQLSERVER with AgenDB is " +  testConnection(SQLSERVER);
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

species AgentDB_ORACLE parent: AgentDB {
	init {
		write "Connection to ORACLE with AgenDB is " +  testConnection(ORACLE);
	}
}

species AgentDB_POSTGRESQL parent: AgentDB {
	init {
		write "Connection to POSTGRESQL with AgenDB is " +  testConnection(POSTGRES);
	}
}

experiment default_expr type: gui {}  