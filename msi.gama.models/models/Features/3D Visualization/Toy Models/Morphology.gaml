/**
 *  morpho3D
 *  Author: Arnaud Grignard
 *  Description: This model displays an awesome simulation of something ...
 */

model morpho3D

global { 

	 
	int nbPointOnSection parameter: 'Number of point of the section' min: 1 <- 90 category: 'Model';
	int nbSection parameter: "Number of section" min: 1 <-20 category: 'Model';
	int lenghtSection parameter: "Distance between 2 sections" min: 1 <-1000 category: 'Model';
	int heightSection parameter: "Height of section" min: 1 <-10000 category: 'Model';
	int width_and_height_of_environment min: 10 <- nbSection* lenghtSection;
	
	int height_of_environment <- nbSection* lenghtSection;
	int width_of_environment <- heightSection;
	
	int meanDepth parameter: "Mean depth" min: 0  max: 10000 <-100 category: 'Model';
	int noise parameter: "Noise" min:0 max:500 <-50 category: 'Model';
	
	bool isEmpty parameter: "empty"  <-false category : 'Display';

	geometry shape <- rectangle(width_of_environment, height_of_environment);

	init { 
		int i <- 1;
		create section number: nbSection { 
			location <- {width_of_environment/2, height_of_environment*(i/nbSection)};
			do initZSection;
			i <-i+1;	
		}
		
		create riverPlan{
			location <- {width_of_environment/2,height_of_environment/2,-1000};	
			river_plan <- rectangle({width_of_environment,height_of_environment}) ; 
		}
	
	}
	
	reflex increaseDepth{
		meanDepth <- meanDepth + 100;	
	}
	
	reflex increaseNoise{
		noise <- noise + 1;
	}  
	  
} 
 
  
entities { 
	species section {
		
		//Polyline in x,z
		geometry zSection;
		geometry zSection2;
		geometry river_channel;
		
		action initZSection{
			
			list<point> zPoints <- [];
			list<point> zPoints2 <- [];
			
			loop i from: 0 to: nbPointOnSection {
				
				int z1_noise <-   (-rnd(noise));
				int z2_noise <-   (-rnd(noise));         
                point tmpzPoint <- {(i/nbPointOnSection)*width_of_environment,location.y} +{0,0, ((cos((i/nbPointOnSection)*180+90)*meanDepth)+z1_noise)};
                point tmpzPoint2 <- {(i/nbPointOnSection)*width_of_environment,location.y+lenghtSection} + {0,0, ((cos((i/nbPointOnSection)*180+90)*meanDepth)+z2_noise)};
                
                add tmpzPoint to: zPoints;
                add tmpzPoint2 to: zPoints2;
               
            }
            zSection <- polyline(zPoints);
            zSection2 <- polyline(zPoints2);
            list<point> list_tmp  <- list(reverse(zSection2.points));

            river_channel <- polygon( zSection.points + list_tmp);
		}
		
		reflex update {
			do initZSection;
		}
			 
		aspect default { 
			draw river_channel color: rgb('blue') empty:isEmpty;
		}
	}
	
	species riverPlan{
		
		geometry river_plan;
		
		reflex update{
			river_plan <- rectangle({width_of_environment,height_of_environment});
			location <-{location.x,location.y,-rnd(meanDepth/4)}; 
		}
		aspect default { 
			draw river_plan color: rgb('blue') empty:isEmpty;
		}
	}
}

experiment morpho type: gui {
	output {
		display morpho refresh_every: 1 type: opengl tesselation:false{
			species section;
			species riverPlan;
		}
	}
}

