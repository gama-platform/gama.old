/**
* Name: Ant Foraging (Charts examples)
* Author: Philippe Caillou
* Description: How ants search food and use pheromons to return to their nest once they find it.
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
	//Size of the grid
	int gridsize <- 75 ;
	//Center of the grid to put the location of the nest
	point center const: true <- { (gridsize / 2),  (gridsize / 2)} ;
	file types const: true <- (pgm_file('../images/environment75x75.pgm')) ;
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
	rgb color <- isNestLocation ? #violet:((food > 0)? #blue : ((road < 0.001)? rgb ([100,100,100]) : ((road > 2)? #white : ((road > 0.5)? (#grey) : ((road > 0.2)? (#lightgrey) : (#darkgray)))))) update: isNestLocation ? #violet:((food > 0)? #blue : ((road < 0.001)? rgb ([100,100,100]) : ((road > 2)? #white : ((road > 0.5)? (#grey) : ((road > 0.2)? (#lightgray) : (#darkgray)))))) ;
	int food <- isFoodLocation ? 5 : 0 ;
	int nest const: true <- 300 - int(self distance_to center) ;
	
}
//Species ant that will move and follow a final state machine
species ant skills: [moving] control: fsm {
	float speed <- 2.0 ;
	ant_grid place update: ant_grid (location ); 
	bool hasFood <- false ;



	reflex diffuse_road when:hasFood=true{
      ant_grid(location).road <- ant_grid(location).road + 100.0;
   }
   //Action to pick food
	action pick {
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
		do wander amplitude:120.0 ;
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
		location <- (choose_best_place()) as point ;
		transition to: carryingFood when: place.food > 0 {
			do pick ;
		}
		transition to: wandering when: (place.road < 0.05) ;
	}

	aspect default {
		draw circle(1.0) wireframe: !hasFood color: #orange ; 
	}
}
experiment "Experiment" type: gui {
	//Parameters to play with  in the gui
	parameter 'Number of ants:' var: ants_number category: 'Model' ;
	parameter 'Evaporation of the signal (unit/cycle):' var: evaporation_per_cycle category: 'Model' ;
	parameter 'Rate of diffusion of the signal (%/cycle):' var: diffusion_rate category: 'Model' ;

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
		loop x over:(world.ant)
		{
			if !(statesnames contains (x.state))
			{				
			add [(ant count (each.state=x.state and !each.hasFood)),(ant count (each.state=x.state and each.hasFood))] to: nbants;
			add (x.state) to:statesnames;				
			list<int> nl<-list<int>([]);
			loop d from:0 to:9
				{
			add (ant count (each.state=x.state and (((each distance_to center)>gridsize/20*d) and ((each distance_to center)<gridsize/20*(d+1))))) to: nl;
				}
			add nl to:nbantsbydist;
			}
		}
	
	}
	//The different displays
	output {
	    layout horizontal([vertical([0::6721,2::3279])::5000,vertical([1::5000,horizontal([3::5000,4::5000])::5000])::5000]) tabs:true toolbars:true;
		
		display Ants type: 2d antialias:false{
			grid ant_grid ;
			species ant  ;
		}
		display ProportionCarryFood  type: 2d {
			chart "Proportions carrying: Pie"  size: {0.5,0.5} position: {0, 0} type:pie
			{
				data "empty_ants" value:(ant count (!each.hasFood)) color:#red;
				data "carry_food_ants" value:(ant count (each.hasFood)) color:#green;
				
			}
			
			chart "Proportions carrying: Radar"  size: {0.5,0.5} position: {0.5, 0} type:radar
			axes:#white

			{
				data "empty" value:(ant count (!each.hasFood)) 
				accumulate_values:true
				color:#red;				
				data "carry" value:(ant count (each.hasFood)) 
				accumulate_values:true
				color:#blue;				
			}
			
			chart "Proportion: serie"   size: {1.0,0.5} position: {0, 0.5} type:series 
			series_label_position: legend
			style:stack
			{
				datalist ["empty","carry"] accumulate_values:true 
				value:[(ant count (!each.hasFood)),(ant count (each.hasFood))] 
				color:[#red,#green];				
			}
		}

		display CentroidPosition  type: 2d {
			chart "Positions and History of Centroide and size by Carry state" type:scatter
			{
				datalist ["avg-carry","avg-empty"] value:[mean((ant where (each.hasFood)) collect each.location),
					mean((ant where (!each.hasFood)) collect each.location)
				]
				marker_size: [(ant count (each.hasFood))/20,(ant count (!each.hasFood))/20]
					 color:[#red,#green] 
					 fill:false
					 line_visible:true;				
				data "empty_ants" value:((ant where (!each.hasFood)) collect each.location) color:#red 
				accumulate_values:false
				line_visible:false;
				data "carry_food_ants" value:((ant where (each.hasFood)) collect each.location) 
				accumulate_values:false
				color:#green line_visible:false;

			}
		}	
		display Distribution2dPosition  type: 2d {
			chart "Distribution of the X positions"   size: {0.65,0.3} position: {0.05, 0} type:histogram
			
			{
				datalist (distribution_of(ant collect each.location.x,10,0,100) at "legend") 
					value:(distribution_of(ant collect each.location.x,10,0,100) at "values");
			}
			chart "Distribution of the Y positions"   size: {0.3,0.7} position: {0.7, 0.28} type:histogram
			reverse_axes:true
			
			{
				datalist reverse(distribution_of(ant collect each.location.x,10,0,100) at "legend") 
					value:reverse(distribution_of(ant collect each.location.x,10,0,100) at "values");
			}

			chart "Distribution2d of the XvsY positions- heatmap"   size: {0.7,0.7} position: {0, 0.3} type:heatmap
			series_label_position:none
			{
				data  "XYdistrib"
					value:(distribution2d_of(ant collect each.location.x,ant collect each.location.y,10,0,100,10,0,100) at "values")
					color:[#red]; 
			}
		}
		
		display DistributionPosition  type: 2d {
			chart "Distribution of the X positions"   size: {0.92,0.3} position: {0, 0} type:histogram
			
			{
				datalist (distribution_of(ant collect each.location.x,10,0,100) at "legend") 
					value:(distribution_of(ant collect each.location.x,10,0,100) at "values");
			}
			chart "Distribution of the X positions- heatmap"   size: {1.0,0.7} position: {0, 0.3} type:heatmap
			x_serie_labels: (distribution_of(ant collect each.location.x,10,0,100) at "legend")
			y_range:50
			{
				data  "Xdistrib"
					value:(distribution_of(ant collect each.location.x,10,0,100) at "values")
					color:[#red];
			}
		}	
		}
	}	




