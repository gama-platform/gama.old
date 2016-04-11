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
	const center type: point <- { (gridsize / 2),  (gridsize / 2)} ;
	const types type: file <- (pgm_file('../images/environment75x75.pgm')) ;
	const ant_shape_empty type: string <- '../icons/ant.png' ;
	const ant_shape_full type: string <- '../icons/full_ant.png'  ;
	const C00CC00 type: rgb <- rgb('#00CC00') ;    
	const C009900 type: rgb <- rgb('#009900') ; 
	const C005500 type: rgb <- rgb('#005500') ; 
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
	const nest type: int <- 300 - int(self distance_to center) ;
	
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
	list<string> statesnames<-[""];
	list<string> categnames<-["empty","carry"];
	list<list<int>> nbantsbydist<-[[0]];
	list xytestvallist<-[[[1,1],[2,2],[3,3]],[[1,2],[2,1],[3,4]],[[1,3],[2,3],[0,1]],[[1,4],[2,5],[0,0]]];
	list<list<int>> xyval<-[[1,1],[2,1],[3,2]];
	
	//Reflex to update the charts, belonging to the experiment bloc as it will not be used by other experiment which don't have the charts
	reflex update_charts
	{
		nbants<-[];
		statesnames<-[];
		categnames<-["empty","carry"];
		nbantsbydist<-[];
		ant x<-one_of(world.ant);
		loop x over:list(world.ant)
		{
			if !(statesnames contains (x.state))
			{				
			add [(list(ant) count (each.state=x.state and !each.hasFood)),(list(ant) count (each.state=x.state and each.hasFood))] to: nbants;
			add (x.state) to:statesnames;				
			int d<-0;
			list<int> nl<-[];
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
		display ChartPie {
			chart "DataPie" type:pie
			{
				data "empty_ants" value:(list(ant) count (!each.hasFood)) color:°red;
				data "carry_food_ants" value:(list(ant) count (each.hasFood)) color:°green;
				
			}
			
			}
		display ChartPieList {
			chart "DataListPie" type:pie style:exploded
			{
				datalist ["empty","carry"] value:[(list(ant) count (!each.hasFood)),(list(ant) count (each.hasFood))] color:[°red,°green];				
			}
		}
		display ChartHisto {
			chart "DataBar" type:histogram
			{
				data "empty_ants" value:(list(ant) count (!each.hasFood)) color:°red;
				data "carry_food_ants" value:(list(ant) count (each.hasFood)) color:°green;				
			}
			
			}
		display ChartHistoList {
			chart "DataListBar" type:histogram style:"3d"
			{
				datalist ["empty","carry"] value:[(list(ant) count (!each.hasFood)),(list(ant) count (each.hasFood))] color:[°red,°green];				
			}
		}
		display ChartHistoListList {
			chart "DataListListBar" type:histogram
			{
				datalist categoriesnames:categnames  value:nbants legend:statesnames inverse_series_categories :false style:stack;
			}
		}
		display ChartSerie {
			chart "DataSeries" type:series
			{
				data "empty_ants" value:float((list(ant) count (!each.hasFood))) color:°red;
				data "carry_food_ants" value:float((list(ant) count (each.hasFood))) color:°green;
				
			}
			
			}

		display Dispscaleoption
		{
			chart "simpleserieslist" type:series  y_range:{-6.11, -4.11} x_tick_unit:0.5 y_tick_unit:0.05
			{
				data value:[-5,-6,-8] legend:"prems" color:°blue marker_shape:marker_square fill:false;
				data value:[-5.5,-4.11,-5.645] legend:"sec" color:°red marker_shape:marker_empty;
				data value:[-7,-6,-4.654] legend:"third" color:°green marker_shape:marker_hor_ellipse fill:false;
			}
		}
		display Cyclevalues
		{
			chart "simplecyclelist" type:xy x_tick_unit:0.5 y_tick_unit:0.5
			{
				data value:{-5+cycle,-4} legend:"prems" color:°blue marker_shape:marker_square fill:false;
				data value:{-5.5+cycle,-3.5} legend:"sec" color:°red marker_shape:marker_diamond;
				data value:{-7+cycle,-4.654} legend:"third" color:°green marker_shape:marker_hor_ellipse fill:false;
			}
		}
			display ChartSerieList {
			chart "DataListSeries" type:series
			{
				datalist ["food","empty"] value:[(list(ant) count (each.hasFood)),(list(ant) count (!each.hasFood))] color:[°purple,°black] style:area;				
			}
		}
		display ChartSeriesListList {
			chart "DataListListSeries" type:series
			{
				datalist categoriesnames:categnames  value:nbants legend:statesnames inverse_series_categories :false style:line;
			}
		}
		display ChartScatter {
			chart "DataScatter" type:scatter
			{
				data "empty_ants" value:((list(ant) where (!each.hasFood)) collect each.location) color:°red line_visible:false;
				data "carry_food_ants" value:((list(ant) where (each.hasFood)) collect each.location) color:°green line_visible:false;
				
			}
			
			}
		display ChartScatterList {
			chart "DataListScatter" type:scatter
			{
				datalist ["empty","carry"] value:[((list(ant) where (!each.hasFood))  collect each.location),((list(ant) where (each.hasFood))  collect each.location)] color:[°red,°green] line_visible:false;				
			}
		}
		display ChartScatterHistory {
			chart "DataListScatterHistory" type:scatter
			{
				datalist ["empty","carry"] value:[mean((list(ant) where (!each.hasFood)) collect each.location),mean((list(ant) where (each.hasFood)) collect each.location)]
					 color:[°red,°green] line_visible:true;				
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
		loop x over:list(world.ant)
		{
			if !(statesnames contains (x.state))
			{				
			add [(list(ant) count (each.state=x.state and !each.hasFood)),(list(ant) count (each.state=x.state and each.hasFood))] to: nbants;
			add (x.state) to:statesnames;				
			int d<-0;
			list<int> nl<-[];
			loop d from:0 to:9
				{
			add (list(ant) count (each.state=x.state and (((each distance_to center)>gridsize/20*d) and ((each distance_to center)<gridsize/20*(d+1))))) to: nl;
				}
			add nl to:nbantsbydist;
			}
//			add length((list(world.ant) collect (each.next_place distance_to each.location)) where (each=x)) to:nbants;
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

		display ChartSerieList {
			chart "DataListScatterHistory" type:scatter
			{
				datalist ["empty","carry"] value:[mean((list(ant) where (!each.hasFood)) collect each.location),mean((list(ant) where (each.hasFood)) collect each.location)]
					 color:[°red,°green] line_visible:true;				
			}
		}
	}
}


