/**
 *  new
 *  Author: Truong Minh Thai
 *  Description: 
 */

model hydro_steadymodel04

global {
	//map PARAMS  <- ['host'::'localhost','dbtype'::'sqlserver','database'::'hydromodel','port'::'1433','user'::'sa','passwd'::'tmt'];
	map SQLITE  <- ['dbtype'::'sqlite','database'::'../includes/hydro.db'];
	string sqlstr <- "SELECT  NAME,X,Y FROM sections ";
	string selectnames <- "SELECT distinct NAME FROM sections";
	string sqlwl <- "SELECT Water_level,Time_step FROM water_levels"; 
	list sections of:section function:{list(section)};
	list water_levels <-[];
	list names;
	int width <-3000;
	int height <- 8000;
	int lenghtSection <- 100;
	int z_ratio <-10;
	float a0<-5000.0;
	int nstep <- 100;
	float H0 <-1.0 ;
	float Q0 <-0.0;
	float levelmin<-0;
	float levelmax<-0;
	float evolevel<-1.0;	
	db dba;
	init {
		create species : db {
			do action: connect with : [ params :: SQLITE ] ;
		}
		set dba <- one_of ( list ( db ) ) ; ask dba {
			do load_global ;
			do create_section ;
			do close ;
		}
		do updategeom ;
	}
	reflex computeRiver  {
		write "At cycle=" + cycle ;
		//set H0 <- abs(float(first(list(water_levels at cycle))));
		set H0 <- float ( first ( list ( water_levels at cycle ) ) ) ;
		//set H0 <-2.7;
		//set H0 <- 4.0;
		set H0<-H0+rnd(evolevel)-evolevel/2;
		write "H0:" + H0 ;
		let tmpA type : float <- 0.0 ;
		let tmp_n type : int <- length ( names ) - 1 ;
		write "tmp " + tmp_n ;
		//loop i from: tmp_n  to:0 step: -1{
					set tmpA <- polyline (first(list(section)).zPoints ) water_area_for H0 ;
		loop sec over : ( list(section) sort_by each . location . y ) {
			ask section ( sec ) {
					set A <- tmpA ;
					set H <- polyline ( zPoints ) water_level_for A ;
					//set tmpA<-A;
					write "Else.section:" + sec.name + " - A:" + A + " - H:" + H ;
					
					
			}
		}
		do updategeom ;
	}
	reflex stop {
		if ( cycle >= nstep ) {
			do halt ;
		}
	}
	action updategeom {
		let previousy <- 0.0 ;
		let li type : list <- list ( section ) ;
		loop sec over : ( li sort_by each . location . y ) {
			ask section ( sec ) {
				write ( "sec " + name + "A" + A ) ;
//				write ( "zs " + zSection.points ) ;
				let theh type:float <-polyline ( plist0 ) water_level_for a0;
				if (A>0)
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
					let p type : point <- { x , location . y } add_z ( y * z_ratio ) ;
//						let p type : point <- { x , location . y } add_z ( y ) ;
						add p to : nplist1 ;

						//					let p2 type:  point<- {x,location.y+lenghtSection} add_z (y*z_ratio);
//						let p2 type : point <- { x , previousy } add_z ( y ) ;
						let p2 type : point <- { x , previousy } add_z ( y * z_ratio ) ;
						add p2 to : nplist2 ;
					}
					add nplist1 to : plines ;
					add nplist2 to : plines2 ;
				}
				let list_tmp type : list of : point <- list ( reverse ( zSection2 . points
				) ) ;
				//            let list_tmp type: list of: point <- list(reverse(zSection.points));
				set lit <- polygon ( zSection . points + list_tmp ) ;
				set waterlit <- [ ] ;
				set watertop <- [ ] ;
				set watersect <- [ ] ;
				loop i from : 0 to : length ( plines ) - 1 {
					set waterzSection <- plines at i ;
					add waterzSection to:watersect;
					set waterzSection2 <- plines2 at i ;
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
		//			write "top:" + i + " " + watertopt . points ;
					add watertopt to : watertop ;
				}
				set previousy <- location . y ;
			}
		}
	}


}

environment  width: width height: height;

