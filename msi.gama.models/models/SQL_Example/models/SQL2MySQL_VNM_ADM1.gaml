/**
 *  Author: Truong Minh Thai (thai.truongminh@gmail.com)
 *  Description: 
 *   Copy data from SQL into MySQL
 *   
 */

model InsertWithValues

global {
	var PARAMS type:map init: ['host'::'localhost','dbtype'::'sqlserver','port'::'1433','database'::'bph','user'::'sa','passwd'::'tmt'];
	var MySQL type:map init: ['host'::'localhost','dbtype'::'MySQL','port'::'3306','database'::'bph','user'::'root','passwd'::'root'];

	init {
		create species: toto number: 1 ;
		ask (toto at 0)	
		{
			let t1 value: self select[params :: PARAMS
									//,transform::true   // If you want transform geometry values from GIS to Absolute (GAMA Geometry), default is false									  
									, select:: "SELECT ID_0
												,ISO
												,NAME_0
												,ID_1
												,NAME_1
												,VARNAME_1
												,NL_NAME_1
												,HASC_1
												,CC_1
												,TYPE_1
												,ENGTYPE_1
												,VALIDFR_1
												,VALIDTO_1
												,REMARKS_1
												,Shape_Leng
												,Shape_Area
												,GEOM.STAsBinary() as GEOM
												FROM VNM_adm1"
									];
 			let rows type:list value: t1 at 2;
 			let n type:int value: length(rows);
 			write "lenght:"+n;
			loop from: 0 to: n-1 var: i{
				do action: insert{ 
					arg params value: MySQL; 
					arg into value: "vnm_adm1";
                	arg values value: list(rows at i)  ;
                	//arg transform value: true; // If you want transform geometry values from Absolute (GAMA Geometry) to GIS, default is false
 				}
 				write "Copied row " +(i+1);
			}	
					
		}
	}
}   
entities {  
	species toto skills: [SQLSKILL] {  
// 		reflex delete {	 
// 
//            let t value: self select[params :: MySQL, select:: "select * from vnm_adm1 ;"];
// 			
// 			write "Selected before delete";
//			do action: executeUpdate{ 
// 				arg params value: MySQL; 			
// 				arg updateComm value: "DELETE FROM vnm_adm1  ";
//            }
//            
//            let t value: self select[params :: MySQL, select:: "select * from vnm_adm1 ;"];
// 			
// 			write "Selected after delete";
// 		}
	} 
}      