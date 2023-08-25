/**
* Name: Trafic Group (R2D2) MAPS7 - LWR Model
* Author: A. Banos, N. Corson, C. Pivano, L. Rajaonarivo, P. Taillandier
* Description: The LWR model was proposed by Lighthill and Whitham (1955) and by Richards (1956). 
* It describes the trafic at a global level considering the speed, concentration and flows without taking into account the individual behavior af vehicles. 
* Speed, concentration and flow are the three components of the LWR model. 
* This models reproduces flow of traffic and congestion in specific conditions (homogeneous traffic), 
* going from one equilibrium state to another (see the fundamental diagramm of traffic, which gives flow according to concentration).
* 
* In this model, a road is divided into sections and we arbitrarily give to the middle section a lower speed and critical concentration.
* Tags: transport
*/


model TraficGroup

global {
	
	float monit <- sum(section collect each.current_concentration) update: sum(section collect each.current_concentration);
		
	float road_size <- 10 #km ; 						// Size of the road
	
	geometry shape <- rectangle (road_size, 200 #m) ; 	// The world is a rectangle with a length equals to the size of the road and a height of 200m
		
	float time_step <- 1.0 ; 							// Time step 
	int nb_sections <- 10 ; 							// Number of sections of the road
	float section_size <- road_size / nb_sections ; 	// Size of a section
	
	float car_size <- 4 #m ;									// Size of a car
	float nb_max_cars_on_section <- section_size / car_size ;	// Maximum number of cars on one section 
	
	   init{
	   		
	   		// Creation of the nb_sections sections which compose the road. 
	   		
		   loop i from: 0 to: (nb_sections - 1){
		   	  create section with: [shape:: line([{i * section_size , 100},{(i +1) * section_size , 100}])];
		   }
		   	
		   	// For each section, as we need to have information concerning the previous and the next one, we define which section is the previous and which is the next.
		   	// The previous section is the one which last point corresponds to the first point of the actual section. 
		   	// The next section is the one which first point corresponds to the last point of the actual section. 
		   	
	    	ask section{
	    		previous <- section first_with (last(each.shape.points) = first(self.shape.points)) ;
	    		next <- section first_with (first(each.shape.points) = last(self.shape.points)) ;
	    	}
	
	    	// For each section, we define a critical concentration (see the fundamental diagram), a maximum speed, a concentration and a flow (which are = 0 at initialization). 
	    	// A fundamental relation about trafic gives : flow = concentration * speed.
	    	
	    	ask section{
	    		critical_concentration <- 125.0 ; //Kc
				max_speed <- 50 #km/#h; // Vl
				current_concentration <- 0.0 ;
				current_flow <- current_concentration * max_speed ;
				max_flow <- critical_concentration * max_speed ; 
	    	}
	    		    	
	    	// The concentration and flow ot first section are initialized.
	    	
	    	ask section[0]{
	    		current_concentration <- nb_max_cars_on_section ;
				current_flow <- current_concentration * max_speed ;
	    	}
	    	
	    	// The middle section is supposed to have a different maximum speed and a different critical concentration so that we can observe congestion phenomenon.
	    	// This middle section is green.
	    	
	    	ask section[int(nb_sections/2)] {
	    			critical_concentration <- 10.0 ; 
	    			max_flow <- critical_concentration * max_speed ; 
				    max_speed <- 10 #km/#h; 
					color <- #green;
	    	}
    	}
    	
   // To update flow and concentration at each time step in each section, we use an offer and a demand function. 
   // These functions define the welcome capacity (offer) and the emission capacity (demand) of a section.
   
    reflex offer_function {
	     ask section {
			  if current_concentration <= critical_concentration {
			  	offer <- max_flow ;
			  }
			  else {
			  	offer <- max([0 , (- max_flow / critical_concentration) * current_concentration + 2 * max_flow ]);
			  }
		  
	  }
	}
	
	reflex demand_function {
		ask section{
			  if current_concentration <= critical_concentration {
			  		demand <- max([0 , ( max_flow / critical_concentration) * current_concentration]) ;
			  }
			  else {
			  		demand <- max_flow ;
			  }
		}
	}
	
	// The flow and concentration are then updated according to the offer and demand functions of the current section, but also of the next and previous ones.
	
	// During a time step, the flow of a section take the minimum value between its demand and the offer of the next section.
	// The flow of the last section is equal to its demand.	
	
	reflex update_flow{
		ask section  {
			float next_offer <-  (self.next != nil) ? (self.next).offer : self.demand;
			current_flow <- min([self.demand, next_offer]);
		}
	}
	
	
	// After a time step, the concentration is updated from the current concentration, according to the incoming and outgoing concentrations.
	// The first section concentration on ly takes into account the outgoing concentration of vehicles.
	
	reflex update_concentration{
		ask section   {
			float previous_flow <- (self.previous != nil) ? (self.previous).current_flow : 0.0;
			current_concentration <- current_concentration + time_step/section_size *(previous_flow - self.current_flow) ;
		}	
	}
	
	// When there is less than one car left on the road, the simulation stops.
	
	reflex stop_simulation when: sum(section collect each.current_concentration) < 1.0 {
		do pause;
	}
}

// A road is divided into sections. 
// Each section has a concentration, a flow, an offer and a demand, a critical concentration, a maximum speed and a maximum flow, 
// and a previous and a next section.

species section {
	float current_concentration  ;
	float current_flow  ;
	
	float offer ;
	float demand  ;
	
	
	float critical_concentration ; 
	float max_speed ; 
	float max_flow ; 

	section previous ;
	section next ;
	
	// The width of a section depends on its concentration.
	
	aspect shape_section {
		draw shape + (10 + 15 * ln (current_concentration + 1)) color: #gray + 128*current_concentration;		
	} 

}


experiment TraficGroup type: gui {

	// Users can chose the number of sections and the time step.

    parameter 'Number of sections' var: nb_sections category: "Section parameter";
    parameter 'Time step - DeltaT' var: time_step category: "Time parameter";
        
	output {
		
		// A monitor can give the number of cars on the road at every time step.
		
		//monitor "Sum Concentrations" value: sum(section collect each.current_concentration);
		 layout vertical([0::3472,horizontal([1::5000,2::5000])::6528]) tabs:true editors: false;
		
		// A display shows the road. Section width depend on their concentration.
		display TheRoad type:3d axes:false{
			camera 'default' location: {5044.1782,104.4563,2208.909} target: {5044.1782,104.4178,0.0};			
			species section aspect: shape_section ;
		}
		
		// The greeen time series correspond to the middle section (on which concentration and maximum speed are lower).
		// The red time series correspond to the section just before the middle one.
				
		display Concentrations  type: 2d {
			chart "Concentrations" type: series  {
				data 'Section 0' value: section[0].current_concentration color: #gray marker: false ;				
				data 'Section 1' value: section[1].current_concentration color: #gray marker: false;
				data 'Section 2' value: section[2].current_concentration color: #gray marker: false;
				data 'Section 3' value: section[3].current_concentration color: #gray marker: false;				
				data 'Section 4' value: section[int(nb_sections/2 - 1 ) ].current_concentration color: #red marker: false;
				data 'Section 5' value: section[int(nb_sections/2)].current_concentration color: #green marker: false;
				data 'Section 6' value: section[6].current_concentration color: #gray marker: false;				
				data 'Section 7' value: section[7].current_concentration color: #gray marker: false;
				data 'Section 8' value: section[8].current_concentration color: #gray marker: false;
				data 'Section 9' value: section[9].current_concentration color: #gray marker: false;
				}
			}
			
			display Flows  type: 2d {
			    chart "Flows" type: series  {
				data 'Section 0' value: section[0].current_flow color: #gray marker: false;				
				data 'Section 1' value: section[1].current_flow color: #gray marker: false;
				data 'Section 2' value: section[2].current_flow color: #gray marker: false;
				data 'Section 3' value: section[3].current_flow color: #gray marker: false;				
				data 'Section 4' value: section[int(nb_sections/2 - 1)].current_flow color: #red marker: false;
				data 'Section 5' value: section[int(nb_sections/2)].current_flow color: #green marker: false;
				data 'Section 6' value: section[6].current_flow color: #gray marker: false;				
				data 'Section 7' value: section[7].current_flow color: #gray marker: false;
				data 'Section 8' value: section[8].current_flow color: #gray marker: false;
				data 'Section 9' value: section[9].current_flow color: #gray marker: false;
				}
			}
	}
}
