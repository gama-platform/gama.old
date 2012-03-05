/**
 *  SQLConnection
 *  Author: thaitruongminh
 *  Description: 
 *   00: Test DBMS Connection
 */
model SQLConnection_00
global {
	init {
		create species: toto number: 1;
	}
}
entities {
	species toto skills: [ MAELIADBMS ] {
		var listRes type: list init: [ ];
		//var obj type: obj;
		reflex {
			do action: helloWorld;
			do action: connectTest {
				arg url value: "127.0.0.1";
				arg port value: "3306";
				arg dbName value: ""; 
				arg usrName value: "root";
				arg password value: "";
				arg driver value: "com.mysql.jdbc.Driver";
			}
			do action: connectDB {
				arg vendorName value: "MySQL";
				arg url value: "127.0.0.1";
				arg port value: "3306";
				arg dbName value: "";
				arg usrName value: "root";
				arg password value: "";
			}
			do action: connectDB {
				arg vendorName value: "MSSQL";
				arg url value: "193.49.54.112";
				arg port value: "1433";
				arg dbName value: "BPH";
				arg usrName value: "sa";
				arg password value: "tmt";
			}
			do action: connectDB {
				arg vendorName value: "ORACLE";
				arg url value: "127.0.0.1";
				arg port value: "1433";
				arg dbName value: "";
				arg usrName value: "root";
				arg password value: "";
			}
		}
	}
}      