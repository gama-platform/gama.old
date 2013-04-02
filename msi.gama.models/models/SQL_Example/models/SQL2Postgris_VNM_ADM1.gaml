/**
 *  SQLConnection
 *  Author: Truong Minh Thai (thai.truongminh@gmail.com)
 *  Description: 
 *   Copy data from SQL into postgis
 *   
 */

model InsertWithValues

global {
	var PARAMS type:map init: ['host'::'localhost','dbtype'::'sqlserver','port'::'1433','database'::'bph','user'::'sa','passwd'::'tmt'];
	var PG type:map init: ['host'::'localhost','dbtype'::'postgis','database'::'bph','port'::'5432','user'::'postgres','passwd'::'tmt'];

	init {
		create species: toto number: 1 ;
		ask (toto at 0)	
		{
			let t1 value: self select[params :: PARAMS
									,transform::true   // If you want transform geometry values from GIS to Absolute (GAMA Geometry), default is false									  
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
				write "row " +(i+1)	;
				do action: insert{ 
					arg params value: PG; 
					arg into value: "vnm_adm1";
                	arg values value: list(rows at i)  ;
                	arg transform value: true; // If you want transform geometry values from Absolute (GAMA Geometry) to GIS, default is false
                	
 				}
 			    write "finish "+ (i+1);
			}	
					
		}
	}
}   
entities {  
	species toto skills: [SQLSKILL] {  
 		reflex delete {	 
 			do action: executeUpdate{ 
 				arg params value: PG; 			
 				arg updateComm value: "DELETE FROM vnm_adm1  ";
            }
 			write "All Data were deleted";
 		}
	} 
}      