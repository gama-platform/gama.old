/**
 *  vector_desease
 *  Author: Arnaud Grignard and Ahmed Tidjane CISSE
 *  Description: Vector desease model. Presented at Commisco 2012.
 */

model shape_visualization


import "../../PrimitiveAgent/rain_agent.gaml"

global {
	
	//Parameter
    file shape_file_locality parameter: 'Shapefile' <- file('../includes/GIS/Localites.shp') category: 'GIS';
    file shape_file_waterhole parameter: 'Shapefile' <- file('../includes/GIS/Eaux_fusion_2003_2005.shp') category: 'GIS';
	file shape_file_vegetation parameter: 'Shapefile' <- file('../includes/GIS/vegetation_fusion_2003_2005.shp') category: 'GIS';
	
	file imageVillage <- file('../includes/images/Village-icon.png') category: 'Aspect' ;
	rgb waterholeColor parameter: 'shape color' <- rgb([5, 112, 176]) category: 'Aspect';
	
	
	const init_data parameter: 'rain data' type: matrix <- matrix(file('../includes/data/pluvio.csv')) category:'rain data';
	
	
	int rainValue <- 0;
	//amount of beat
	int totalSting <-0;

	
	list localities of: locality function: {locality as list};
	
	init {
		   ask list(cell) {
				//set color <-rgb([0, (255-rnd(100)), 0]);
				set color <-rgb([35, 200, 67]);
				
		   }
		   
		   //rain creation
		  set envRain <-geometry(shape_file_waterhole); 
		  create Rain number:1000{		
			set location <- { rnd ( envRain.width ) , rnd (envRain.height ) } ;
			set z <-rnd(envRain.width/2);
			set shape <- shape add_z z ;	
		  }
		
		   	create locality from: shape_file_locality{
		   		add self to: localities;
		   		set shape <- circle(size/2);
		   	}

		 	create waterhole from: shape_file_waterhole{
 				do initializeMosquito;	

			}

			create vegetation from: shape_file_vegetation with: [type::read ('CLASS_NAME')] {
	    
			if type='Steppes' {
				set colorQualitative <-  rgb([255, 255, 204]);
				set colorQuantitative <-  rgb([0, 255-(rainValue*4), 0]);  
			}

			if type='Steppe_arvu_arbo' {
				set colorQualitative <- rgb([194, 230, 153]) ;
				set colorQuantitative <-  rgb([0, 200-(rainValue*4), 0]); 
			}

			if type='Savane_arbustive' {
				set colorQualitative <- rgb([120, 198, 121]) ;
				set colorQuantitative <-  rgb([0, 150-(rainValue*4), 0]);
			}

			if type='Savane_arbo' {
				set colorQualitative <- rgb([35, 132, 67]) ;
				set colorQuantitative <-  rgb([0, 100-(rainValue*4), 0]);
			}
	  	}

	}

}
entities {
		
	species locality{

        int size <-1000;     
        int nbInfected<-0;
        rgb color;
       
        reflex updateColor{
        	set color <- rgb([255, 255-nbInfected*10, 255-nbInfected*10]);
        }
		aspect base {
			draw geometry: shape color: color;
		}
		
		aspect image{
			draw text: "Infected: " + string (nbInfected) at: {shape.location.x+size/2, shape.location.y-size/2} color: rgb('black') size:400;
			draw geometry: shape  color: color ;
    		draw image: imageVillage size:size;
    	}
	}
	
	species waterhole{
		
		rgb color;
		action initializeMosquito{
			create mosquito number:10{
				set shape <-geometry (point([1,1]));
				set location <- any_location_in ((myself.shape buffer(30)) - myself.shape);
				set myState <- "dry";
				set mywaterhole <-myself;
			}
		}
		
		reflex updateRainValue{
			set rainValue <- init_data at {1,time+1};
		}
		
		aspect base {
			draw geometry: shape buffer(rainValue) color: waterholeColor border:waterholeColor;
		}
	}
	
	species mosquito skills: [moving] {
		rgb color <- rgb('red') ;
		int myspeed <- 100;
		int size <-30;
		string myState;
		waterhole mywaterhole;
				
		reflex move {
			if(myState = "adult"){
				do wander speed: myspeed;
			}
		}

        reflex updateState{		
				if(rainValue>0) {
				  if(shape intersects((mywaterhole.shape buffer( rainValue)) - mywaterhole.shape)){
					set self.myState <- "adult";
				  }	
				}
		}

		reflex sting{
			let beat <-false;
			ask localities{
				if(self.shape intersects myself.shape){
					set self.nbInfected<-self.nbInfected+1;
					set totalSting <- totalSting+1;
					set beat <-true;
				}
			}
			if(beat){	
				do goto target: mywaterhole speed: myspeed ;
			}
		}
		
		aspect base {
			if(myState = "dry"){
				draw circle(size) color: rgb('yellow') border:rgb('yellow') ;
			}
			
			if(myState = "adult"){
				draw circle(size) color: rgb('red') border:rgb('red') ;
				set shape <- shape add_z 2;
			}	
		}
	}
	
	species vegetation{
		string type;
		rgb colorQuantitative;
		rgb colorQualitative;
		
		reflex updateRainValue{
	    
			if type='Steppes' {
				set colorQuantitative <-  rgb([0, 255-(rainValue*4), 0]);  
			}
				
			if type='Steppe_arvu_arbo' {
				set colorQuantitative <-  rgb([0, 200-(rainValue*4), 0]); 
			}
			
			if type='Savane_arbustive' {
				set colorQuantitative <-  rgb([0, 150-(rainValue*4), 0]);
			}

			if type='Savane_arbo' {
				set colorQuantitative <-  rgb([0, 100-(rainValue*4), 0]);
			}
			
			
		}
		aspect qualitative {
			draw geometry: shape color: colorQualitative;
		}
		
		aspect quantitative {
			draw geometry: shape color: colorQuantitative;
		}
	}



}
environment bounds: shape_file_vegetation{
	grid cell width: 300 height: 300{
		int rainValueCell <-1;
		//rgb color update: rgb([0, (255-(rainValue*4)+(exp (self.location.x/15000 * self.location.y/15000)) * 50  ), 0]);
	}
}



experiment display_shapefile type: gui {
	
	output {
		display full_display {

            grid cell;
			species locality aspect:image z:0.001;
			species waterhole aspect:base z:0.005;
		    species mosquito aspect:base z:0.006;

		}
			
		display vegetation type:opengl{
			species vegetation aspect:quantitative refresh:false;
			species locality aspect:image z:0.0010;
			species waterhole aspect:base z:0.0012;
			species mosquito aspect:base z:0.0014;
			species Rain aspect:circle;
		}	
						
		display mosquito_waterhole_display {
			species waterhole aspect:base z:0.005;
			species mosquito aspect:base z:0.006;
		}
		
		display locality_display {
			species locality aspect:image z:0.001;
		}
				
		display charts { 
			chart name: 'Rain Value' type: series background: rgb('white') size: {1,0.5} position: {0, 0} {
				data total_rain_quantity value: rainValue color: rgb('blue') ;
				//data number_of_beat value: totalSting color: rgb('red') ;
			}	
		}
	}
}




