/**
 *  SQLConnection
 *  Author: thaitruongminh
 *  Description: 
 *   00: Test Microsoft SQL Server Connection
 */
model MSSQL_00
global {
	init {
		create species: toto number: 1;
	}
}
entities {
	species toto skills: [SQLSKILL ] { 
		var listRes type: list init: [ ];
		//var obj type: obj;
		reflex conn {
			do action: helloWorld;
			do action: connectDB {
				arg dbtype value: "SQLSERVER";//SQLSERVER = SQLServer; MySQL = MySQL 
				arg host value: "localhost";// IP address or computer name
				arg port value: "1433"; // MSSQL = 1433; MySQL = 3306 
				arg database value: "";
				arg user value: "sa";
				arg passwd value: "tmt";
			}
		}
	}
}      