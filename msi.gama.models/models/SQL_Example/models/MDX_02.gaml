/**
 *  Author: Truong Minh Thai (thai.truongminh@gmail.com)
 *  Description: 
 *  Description:  
 *   00: Test DBMS Connection
 */
model MDX_02
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
//			if (self testConnection[ params::SSAS]){
				let l1 type:list <-list(self select [params:: SSAS
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
//			}else {
//				write "Connect error";
//			}

//			if (self testConnection[ params::MONDRIANXMLA]){
				let l2 type:list <-list(self select [params:: MONDRIANXMLA
								, select::
			  " select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} ON COLUMNS,"
			 +"     Hierarchize(Union(Union(Union({([Promotion Media].[All Media], [Product].[All Products])}, Crossjoin([Promotion Media].[All Media].Children, {[Product].[All Products]})), "
			 +"		Crossjoin({[Promotion Media].[Daily Paper, Radio, TV]}, [Product].[All Products].Children)), Crossjoin({[Promotion Media].[Street Handout]}, [Product].[All Products].Children))) ON ROWS "
			 +" from [?] "
			 +" where [Time].[?] "
			 ,values::["Sales",1997]

				]);
			write "result2:"+ l2;
//			}else {
//				write "Connect error";
//			}

// 			if (self testConnection[ params::MONDRIAN]){
				let l3 type:list <-list(self select [params:: MONDRIAN
								, select::
			  " select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} ON COLUMNS,"
			 +"     Hierarchize(Union(Union(Union({([Promotion Media].[All Media], [Product].[All Products])}, Crossjoin([Promotion Media].[All Media].Children, {[Product].[All Products]})), "
			 +"		Crossjoin({[Promotion Media].[?, Radio, TV]}, [Product].[All Products].Children)), Crossjoin({[Promotion Media].[Street Handout]}, [Product].[All Products].Children))) ON ROWS "
			 +" from [?] "
			 +" where [Time].[?] "
			 ,values::["Daily Paper","Sales",1997]

				]);
			write "result3:"+ l3;
//			}else {
//				write "Connect error";
//			}



		}
	}
}
experiment default_expr type: gui {

}        