/**
 *  SQLConnection
 *  Author: thaitruongminh
 *  Description: 
 *   00: Test DBMS Connection
 */
model MySQL_00
global {
			var SQLSERVER type:map init: ['host'::'localhost','dbtype'::'sqlserver','database'::'','port'::'1433','user'::'sa','passwd'::'tmt'];
			var MySQL type:map init: ['host'::'localhost','dbtype'::'MySQL','database'::'','port'::'3306','user'::'root','passwd'::'root'];
			var SQLITE type:map init: ['dbtype'::'sqlite','database'::'../includes/meteo.db'];
			var ORACLE type:map init: ['host'::'localhost','dbtype'::'Oracle','database'::'','port'::'1433','user'::'sa','passwd'::'tmt'];
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
			do action: write with: [message::"Current Time "+ self timeStamp[]];
			do action: write with: [message::"Connection to SQLSERVER is "+ self testConnection[ params::SQLSERVER]];
			do action: write with: [message::"Connection to MySQL is "+self testConnection[ params::MySQL]];
			do action: write with: [message::"Connection to SQLITE is "+self testConnection[ params::SQLITE]];
			do action: write with: [message::"Connection to ORACLE is "+self testConnection[ params::ORACLE]];
		}
	}
}      