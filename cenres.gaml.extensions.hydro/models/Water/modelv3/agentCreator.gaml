/**
 *  agentCreator
 *  Author: bgaudou
 *  Description: 
 */

model hydro_steadymodelv3

import "section.gaml"
import "section3D.gaml"

global {
	// Database access global variables 
	map<string,string> SQLITE  <- ['dbtype'::'sqlite','database'::'../includes/hydro.db'];
	string sqlwl <- "SELECT Water_level,Time_step FROM water_levels"; 
	
	list<float> wlev <-[];	 
}

entities {
	species db parent: AgentDB {
		
		action load_step{
			return length(wlev);
		}
		
		action load_water_levels {
			return wlev;
		}
		
		action load_global{
			list<list> listTemp <- (self select [select:: sqlwl]) at 2;
			wlev  <- (listTemp accumulate (each at 0));
		}	
			 
		action init_section3D {
			ask section3D {
				string str <- "SELECT X,Y FROM sections where NAME='"+section_name+"'";
				list<point> points <- list((myself select [select:: str]) at 2) collect point({int(list(each) at 0) , int(list(each) at 1)});			
				//get polyline
				float len <- length(points);
				 
				loop ptSec over: points {
					float xPt <- ptSec.x;
					float yPt <- ptSec.y;
					
					add point({xPt, yPt}) to: ptsOfSection;					
					// add point({xPt + location.x, location.y} add_z(yPt*z_ratio)) to: ptsOfSection3D;
					add point({xPt + location.x, location.y,yPt*z_ratio}) to: ptsOfSection3D;
				}
			}				
			
		}
	}
	
}
