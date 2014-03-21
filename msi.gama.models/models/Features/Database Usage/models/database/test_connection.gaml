/**
 *  SQLConnection
 *  Author: thaitruongminh
 *  Description: 
 *   00: Test DBMS Connection
 */
model test_connection

global {
	map<string, string> SQLSERVER <- ['host'::'localhost', 'dbtype'::'sqlserver', 'database'::'', 'port'::'1433', 'user'::'sa', 'passwd'::'tmt'];
	map<string, string> MySQL <- ['host'::'localhost', 'dbtype'::'MySQL', 'database'::'', 'port'::'8889', 'user'::'root', 'passwd'::'root'];
	map<string, string> ORACLE <- ['host'::'localhost', 'dbtype'::'Oracle', 'database'::'', 'port'::'1433', 'user'::'sa', 'passwd'::'tmt'];
	map<string, string> POSTGRES <- ['host'::'localhost', 'dbtype'::'Postgres', 'database'::'bgaudou', 'port'::'5432', 'user'::'bgaudou', 'passwd'::''];
	map<string, string> SQLITE <- ['dbtype'::'sqlite', 'database'::'../../includes/meteo.db'];
	init {
		create DB_connection_tester;
	}

}

species DB_connection_tester skills: [SQLSKILL] {
	init {
		write "Current Time " + self timeStamp [];
		write "Connection to SQLSERVER is " + self testConnection [params::SQLSERVER];
		write "Connection to MySQL is " + self testConnection [params::MySQL];
		write "Connection to SQLITE is " + self testConnection [params::SQLITE];
		write "Connection to ORACLE is " + self testConnection [params::ORACLE];
		write "Connection to POSTGRESQL is " + self testConnection [params::POSTGRES];
	}

}

experiment default_expr type: gui {
}  