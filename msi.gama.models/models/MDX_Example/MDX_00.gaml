/**
 *  Author: Truong Minh Thai (thai.truongminh@gmail.com)
 *  Description: 
 *  Description:  
 *   00: Test DBMS Connection
 */
model MDX_00
global {
			var SSAS type:map init: ['olaptype'::'SSAS/XMLA','dbtype'::'sqlserver','host'::'localhost','port'::'80','database'::'olap','user'::'olapSA','passwd'::'olapSA'];
			var MONDRIANXMLA type:map init: ['olaptype'::"MONDRIAN/XMLA",'dbtype'::'MySQL','host'::'localhost','port'::'8080','database'::'MondrianFoodMart','catalog'::'FoodMart','user'::'root','passwd'::'root'];
			var MONDRIAN type:map init: ['olaptype'::'MONDRIAN','dbtype'::'MySQL','host'::'localhost','port'::'3306','database'::'foodmart','catalog'::'../includes/FoodMart.xml','user'::'root','passwd'::'root'];
			var OOLAP type:map init: ['olaptype'::'ORACLEOLAP','dbtype'::'ORACLE','host'::'localhost','port'::'3306','database'::'foodmart','catalog'::'../includes/FoodMart.xml','user'::'root','passwd'::'root'];

	init {
		create species: toto number: 1;
	}
}
entities { 
	species toto skills: [ MDXSKILL ] {
		var listRes type: list init: [ ]; 
		//var obj type: obj;
		reflex testConnection{
			do action: helloWorld;
			do action: write with: [message::"Current Time "+ self timeStamp[]];
			do action: write with: [message::"Connection to SSAS is "+ self testConnection[ params::SSAS]];
			do action: write with: [message::"Connection to Mondrian/XMLA is "+self testConnection[ params::MONDRIANXMLA]];
			do action: write with: [message::"Connection to Mondrian is "+self testConnection[ params::MONDRIAN]];
			do action: write with: [message::"Connection to Oracle OLAP is "+self testConnection[ params::OOLAP]];
//			do action: write with: [message::"Connection to ORACLE is "+self testConnection[ params::ORACLE]];
		}
	}
}
experiment default_expr type: gui {

}        