/**
 *  section3D
 *  Author: bgaudou
 *  Description: 
 */

model section3D

import "section.gaml"

global {
	int z_ratio <-100;
	
	list section3Ds of: section3D function:{list(section3D)};
}

entities {
	species section3D parent: section {
		list ptsOfSection3D of:point;
		list WaterptsOfSection3D of:list;
		

		geometry river_channel;
		list river_water;
		list water_top;
		
		action create_river_channel {
			if(next_section = nil){
				set river_channel <- polyline(ptsOfSection3D);
			}
			else {
				set river_channel <- polygon(ptsOfSection3D + list(reverse((next_section as section3D).ptsOfSection3D)));
			}
		}
		
		action updatepoints {
			let waterHeightPolylineL type : list <- list ( polyline ( ptsOfSection ) water_polylines_for H );
			set WaterptsOfSection3D<-[];
				loop part over: waterHeightPolylineL {
				let secpart<-list(part);	
				let len <- length(secpart);
				let WaterptsOfSection3Dpart<-[];
				loop ptSec over: secpart {
					let xPt type: float <- point(ptSec).x;
					let yPt type: float <- point(ptSec).y;
					add point({xPt + location.x, location.y} add_z(yPt*z_ratio)) to: WaterptsOfSection3Dpart;
					
					}
				add WaterptsOfSection3Dpart to:WaterptsOfSection3D;
					
				}
			write ("sec3D "+WaterptsOfSection3D);			
			}
			
		action updategeom {
				set river_water<-[];
				set water_top<-[];
			if(next_section = nil){
//				set river_channel <- polyline(waterHeightPolyline);
				loop part over:WaterptsOfSection3D
				{
				add polyline(part) to:river_water;
				add polyline([list(part) at 0,list(part) at (length(list(part))-1)]) to:water_top;
				}
			}
			else {
				loop part over:WaterptsOfSection3D
				{
				loop partnext over:section3D(next_section).WaterptsOfSection3D
				{
				add polygon(part + list(reverse(partnext))) to:river_water;
				add polygon([list(part) at 0,list(part) at (length(list(part))-2),list(partnext) at (length(list(partnext))-2),list(partnext) at 0]) to:water_top;
				}
				
				}
			}
			write ("wrt "+geometry(river_water at 0).points);			
			write ("wtt "+geometry(water_top at 0).points);			
//		let previousy <- 0.0 ;
//		// let li type : list <- list ( section3D ) ;
//		let plinesprec type : list <- [ ] ;
//		loop sec over : ( section3Ds sort_by each.location.y ) {
//			ask section3D ( sec ) {
//				let theh type:float <-polyline ( plist0 ) water_level_for 1;//A;
//				if (A>areamax)
//				{
//				set theh  <-polyline ( plist0 ) water_level_for areamax;					
//				}
//				if ((A>0) and (A<=areamax))
//				{
//				set theh  <-polyline ( plist0 ) water_level_for A;
//					
//				}
//				let plines0 type : list <- list ( polyline ( plist0 ) water_polylines_for theh ) ;
//				let plines type : list <- [ ] ;
//				let plines2 type : list <- [ ] ;
//
//				loop pl over : plines0 {
//					let nplist1 type : list of : point <- [ ] ;
//					let nplist2 type : list of : point <- [ ] ;
//					loop j from : 0 to : length ( list ( pl ) ) - 1 {
//						let x <- float ( point ( list ( pl ) at j ) . x ) ;
//						let y <- float ( point ( list ( pl ) at j ) . y ) ;
//
//						let p type : point <- { x ,previousy} add_z ( y * z_ratio ) ;
//						add p to : nplist1 ;
//						let p2 type : point <- { x ,  location . y  } add_z ( y * z_ratio ) ;
//						add p2 to : nplist2 ;
//					}
//					add nplist1 to : plines ;
//					add nplist2 to : plines2 ;
//				}
//				set plines<-plinesprec;
//				set plinesprec<-plines2;
//				let list_tmp type : list of : point <- list ( reverse ( zSection2 . points
//				) ) ;
//				//            let list_tmp type: list of: point <- list(reverse(zSection.points));
//				set lit <- polygon ( zSection . points + list_tmp ) ;
//				set waterlit <- [ ] ;
//				set watertop <- [ ] ;
//				set watersect <- [ ] ;
//				loop i from : 0 to : length ( plines ) - 1 {
//				loop j from : 0 to : length ( plines2 ) - 1 {
//					set waterzSection <- plines at i ;
//					add waterzSection to:watersect;
//					set waterzSection2 <- plines2 at j ;
//					let list_tmp type : list of : point <- list ( waterzSection2 . points ) ;
//					let waterlitt <- polygon ( waterzSection . points + list_tmp ) ;
//					add waterlitt to : waterlit ;
//					let list_tmp2 type : list of : point <- [];	
//					add (waterzSection.points at 0) to:list_tmp2;
//					add (waterzSection.points at (length(waterzSection.points)-2)) to:list_tmp2;
//					add (waterzSection2.points at (length(waterzSection2.points)-2)) to:list_tmp2;
//					add (waterzSection2.points at 0) to:list_tmp2;
//					let watertopt <- polygon ( list_tmp2 ) ;
//					add watertopt to : watertop ;
//					
//					}
//				}
//				set previousy <- location . y ;
//			}
//		}
		}		
		
		aspect aspect2D {
			draw geometry: polyline(ptsOfSection3D) color: rgb('red');
		}
		
		aspect channel{
			draw geometry: river_channel color: rgb('red');
		}
		
		aspect default3D{
			draw geometry: river_channel color: rgb('red') empty:true;
			loop g over: river_water
			{
				draw geometry: g color: rgb('blue') empty:true;
			}
			loop g over: water_top
			{
				draw geometry: g color: rgb('blue') empty:false;
			}
		}
	}
}
