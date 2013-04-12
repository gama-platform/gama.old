
model hydro_steadymodelv3

import "section.gaml"

global {
	int z_ratio <-100;
}

entities {
	species section3D parent: section {
		list<point> ptsOfSection3D;
		geometry river_channel;
				
		list<list> WaterptsOfSection3D;
		list<geometry> river_water;
		list<geometry> water_top;
		
		action create_river_channel {
			if(next_section = nil){
				river_channel <- polyline(ptsOfSection3D);
			}
			else {
				river_channel <- polygon(ptsOfSection3D + reverse((next_section as section3D).ptsOfSection3D));
			}
		}
		
		action updatepoints {
			// list<list<point>>
			list<list> waterHeightPolylineL <- list ( polyline ( ptsOfSection ) water_polylines_for H );
			WaterptsOfSection3D <- [];
			loop part over: waterHeightPolylineL {
				list<point> secpart<-list(part);	
				list<point> WaterptsOfSection3Dpart <- [];
				loop ptSec over: secpart {
					let xPt type: float <- ptSec.x; // point(ptSec).x;
					let yPt type: float <- ptSec.y; //point(ptSec).y;
					// add point({xPt + location.x, location.y} add_z(yPt*z_ratio)) to: WaterptsOfSection3Dpart;
					add {xPt + location.x, location.y, yPt*z_ratio}	to: WaterptsOfSection3Dpart;
				}
				add WaterptsOfSection3Dpart to:WaterptsOfSection3D;
				}
			}
			
		action updategeom {
			river_water <- [];
			water_top <- [];
			if(next_section = nil){
				loop part over:WaterptsOfSection3D
				{
					list<geometry> part2 <- part;
					add polyline(part2) to: river_water;
					add polyline([part2 at 0,part2 at (length(part)-1)]) to:water_top;
				}
			}
			else {
				loop part over:WaterptsOfSection3D
				{
					loop partnext over:section3D(next_section).WaterptsOfSection3D
					{
						list<geometry> part2 <- part;
						list<geometry> partnext2 <- partnext;
						add polygon(part2 + reverse(partnext)) to:river_water;
						add polygon([part2 at 0, part2 at (length(part)-2),partnext2 at (length(partnext2)-2),partnext2 at 0]) to:water_top;
					}	
				}
			}
		}		
		
		aspect aspect2D {
			draw polyline(ptsOfSection3D) color: rgb('red');
		}
		
		aspect channel{
			draw river_channel color: rgb('red');
		}
		
		aspect default3D{
			draw river_channel color: rgb('red') empty:true;
			loop g over: river_water
			{
				draw g color: rgb('blue') empty:true;
			}
			loop g over: water_top
			{
				draw g color: rgb('blue') empty:false;
			}
		}
	}
}
