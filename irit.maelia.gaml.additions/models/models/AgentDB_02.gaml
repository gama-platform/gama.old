/**
 *  
 *  Author: Truong Minh Thai
 *  Description: 
 * 		Model testing the irit.maelia.gaml.additions package
 * 		
 */

model AgentDB_02 
  
global {
		var SQLSERVER type:map init: ['host'::'localhost','dbtype'::'sqlserver','database'::'','port'::'1433','user'::'sa','passwd'::'tmt'];
		var MySQL type:map init: ['host'::'localhost','dbtype'::'MySQL','database'::'','port'::'3306','user'::'root','passwd'::'root'];
		var SQLITE type:map init: ['dbtype'::'sqlite','database'::'../includes/meteo.db'];
		var ORACLE type:map init: ['host'::'localhost','dbtype'::'Oracle','database'::'','port'::'1433','user'::'sa','passwd'::'tmt'];

	init {		
		create species: inheritantAgent number: 1{
			do action: write with: [message::"isConected: " + self isConnected[]];
			
			do action: connect with: [params::SQLSERVER];		
			do action: write with: [message::"isConected: " + self isConnected[]];
			do action: write with: [message::"Connection parameters: " + self getParameter[]];	
			
			//remove command below for correct running.
			do action: connect with: [params::SQLITE];	//error on this command. must close current connection before open new	
			
			do action: close;	
			do action: write with: [message::"isConected: " + self isConnected[]];
			
			do action: connect with: [params::SQLITE];						
			let t value: self select[select::"SELECT id_point, temp_min FROM points WHERE month='1' AND day='14';"];
			set listRes value: t;			
			do action: write with: [message:: listRes];
			
			
		}
	} 
} 

entities { 
	
	species inheritantAgent parent: AgentDB {
		var listRes type: list init:[];		
		reflex testConnection{
			do action: write with: [message::"Current Time "+ self timeStamp[]];
			do action: write with: [message::"Connection to SQLSERVER is "+ self testConnection[ params::SQLSERVER]];
			do action: write with: [message::"Connection to MySQL is "+self testConnection[ params::MySQL]];
			do action: write with: [message::"Connection to SQLITE is "+self testConnection[ params::SQLITE]];
			do action: write with: [message::"Connection to ORACLE is "+self testConnection[ params::ORACLE]];
		}
		
		reflex select{
			if (self isConnected[]){
				let t value: self select[select::"SELECT id_point, temp_min FROM points WHERE month='1' AND day='14';"];
				set listRes value: t;			
				do action: write with: [message:: listRes];
				
			}else
				do action: write with: [message::"Connection was closed"];
			
		}
		reflex close{
			do action: close;
		}
	}
}
