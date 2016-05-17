/**
* Name: Ant Foraging (Charts examples)
* Author: 
* Description: Toy Model ant using the question of how ants search food and use pheromons to return to their 
* nest once they did find food. In this model, the charts are particularly used.
* Tags: gui, skill, chart, grid, diffusion
*/
model ants

global {
	//Number of ants
	int ants_number <- 100 min: 1 max: 2000 ;
	//Evaporation value per cycle for the pheromons
	float evaporation_per_cycle <- 5.0 min: 0.0 max: 240.0 ;
	//Diffusion rate for the pheromons
	float diffusion_rate <- 1.0 min: 0.0 max: 1.0 ;
	bool use_icons <- true ;
	bool display_state <- true;
	//Size of the grid
	int gridsize <- 75 ;
	//Center of the grid to put the location of the nest
	point center const: true <- { (gridsize / 2),  (gridsize / 2)} ;
	file types const: true <- (pgm_file('../images/environment75x75.pgm')) ;
	string ant_shape_empty const: true <- '../icons/ant.png' ;
	string ant_shape_full const: true <- '../icons/full_ant.png'  ;
	rgb C00CC00 const: true <- rgb('#00CC00') ;    
	rgb C009900 const: true <- rgb('#009900') ; 
	rgb C005500 const: true <- rgb('#005500') ; 
	int food_gathered <- 0 ;   
	geometry shape <- square(gridsize);
	init{  
		//Ant are placed randomly in the nest
		create ant number: ants_number with: [location::any_location_in (ant_grid(center))] ;
		
	}
	
	//Reflex to diffuse the road of pheromon on the grid
	reflex diffuse {
      diffuse var:road on:ant_grid proportion: diffusion_rate radius:2 propagation: gradient;
   }



}