entities {
	species db parent : AgentDB {
		action load_global {
			set names <- list ( ( self select [ select :: selectnames ] ) at 2 ) ;
			set water_levels <- list ( ( self select [ select :: sqlwl ] ) at 2 ) ;
			set nstep <- length ( water_levels ) ;
			set H0 <- float ( list ( water_levels at 0 ) at 0 ) ;
			write "H0=" + H0 ;
			write "water level" + water_levels ;
		}
		action create_section {
		//get name of river_section
		//let names type:list <- (self select [select:: selectnames]) at 2;
			do action: write with : [ message :: names ] ;
			// load section_points for each river_section
			let previousy <- 0.0 ;
			let n <- length ( names ) ;
			loop i from : 0 to : ( n - 1 ) {
			// get name of section
						set previousy <- height * ( (i-1) / n ) ;
				create section {
					set section_name <- first ( list ( names at i ) ) ;

					//create sql secltion string
					let str <- "SELECT X,Y FROM sections where NAME='" + section_name + "'" ;
					do write with : [ message :: str ] ;
					//get points of section with name
					let points type : list <- ( myself select [ select :: str ] ) at 2 ;
					do write with : [ message :: points ] ;

					//get polyline
					let len <- length ( points ) ;
					set plist0 <- [ ] ;
					set plist1 <- [ ] ;
					set plist2 <- [ ] ;
					set location <- { ( i / 2 ) * width , height * ( i / n ) } ;
					loop j from : 0 to : len - 1 {
					//let p type:point <-point(list((points at j)));
						let x <- float ( ( list ( ( points at j ) ) at 0 ) ) ;
						let y <- float ( ( list ( points at j ) ) at 1 ) ;

						//let p type:point <-point([x,location.y]) add_z(y);
						let p0 type : point <- point ( [ x , y ] ) ;
						add p0 to : plist0 ;
//						let p type : point <- { x , previousy } add_z ( y * z_ratio ) ;
						let p type : point <- { x ,  location . y -height/n } add_z ( y * z_ratio ) ;
						add p to : plist1 ;

						//					let p2 type:  point<- {x,location.y+lenghtSection} add_z (y*z_ratio);
						let p2 type : point <- { x , location . y } add_z ( y * z_ratio ) ;
						add p2 to : plist2 ;
					}
					set pline <- polyline ( plist1 ) ;
					set pline2 <- polyline ( plist2 ) ;
					set list_tmp <- list ( reverse ( pline2 . points ) ) ;
					set tmplitp <- polygon ( pline . points + list_tmp ) ;
					write "poly line:" + plist0 ;
					let hs type : float <- 0 ;
					set hs <- polyline ( plist0 ) water_level_for a0 ;
					//				set hs<-H;
					write "hs:" + hs ;

					//create river section
					set location <- { ( i / 2 ) * width , height * ( i / n ) } ;
					set start <- point ( { width / 2 , 0 } ) ;
					set end <- point ( { width / 2 , height } ) ;
					set section_name <- name ;
					//set zPoints <- points;
					set zPoints <- plist0 ;
					set zSection <- pline ;
					set zSection2 <- pline2 ;
					set lit <- tmplitp ;
					set Q <- Q0 ;
					let list_tmp type : list of : point <- list ( reverse ( zSection2 . points
					) ) ;
					//            let list_tmp type: list of: point <- list(reverse(zSection.points));
					set lit <- polygon ( zSection . points + list_tmp ) ;
					write "lit:" + lit.points ;
				}
//				set previousy <- location . y ;
			}
		}
		
	}
	
	species section {
		point start;
		point end; 
		
		float posy;
		
		//Polyline in x,y
		geometry 2DRiver;
		
		string section_name;
		list zPoints of:point;
		
		geometry zSection;
		geometry zSection2;
		geometry lit;
		
		geometry 2DRiver;
		

		geometry waterzSection;
		geometry waterzSection2;
		list waterlit of:geometry;
		list watertop of:geometry;
		list watersect of:geometry;
		
		geometry water2DRiver;

				list plist0 of:point;
				list plist1 of:point;
				list plist2  of:point;
				geometry pline;
				geometry pline2;
				list list_tmp of: point;
				geometry tmplitp;
		
		//Polyline in x,z
		list zPoints2 of: point;
		
		float H<-1;
		float Q;
		float A ;	
		
		
		aspect default{
//			draw geometry: 2DRiver color: rgb('blue');
			draw geometry: lit color: rgb('red') empty:false;
			loop g over:waterlit
			{
//			draw geometry: g color: rgb('blue') empty:false;
				
			}
			loop g over:watertop
			{
			draw geometry: g color: rgb('blue') empty:false;
				
			}
			loop g over:watersect
			{
			draw geometry: g color: rgb('green') empty:false;
				
			}
			
		}
	
		
	}
}

experiment hydro_steadymodel01 type: gui {
	/** Insert here the definition of the input and output of the model */
		output {
		display morpho refresh_every: 1 type: opengl{
			species section ;
		}
	}
}
