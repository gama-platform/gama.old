/**
 *  Author: Truong Minh Thai (thai.truongminh@gmail.com)
 *  Description: 
 *  Description:  
 *   00: Test DBMS Connection
 */
model MDX_01
global {
			var SSAS type:map init: ['olaptype'::'SSAS/XMLA','dbtype'::'sqlserver','host'::'localhost','port'::'80','database'::'olap','user'::'olapSA','passwd'::'olapSA'];
			var MONDRIANXMLA type:map init: ['olaptype'::"MONDRIAN/XMLA",'dbtype'::'MySQL','host'::'localhost','port'::'8080','database'::'MondrianFoodMart','catalog'::'FoodMart','user'::'root','passwd'::'root'];
			var MONDRIAN type:map init: ['olaptype'::'MONDRIAN','dbtype'::'MySQL','host'::'localhost','port'::'3306','database'::'foodmart','catalog'::'../includes/FoodMart.xml','user'::'root','passwd'::'root'];
			
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
			if (self testConnection[ params::SSAS]){
				let l1 type:List <-list(self select [params:: SSAS
							, select:: 
					 "SELECT { [Measures].[Quantity], [Measures].[Price] } ON COLUMNS ,"
					 			+ " { { { [Time].[Year].[All].CHILDREN } * "
					 			+ " { [Product].[Product Category].[All].CHILDREN } * "
					 			+ "{ [Customer].[Company Name].&[Alfreds Futterkiste], " 
					 			+ "[Customer].[Company Name].&[Ana Trujillo Emparedados y helados], " 
					 			+ "[Customer].[Company Name].&[Antonio Moreno Taquería] } } } ON ROWS " 
					 +"FROM [Northwind Star] "

				]);
				write "result1:"+ l1;
			}else {
				write "Connect error";
			}


		}
	
	}
}
experiment default_expr type: gui {

}        