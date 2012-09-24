/**
 *  SQLConnection
 *  Author: thaitruongminh
 *  Description: 
 *   00: Test Microsoft SQL Server Connection
 */
model MSSQL_00
global {
		var PARAMS type:map init: ['host'::'localhost','dbtype'::'sqlserver','database'::'','port'::'1433','user'::'sa','passwd'::'tmt'];
	
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
			if (self testConnection[ params::PARAMS]){
				do action: write with: [message::"Connection is OK"] ;
			}else{
				do action: write with: [message::"Connection is false"] ;
			}		
		}
	}
}      