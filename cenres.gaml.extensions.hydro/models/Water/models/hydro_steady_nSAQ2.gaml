/**
 *  new
 *  Author: Truong Minh Thai
 *  Description: 
 */

model hydro_steadymodel04

global {
	float PI <- 3.141592653589793238462643383;
	
	// Shapefiles 
	file riverFile <- file('../includes/mythanh_river.shp');
	file sectionFile <- file('../includes/section_mt.shp');

	// Database access global variables 
	map SQLITE  <- ['dbtype'::'sqlite','database'::'../includes/hydro.db'];
	string sqlstr <- "SELECT  NAME,X,Y FROM sections ";
	string selectnames <- "SELECT distinct NAME FROM sections";
	string sqlwl <- "SELECT Water_level,Time_step FROM water_levels"; 

	// Environment 
	int width <-3000;
	int height <- 1000; 	
	
	list sections of:section function:{list(section)};
	list water_levels <-[];
	list names;
	
	float cycleH0variation<-0.5;

	int lenghtSection <- 1;
	int z_ratio <-10;
	
	int nstep <- 100;
	
	// Water-related parameters
	float H0 <- -4.31;
	float Q0 <-20.0;
	float n <- 0.02;
	
	float maxPossibleArea<-0;
	
	init {
		create river from: riverFile;
		create section from: sectionFile with: [section_name::read('NODE_LABEL')];
		
		create species: db number: 1  
		{ 
			do action: connect with: [params::SQLITE];	
			do load_global;
			do create_section_Benoit;
			do close;
		}
		loop i from: 0 to: length(section)-2 step: 1 {
			ask (list(section) at i) {
				set next_section <- list(section) at (i+1);
			}
		}
		
		write "test " + (2000 * 0.000001); 
	}
	reflex computeRiver when: cycle<nstep {
		write "At cycle="+ cycle;
		//set H0 <- abs(float(first(list(water_levels at cycle))));
		set H0 <- float(first(list(water_levels at cycle)));
//		set H0 <- H0+rnd(cycleH0variation)-cycleH0variation/2;

			ask (list(section) at (length(names)-1)){
				if (H0<(plist0 with_min_of each.y).y)
				{
					write("back to min "+H0+" -> "+(plist0 with_min_of each.y).y);
					set H0<-(plist0 with_min_of each.y).y;
				}
				if (H0>(plist0 with_max_of each.y).y)
				{
					write("back to max "+H0+" -> "+(plist0 with_max_of each.y).y);
					set H0<-(plist0 with_max_of each.y).y;
				}
				
			}

		
		//set H0 <-2.7;
		//set H0 <- 4.0;
		write "H0:"+H0;
		
		// let tmpA type:float <-0.0;
		let tmp_n type:int <- length(names)-1;
		
		
		
		// write "tmp " +tmp_n;
		// loop i from: tmp_n  to:0 step: -1{
		loop i from:0  to:tmp_n {
			// write "i " +i;
			// let tmpname type:string <- first(names at (tmp_n-i));
			// write "============== Section name:"+tmpname;
			// ask sections first_with (each.section_name = tmpname){
			ask (list(section) at (tmp_n - i)){
				if (i = 0){ // Computation for the downstream
					set H <-H0;
					set Q <- Q0;
					do compute_A;
					do compute_R;
					do compute_s;
					// write "zpoints:" +zPoints;
					write section_name + " -  A: "+ A +" - H: "+H + " - R:  " + R + " -s: " + s;
				}else{
					set Q <- Q0;
					do compute_H;
					do compute_A;
					do compute_R;
					do compute_s;					
					// write "Else.section:" +tmpname + " - A:"+ A +" - H:"+H;
					write section_name + " -  A: "+ A +" - H: "+H + " - R:  " + R + " -s: " + s;					
				}
				//write "section:" +tmpname + " - A:"+ A +" - H:"+H;
			}
			
		}
		let sect type:section<-one_of(list(section));
		let lp type:list <-sect.plist0;
		let maxh type:float <- (lp with_max_of each.y).y;
		set maxPossibleArea <-polyline(sect.plist0) water_area_for maxh;
			loop sec over : ( list(section) sort_by each . location . y ) {
		set maxh  <- (sec.plist0 with_max_of each.y).y;
		set sec.areamax<-polyline(sec.plist0) water_area_for maxh;
		set maxPossibleArea <-min([maxPossibleArea,polyline(sec.plist0) water_area_for maxh]);
				
	
	}	
	write("maxA"+maxPossibleArea);
		do updategeom;
	
	}	
	reflex stop {
		if  (cycle>=nstep){
			do halt;
		}
		
	}

	action updategeom {
		let previousy <- 0.0 ;
		let li type : list <- list ( section ) ;
				let plinesprec type : list <- [ ] ;
		loop sec over : ( li sort_by each . location . y ) {
			ask section ( sec ) {
//				write ( "sec " + name + "A" + A ) ;
//				write ( "zs " + zSection.points ) ;
				let theh type:float <-polyline ( plist0 ) water_level_for 1;//A;
				if (A>areamax)
				{
				set theh  <-polyline ( plist0 ) water_level_for areamax;					
				}
				if ((A>0) and (A<=areamax))
				{
				set theh  <-polyline ( plist0 ) water_level_for A;
					
				}
				let plines0 type : list <- list ( polyline ( plist0 ) water_polylines_for theh ) ;
//					write "pl0: " + plist0 ;
//				let plines0 type : list <- list ( polyline ( zPoints ) water_polylines_for 30.0 ) ;
				let plines type : list <- [ ] ;
				let plines2 type : list <- [ ] ;
//					write "pk0: " + length(plines0) ;
//					write "pk0v: " + list(plines0 at 0) ;
				loop pl over : plines0 {
					let nplist1 type : list of : point <- [ ] ;
					let nplist2 type : list of : point <- [ ] ;
					loop j from : 0 to : length ( list ( pl ) ) - 1 {
						let x <- float ( point ( list ( pl ) at j ) . x ) ;
						let y <- float ( point ( list ( pl ) at j ) . y ) ;

						//let p type:point <-point([x,location.y]) add_z(y);
					let p type : point <- { x ,previousy} add_z ( y * z_ratio ) ;
//						let p type : point <- { x , location . y } add_z ( y ) ;
						add p to : nplist1 ;

						//					let p2 type:  point<- {x,location.y+lenghtSection} add_z (y*z_ratio);
//						let p2 type : point <- { x , previousy } add_z ( y ) ;
						let p2 type : point <- { x ,  location . y  } add_z ( y * z_ratio ) ;
						add p2 to : nplist2 ;
					}
					add nplist1 to : plines ;
					add nplist2 to : plines2 ;
				}
				set plines<-plinesprec;
				set plinesprec<-plines2;
				let list_tmp type : list of : point <- list ( reverse ( zSection2 . points
				) ) ;
				//            let list_tmp type: list of: point <- list(reverse(zSection.points));
				set lit <- polygon ( zSection . points + list_tmp ) ;
				set waterlit <- [ ] ;
				set watertop <- [ ] ;
				set watersect <- [ ] ;
				loop i from : 0 to : length ( plines ) - 1 {
				loop j from : 0 to : length ( plines2 ) - 1 {
					set waterzSection <- plines at i ;
					add waterzSection to:watersect;
					set waterzSection2 <- plines2 at j ;
		//			write "poly lines:" + i + " " + waterzSection . points ;
		//			write "poly lines2:" + i + " " + waterzSection2 . points ;
					//            let list_tmp type: list of: point <- list(reverse(waterzSection2.points));
					//            let list_tmp type: list of: point <- list(waterzSection2.points);
					//            let list_tmp type: list of: point <- list(reverse(waterzSection2.points));
					let list_tmp type : list of : point <- list ( waterzSection2 . points ) ;
					let waterlitt <- polygon ( waterzSection . points + list_tmp ) ;
		//			write "waterlit :" + i + " " + waterzSection . points + " " + list_tmp ;
					add waterlitt to : waterlit ;
					let list_tmp2 type : list of : point <- [];	
//					let ptt type:point <-{(waterzSection.points at 0).x,theh};
//					add {(waterzSection.points at 0).x,(waterzSection.points at 0).y} add_z (theh) to:list_tmp2;		
//					set ptt <-{(waterzSection.points at (length(waterzSection.points)-1)).x,theh};		
//					add {(waterzSection.points at (length(waterzSection.points)-2)).x,(waterzSection.points at (length(waterzSection.points)-2)).y} add_z (theh) to:list_tmp2;		
//					set ptt <-{(waterzSection2.points at (length(waterzSection2.points)-1)).x,theh};		
	//				add {(waterzSection2.points at (length(waterzSection2.points)-2)).x,(waterzSection2.points at (length(waterzSection2.points)-2)).y} add_z (theh) to:list_tmp2;		
//					set ptt <-{(waterzSection2.points at 0).x,theh};		
		//			add {(waterzSection2.points at 0).x,(waterzSection2.points at 0).x} add_z (theh) to:list_tmp2;		
					add (waterzSection.points at 0) to:list_tmp2;
					add (waterzSection.points at (length(waterzSection.points)-2)) to:list_tmp2;
					add (waterzSection2.points at (length(waterzSection2.points)-2)) to:list_tmp2;
					add (waterzSection2.points at 0) to:list_tmp2;
					let watertopt <- polygon ( list_tmp2 ) ;
//					write "top:" + i + " " + watertopt . points ;
					add watertopt to : watertop ;
					
					}
				}
				set previousy <- location . y ;
			}
		}
	}



}

