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
	int height <- 1000;
	int lenghtSection <- 1;
	int z_ratio <-10;
	
	int nstep <- 100;
	float H0 <-1.0 ;
	float Q0 <-0.0;
	init {
		create species: db number: 1  
		{ 
			do action: connect with: [params::SQLITE];	
			do load_global;
			do create_section;
			do close;
		}
	}
	reflex computeRiver when: cycle<nstep {
		write "At cycle="+ cycle;
		//set H0 <- abs(float(first(list(water_levels at cycle))));
		set H0 <- float(first(list(water_levels at cycle)));
		//set H0 <-2.7;
		//set H0 <- 4.0;
		write "H0:"+H0;
		
		let tmpA type:float <-0.0;
		let tmp_n type:int <- length(names)-1;
		write "tmp " +tmp_n;
		//loop i from: tmp_n  to:0 step: -1{
		loop i from:0  to:tmp_n {
			write "i " +i;
			let tmpname type:string <- first(list(names at (tmp_n-i)));
			write "Section name:"+tmpname;
			ask sections first_with (each.section_name = tmpname){
				if (i=0){
					set H <-H0;
					set A <- polyline(zPoints) water_area_for H;
					set tmpA<-A;
					write "zpoints:" +zPoints;
					write "0.section:" +tmpname + " - A:"+ A +" - H:"+H;
				}else{
					set A <- tmpA;	
					set H <- polyline(zPoints) water_level_for A;
					//set tmpA<-A;
					write "Else.section:" +tmpname + " - A:"+ A +" - H:"+H;
				}
				//write "section:" +tmpname + " - A:"+ A +" - H:"+H;
			}
			
		}
	
	}	
	reflex stop {
		if  (cycle>=nstep){
			do halt;
		}
		
	}

}

environment  width: width height: height;

entities {
	species db parent: AgentDB {
		
		action load_global{
			set names <- list((self select [select:: selectnames]) at 2);
			set water_levels  <- list( (self select [select:: sqlwl]) at 2);
			set nstep <- length(water_levels);
			set H0 <-float(list(water_levels at 0) at 0);
			write "H0=" +H0;	
			write "water level" + water_levels;
		}	
			
		action create_section{
			//get name of river_section
			//let names type:list <- (self select [select:: selectnames]) at 2;
			do action: write with: [message::names];
			// load section_points for each river_section
			let n <-length(names);	
			loop i from: 0 to: ( n - 1 ) {
				// get name of section
				let name type:string <- first(list(names at i));
				
				//create sql secltion string
				let str <- "SELECT X,Y FROM sections where NAME='"+name+"'";
				do write with: [message::str];
				//get points of section with name
				let points type:list <- (self select [select:: str]) at 2;
				do write with: [message::points];
				
				
				//get polyline
				let len <- length(points);
				let plist0 type:list of:point <-[];
				let plist1 type:list of:point <-[];
				let plist2 type:list of:point <-[];
				
				set location <- {(i/2)*width,height*(i/n)};
				
				loop j from:0 to: len-1 {
					//let p type:point <-point(list((points at j)));
					let x <- float((list((points at j)) at 0));
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
				let list_tmp type: list of: point <- list(reverse(pline2.points));
				let tmplit <- polygon( pline.points + list_tmp);
				write "poly line:" +plist0;
				
				
				//create river section
				create section{
					set location <- {(i/2)*width,height*(i/n)};
					set start <- point({width/2,0});
					set end <- point({width/2,height});
					
					set section_name <- name;
					//set zPoints <- points;
					set zPoints <- plist0;
					set zSection <- pline;
            		set zSection2 <- pline2;
					set lit <-tmplit;
					set Q<-Q0;
					
				}
				
			}
			
		}
		


	}
	
	species section {
		point start;
		point end; 
		//Polyline in x,y
		geometry 2DRiver;
		
		string section_name;
		list zPoints of:point;
		
		geometry zSection;
		geometry zSection2;
		geometry lit;
		
		
		float H;
		float Q;
		float A ;	
		
		aspect default{
			draw geometry: 2DRiver color: rgb('blue');
			draw geometry: lit color: rgb('blue') empty:false;
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
