/**
 *  SQLConnection
 *  Author: thaitruongminh
 *  Description: 
 *   00: Test DBMS Connection
 */
model MySQL_00
global {
	init {
		create species: toto number: 1;
	}
}
entities {
	species toto skills: [ SQLSKILL ] {
		var listRes type: list init: [ ];
		//var obj type: obj;
		reflex testConnection{
			do action: helloWorld;
	
			do action: connectDB {
				arg dbtype value: "MySQL"; 
				arg url value: "127.0.0.1";
				arg port value: "3306";
				arg database value: "";
				arg user value: "root";
				arg passwd value: "root";
			}
			do action: connectDB {
				arg dbtype value: "MSSQL";
				arg url value: "localhost";
				arg port value: "1433";
				arg database value: "BPH";
				arg user value: "sa";
				arg passwd value: "tmt";
			}
			do action: connectDB {
				arg dbtype value: "ORACLE"; // Not support
				arg url value: "127.0.0.1";
				arg port value: "1433";
				arg database value: "";
				arg user value: "root";
				arg passwd value: "";
			}
		}
	}
}      