environment bounds: riverFile; // width: width height: height;

entities {
	species db parent: AgentDB {
		
		action load_global{
			set names <- list((self select [select:: selectnames]) at 2);
			set water_levels  <- list( (self select [select:: sqlwl]) at 2);
			set nstep <- length(water_levels);
	// ben		set H0 <-float(list(water_levels at 0) at 0);
			// write "H0=" +H0;	
			// write "water level" + water_levels;
		}	
			 
		action create_section_Benoit {

				let plineprec type : geometry <- [{0,0}] ;

			let n <-length(names);
			ask sections {
				let str <- "SELECT X,Y FROM sections where NAME='"+section_name+"'";
				// do write with: [message::str];
				//get points of section with name
				let points type:list <- (myself select [select:: str]) at 2;
				// do write with: [message::points];
				// set points <- (points collect (point({each.x, each.y})));
				
				//get polyline
				let len <- length(points);
				set plist0  <-[];
				let plist1 type:list of:point <-[];
				let plist2 type:list of:point <-[];				
				
				// set location <- {(i/2)*width,height*(i/n)};
				
				// let section_width type: float <- (list((points at j)) at 0) max_of ;
				
				loop j from:0 to: len-1 {
					//let p type:point <-point(list((points at j)));
					let x <- float((list((points at j)) at 0)) + self.location.x;
					let y <- float((list(points at j)) at 1);
					
					//let p type:point <-point([x,location.y]) add_z(y);
					
					let p0 type:point <-point([x,y]);
					add p0 to:plist0;
					
					let p type:point <-{x,location.y} add_z(y*z_ratio);
					add p to: plist1;
					
					let p2 type:  point<- {x,location.y+lenghtSection} add_z (y*z_ratio);
					add p2 to:plist2;
					 
				}
				let pline type:geometry <-polyline(plist1);
				let pline2 type:geometry <-polyline(plist2);
				set pline<-plineprec;
				set plineprec<-pline2;
				let list_tmp type: list of: point <- list(reverse(pline2.points));
				let tmplit <- polygon( pline.points + list_tmp);
				 write "poly line:" +plist0;
				
				
				//create river section
				// set location <- {(i/2)*width,height*(i/n)};
				set start <- point({width/2,0});
				set end <- point({width/2,height});
					
				//	set section_name <- name;
					//set zPoints <- points;
				set zPoints <- plist0;
				set zSection <- pline;
            	set zSection2 <- pline2;
				set lit <-tmplit;
				// set Q<-Q0;
				
					set zPoints <- plist0 ;
					set zSection <- pline ;
					set zSection2 <- pline2 ;
				 write "poly line:"+ zSection.points;
				 write "poly line2:"+ zSection2.points;
					let list_tmp type : list of : point <- list ( reverse ( zSection2 . points
					) ) ;
					//            let list_tmp type: list of: point <- list(reverse(zSection.points));
					set lit <- polygon ( zSection . points + list_tmp ) ;				
			}				
		}
//			
//		action create_section{
//			//get name of river_section
//			//let names type:list <- (self select [select:: selectnames]) at 2;
//			// do action: write with: [message::names];
//			// load section_points for each river_section
//			let n <-length(names);	
//			loop i from: 0 to: ( n - 1 ) {
//				// get name of section
//				let name type:string <- first(list(names at i));
//				
//				//create sql secltion string
//				let str <- "SELECT X,Y FROM sections where NAME='"+name+"'";
//				// do write with: [message::str];
//				//get points of section with name
//				let points type:list <- (self select [select:: str]) at 2;
//				// do write with: [message::points];
//				
//				
//				//get polyline
//				let len <- length(points);
//				let plist0 type:list of:point <-[];
//				let plist1 type:list of:point <-[];
//				let plist2 type:list of:point <-[];
//				
//				set location <- {(i/2)*width,height*(i/n)};
//				
//				loop j from:0 to: len-1 {
//					//let p type:point <-point(list((points at j)));
//					let x <- float((list((points at j)) at 0));
//					let y <- float((list(points at j)) at 1);
//					
//					//let p type:point <-point([x,location.y]) add_z(y);
//					
//					let p0 type:point <-point([x,y]);
//					add p0 to:plist0;
//					
//					let p type:point <-{x,location.y} add_z(y*z_ratio);
//					add p to: plist1;
//					
//					let p2 type:  point<- {x,location.y+lenghtSection} add_z (y*z_ratio);
//					add p2 to:plist2;
//					 
//				}
//				
//				let pline type:geometry <-polyline(plist1);
//				let pline2 type:geometry <-polyline(plist2);
//				let list_tmp type: list of: point <- list(reverse(pline2.points));
//				let tmplit <- polygon( pline.points + list_tmp);
//				// write "poly line:" +plist0;
//				
//				
//				//create river section
//				create section{
//					set location <- {(i/2)*width,height*(i/n)};
//					set start <- point({width/2,0});
//					set end <- point({width/2,height});
//					
//					set section_name <- name;
//					//set zPoints <- points;
//					set zPoints <- plist0;
//					set zSection <- pline;
//            		set zSection2 <- pline2;
//					set lit <-tmplit;
//					set Q<-Q0;
//					
//				}
//				
//			}
//			
	}
	
	species section {
		point start;
		point end; 
		//Polyline in x,y
		geometry TwoDRiver;
		
		string section_name;
		
		list zPoints of:point;
		
		geometry zSection;
		geometry zSection2;
		geometry lit;
		
		float areamax;
		
				list plist0 of:point;
						geometry waterzSection;
		geometry waterzSection2;
		list waterlit of:geometry;
		list watertop of:geometry;
		list watersect of:geometry;
				
		section next_section;
		int section_id;
		float H;
		float Q;
		float A;
		float R;
		float s;	
		
		reflex testS when: (s > 0.0001){
			write 'Section ' + name + ' has trespassed s = 10^-4';
		}
	
		action compute_H {
			let d type: float <- (self.location distance_to next_section.location);
			// write "d " + d + " s " + next_section.s + " H " + next_section.H;
			// let heigth type: float <- d * sin(next_section.s * 180 / (PI * 3600));
			let heig type: float <- d * next_section.s;
			// write "height " + heig;
			set H <- heig + next_section.H; 
		}
		
		action compute_A {
			set A <- polyline(zPoints) water_area_for H;
		}
	
		action compute_R {
			// Hypothesis !
			let section_width type: float <- (zPoints max_of each.x) - (zPoints min_of each.x);
			set R <- A / section_width;
		}
		
		action compute_s {
			// From n = 1/Q * A* R^(2/3) * S ^(1/2)
			set s <- (n * Q / (A * R ^(2/3)))^2;
		}
		
		aspect default{
			// draw geometry: 2DRiver color: rgb('blue');
			draw geometry: lit color: rgb('red') empty:true;
					loop g over:watertop
			{
			draw geometry: g color: rgb('blue') empty:false;
				
			}
		}
		
		aspect sectio {
			draw geometry: shape;
		}
	}
	
	species river {
		aspect default {
			draw geometry: shape;
		}
	}
}

experiment hydro_steadymodel01 type: gui {
	/** Insert here the definition of the input and output of the model */
	output {
		display morpho refresh_every: 1 type:opengl{
			species section aspect: default;
			species river;
		}
		display displayMaree {
			chart 'chartMaree' type: histogram {
				ask list(section){
						data 'd' value: self.H;			
				}
			}
		}
	}
}