//Grid to discretize space for the food and the nest
grid ant_grid width: gridsize height: gridsize neighbors: 8 use_regular_agents: false {
	bool multiagent <- true ;
	float road <- 0.0 max:240.0 update: (road<=evaporation_per_cycle) ? 0.0 : road-evaporation_per_cycle;
	int type <- int(types at {grid_x,grid_y}) ;
	bool isNestLocation <- (self distance_to center) < 4 ; 
	bool isFoodLocation <- type = 2 ; 
	rgb color <- isNestLocation ? °violet:((food > 0)? °blue : ((road < 0.001)? rgb ([100,100,100]) : ((road > 2)? °white : ((road > 0.5)? (C00CC00) : ((road > 0.2)? (C009900) : (C005500)))))) update: isNestLocation ? °violet:((food > 0)? °blue : ((road < 0.001)? rgb ([100,100,100]) : ((road > 2)? °white : ((road > 0.5)? (C00CC00) : ((road > 0.2)? (C009900) : (C005500)))))) ;
	int food <- isFoodLocation ? 5 : 0 ;
	int nest const: true <- 300 - int(self distance_to center) ;
	
}
//Species ant that will move and follow a final state machine
species ant skills: [moving] control: fsm {
	float speed <- 2.0 ;
	ant_grid place update: ant_grid (location ); 
	string im <- 'ant_shape_empty' ;
	bool hasFood <- false ;



	reflex diffuse_road when:hasFood=true{
      ant_grid(location).road <- ant_grid(location).road + 100.0;
   }
   //Action to pick food
	action pick {
		im <- ant_shape_full ;
		hasFood <- true ;
		place.food <- place.food - 1 ;
	}
	//Action to drop food
	action drop {
		food_gathered <- food_gathered + 1 ;
		hasFood <- false ;
		heading <- heading - 180 ;
	}
	//Action to chose the best place according to the possible food in the neighbour cells
	action choose_best_place type: ant_grid {
		list<ant_grid> list_places <- place.neighbors ;
		if (list_places count (each.food > 0)) > 0  {
			return (list_places first_with (each.food > 0)) ;
		} else {
				int min_nest  <-  (list_places min_of (each.nest)) ;
				list_places <- list_places sort ((each.nest = min_nest) ? each.road :  0.0) ;
				return last(list_places) ;
			}
	}
	
	//Initial state of the ant : wander until it finds food or find a road to follow
	state wandering initial: true {
		do wander amplitude:120 ;
		transition to: carryingFood when: place.food > 0 {
			do pick ;
		}
		transition to: followingRoad when: place.road > 0.05 ;
	}
	//State to carry food to the nest once the food is found
	state carryingFood {
		do goto target: center ;
		transition to: wandering when: place.isNestLocation { 
			do drop ;
		}
	}
	//State to follow a road 
	state followingRoad {
		location <- (self choose_best_place []) as point ;
		transition to: carryingFood when: place.food > 0 {
			do pick ;
		}
		transition to: wandering when: (place.road < 0.05) ;
	}
	aspect text {
		if use_icons {
			draw  hasFood ? file(ant_shape_full) : file(ant_shape_empty) rotate: heading at: location size: {7,5} ;
		} else {
			draw circle(1.0) empty: !hasFood color: rgb ('orange') ;
		}
		if display_state {
			draw state at: location + {-3,1.5} color: °white size: 0.8 ;
		}
	}
	aspect default {
		draw circle(1.0) empty: !hasFood color: #orange ; 
	}
}
experiment Ant type: gui {
	//Parameters to play with  in the gui
	parameter 'Number of ants:' var: ants_number category: 'Model' ;
	parameter 'Evaporation of the signal (unit/cycle):' var: evaporation_per_cycle category: 'Model' ;
	parameter 'Rate of diffusion of the signal (%/cycle):' var: diffusion_rate category: 'Model' ;
	parameter 'Use icons for the agents:' var: use_icons category: 'Display' ;
	parameter 'Display state of agents:' var: display_state category: 'Display' ;

	list<list<int>> nbants<-[[0]];
	list<string> statesnames<-["wandering"];
	list<string> categnames<-["empty","carry"];
	list<list<int>> nbantsbydist<-[[0]];
	list xytestvallist<-[[[1,1],[2,2],[3,3]],[[1,2],[2,1],[3,4]],[[1,3],[2,3],[0,1]],[[1,4],[2,5],[0,0]]];
	list<list<int>> xyval<-[[1,1],[2,1],[3,2]];

	//Reflex to update the charts, belonging to the experiment bloc as it will not be used by other experiment which don't have the charts
	reflex update_charts
	{
		nbants<-list<list<int>>([]);
		statesnames<-list<string>([]);
		categnames<-["empty","carry"];
		nbantsbydist<-list<list<int>>([]);
		ant x<-one_of(world.ant);
		loop x over:list(world.ant)
		{
			if !(statesnames contains (x.state))
			{				
			add [(list(ant) count (each.state=x.state and !each.hasFood)),(list(ant) count (each.state=x.state and each.hasFood))] to: nbants;
			add (x.state) to:statesnames;				
			int d<-0;
			list<int> nl<-list<int>([]);
			loop d from:0 to:9
				{
			add (list(ant) count (each.state=x.state and (((each distance_to center)>gridsize/20*d) and ((each distance_to center)<gridsize/20*(d+1))))) to: nl;
				}
			add nl to:nbantsbydist;
			}
//			add length((list(world.ant) collect (each.next_place distance_to each.location)) where (each=x)) to:nbants;
		}
		//write("nbants"+nbants);
		//write("nbantsbydist"+nbantsbydist);
		//write("states"+statesnames);		
	}
	
	//The different displays
	output {
		display Ants type: opengl {
			grid ant_grid ;
			species ant aspect: text ;
		}
		display ProportionCarryFood {
			chart "Proportions carrying: Pie"  size: {0.5,0.5} position: {0, 0} type:pie
			{
				data "empty_ants" value:(list(ant) count (!each.hasFood)) color:°red;
				data "carry_food_ants" value:(list(ant) count (each.hasFood)) color:°green;
				
			}
			
			chart "Proportion carrying: Bar history"  size: {0.5,0.5} position: {0.5, 0} type:histogram
			
			{
				data "empty_ants" value:(list(ant) count (!each.hasFood)) color:°red;
				data "carry_food_ants" value:(list(ant) count (each.hasFood)) color:°green;				
			}
			
			chart "Proportion: serie"   size: {1.0,0.5} position: {0, 0.5} type:series 
			series_label_position: legend
			style:stack
			{
				datalist ["empty","carry"] accumulate_values:true 
				value:[(list(ant) count (!each.hasFood)),(list(ant) count (each.hasFood))] 
				color:[°red,°green];				
			}
		}
		display ProportionByState {
			chart "DataListListBar" type:histogram 
			time_series: categnames 
			series_label_position: legend
			{
				datalist value:nbants legend:statesnames style:stack;
			}
			
		}

		display PositionByCarry {
			chart "Position by carry state (data)" type:scatter
			{
				data "empty_ants" value:((list(ant) where (!each.hasFood)) collect each.location) color:°red line_visible:false;
				data "carry_food_ants" value:((list(ant) where (each.hasFood)) collect each.location) color:°green line_visible:false;
				
			}
			
			}
			// Idem with datalist:
//		display PositionByState {
//			chart "Position by state (datalist)" type:scatter
//			{
//				datalist ["empty","carry"] value:[((list(ant) where (!each.hasFood))  collect each.location),((list(ant) where (each.hasFood))  collect each.location)] color:[°red,°green] line_visible:false;				
//			}
//		}
		display CentroidPosition {
			chart "Centroide and size by Carry state" type:scatter
			{
				datalist ["carry","empty"] value:[mean((list(ant) where (each.hasFood)) collect each.location),
					mean((list(ant) where (!each.hasFood)) collect each.location)
				]
				marker_size: [length(list(ant) where (each.hasFood))/20,length(list(ant) where (!each.hasFood))/20]
					 color:[°red,°green] 
					 fill:false
					 line_visible:true;				
			}
		}	
		display DistributionPosition {
			chart "Distribution of the X positions" type:histogram
			{
				datalist (distribution_of(list(ant) collect each.location.x,10) at "legend") 
					value:(distribution_of(list(ant) collect each.location.x,10) at "values");
			}
		}	
		}
	
	}
	
