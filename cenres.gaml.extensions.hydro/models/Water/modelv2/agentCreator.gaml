/**
 *  agentCreator
 *  Author: bgaudou
 *  Description: 
 */

model agentCreator

import "section.gaml"
import "section3D.gaml"

global {
	// Database access global variables 
	map SQLITE  <- ['dbtype'::'sqlite','database'::'../includes/hydro.db'];
	string sqlstr <- "SELECT  NAME,X,Y FROM sections ";
	string selectnames <- "SELECT distinct NAME FROM sections";
	string sqlwl <- "SELECT Water_level,Time_step FROM water_levels"; 
	
	list names of: string <- [];
	list water_levels <-[];
	int nstep <- 100;		
}

entities {
	species db parent: AgentDB {
		
		action load_global{
			set names <- list((self select [select:: selectnames]) at 2);
			set water_levels  <- list( (self select [select:: sqlwl]) at 2);
			set nstep <- length(water_levels);
		}	
			 
		action create_section {

			ask section3Ds {
				let str <- "SELECT X,Y FROM sections where NAME='"+section_name+"'";
				let points type:list of: list <- (myself select [select:: str]) at 2;
								
				//get polyline
				let len <- length(points);
				
				loop ptSec over: points {
					let xPt type: float <- ptSec at 0;
					let yPt type: float <- ptSec at 1;
					
					add point({xPt, yPt}) to: ptsOfSection;					
					add point({xPt + location.x, location.y} add_z(yPt*z_ratio)) to: ptsOfSection3D;
				}
			}				
			
		}
	}
	
}