//Experiment with only two display : the grid and the ants, and a chart
experiment AntOneDisp type: gui {
	parameter 'Number of ants:' var: ants_number category: 'Model' ;
	parameter 'Evaporation of the signal unit/cycle):' var: evaporation_per_cycle category: 'Model' ;
	parameter 'Rate of diffusion of the signal (%/cycle):' var: diffusion_rate category: 'Model' ;
	parameter 'Use icons for the agents:' var: use_icons category: 'Display' ;
	parameter 'Display state of agents:' var: display_state category: 'Display' ;

	list<list<int>> nbants<-[[0]];
	list<string> statesnames<-[""];
	list<string> categnames<-["empty","carry"];
	list<list<int>> nbantsbydist<-[[0]];
	list xytestvallist<-[[[1,1],[2,2],[3,3]],[[1,2],[2,1],[3,4]],[[1,3],[2,3],[0,1]],[[1,4],[2,5],[0,0]]];
	list<list<int>> xyval<-[[1,1],[2,1],[3,2]];

	reflex update_charts
	{
		ant x<-one_of(world.ant);
		nbants<-list<list<int>>([]);
		statesnames<-list<string>([]);
		loop x over:list(world.ant)
		{
			if !(statesnames contains (x.state))
			{				
			add [(list(ant) count (each.state=x.state and !each.hasFood)),(list(ant) count (each.state=x.state and each.hasFood))] to: nbants;
			add (x.state) to:statesnames;				
			int d<-0;
			list<int> nl<-list<int>([]);
			loop d from:0 to:9
				{
			add (list(ant) count (each.state=x.state and (((each distance_to center)>gridsize/20*d) and ((each distance_to center)<gridsize/20*(d+1))))) to: nl;
				}
			add nl to:nbantsbydist;
			}
		}
		write("nbants"+nbants);
		write("nbantsbydist"+nbantsbydist);
		write("states"+statesnames);		
	}
	output {
		display Ants type: opengl {
			grid ant_grid ;
			species ant aspect: text ;
		}

		display ChartScatter {
			chart "DataScatter" type:scatter
			{
				data "empty_ants" value:((list(ant) where (!each.hasFood)) collect each.location) color:°red line_visible:false;
				data "carry_food_ants" value:((list(ant) where (each.hasFood)) collect each.location) color:°green line_visible:false;
				
			}
			
			}
	}
}





