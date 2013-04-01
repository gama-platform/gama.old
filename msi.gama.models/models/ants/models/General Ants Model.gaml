model ants 
global    {
	float evaporation_rate <- 0.10 min: 0.0 max: 1.0 parameter: 'Rate of evaporation of the signal (%/cycle):' category: 'Signals';
	float diffusion_rate <- 0.5 min: 0.0 max: 1.0 parameter: 'Rate of diffusion of the signal (%/cycle):' category: 'Signals';
	int gridsize <- 100 min: 30 parameter: 'Width and Height of the grid:' category: 'Environment and Population';
	int ants_number <- 50 min: 1 parameter: 'Number of ants:' category: 'Environment and Population';   
	int grid_frequency <- 1 min: 1 max: 100 parameter: 'Grid updates itself every:' category: 'Environment and Population'; 
	int number_of_food_places <- 5 min: 1 parameter: 'Number of food depots:' category: 'Environment and Population';  
	float grid_transparency <- 1.0;   
	const ant_shape_empty type: string <- '../icons/ant.png';   
	const ant_shape_full type: string <- '../icons/full_ant.png'; 
	const center type: point <- { round ( gridsize / 2 ) , round ( gridsize / 2 ) };      
	var food_gathered type: int <- 0;   
	var food_placed type: int <- 0;
	const background type: rgb <- rgb( #999999 );  
	const food_color type: rgb <- rgb ( #312200 );        
	const nest_color type: rgb <- rgb ( #000000 ); 
	init {         
		loop times: number_of_food_places { 
			point loc <- { rnd ( gridsize - 10 ) + 5 , rnd ( gridsize - 10 ) + 5 };  
			list<ant_grid> food_places <- ( ant_grid where ( ( each distance_to loc ) < 5 ) );
			ask food_places {
				if food = 0 {          
					food <- 5 ;
					food_placed <- food_placed + 5 ; 
 					color <- food_color ; 
				}       
			}
		}
		create ant number: ants_number with: (location: center);  
	}

} 
environment width: gridsize height: gridsize { 
	grid ant_grid width: gridsize height: gridsize neighbours: 8 torus: false frequency: grid_frequency { 
		const neighbours type: list of: ant_grid <- self neighbours_at 1;
		const is_nest type: bool <- ( topology ( ant_grid ) distance_between [self , center ] ) < 4;
		int food <- 0;
		
		aspect default {
			draw shape color: is_nest ? nest_color: ( ( food > 0 ) ? food_color: (( float ( road ) < 0.001 ) ? background: rgb ( #009900 ) + int ( road * 5 )) );
		}	
	}
}
entities {
	
	species ant skills: [moving]  control: fsm { 
		float speed <- 1.0; 
		bool has_food <- false;
		signal road value: has_food ? 240: 0 decay: evaporation_rate proportion: diffusion_rate environment: ant_grid;

 
		action pick (int amount) {	
			has_food <- true ;
			ant_grid place <- ant_grid ( location );
			place . food <- place . food - amount ;    
		}
		
		action drop { 
			food_gathered <- food_gathered + 1 ;
			has_food <- false ;   
			heading <- heading - 180 ;
		}
		
		point choose_best_place {  
			container list_places <- (ant_grid ( location )).neighbours;
			if ( list_places count ( each . food > 0 ) ) > 0 {  
				return point ( list_places first_with ( each . food > 0 ) ) ;
			} else {
				list_places <- ( list_places where ( ( each . road > 0 ) and ( ( each distance_to center ) > ( self distance_to center ) ) ) ) sort_by ( each .road ) ;
				return point ( last ( list_places ) ) ;
			}  
		}
		reflex drop when: has_food and  ( ant_grid ( location ) ) . is_nest {
			do drop();
		}
		reflex pick when: ! has_food and ( ant_grid ( location ) ) . food > 0 {
			do pick(1);
		}

		state wandering initial: true {
			do wander(amplitude: 90) ; 
			float pr <- ( ant_grid ( location ) ) . road;  
			transition to: carryingFood when: has_food; 
			transition to: followingRoad when: ( pr > 0.05 ) and ( pr < 4 );
		}  
		 
		state carryingFood { 
			do goto(target:center);  
			transition to: wandering when: ! has_food;
		}
 
		state followingRoad {
			point next_place <- choose_best_place();
			float pr <-  ( ant_grid ( location ) ) . road;    
			location <- next_place ;
			transition to: carryingFood when: has_food ;
			transition to: wandering when: ( pr < 0.05 ) or ( next_place = nil );
		} 
		aspect info {
			draw circle(1) empty: !has_food color: rgb('red');
			draw line([location, destination]) color: rgb ( 'white' );
			draw circle(4) empty: true color: rgb ( 'white' );
			draw string ( self as int ) color: rgb ( 'white' ) size: 1;
			draw state color: rgb ( 'white' ) size: 1 at: my location + { 1 ,1 };
		}
		aspect icon {
			draw file(ant_shape_empty) size: 5 rotate: my heading + 1;
		}
		aspect default { 
			draw square(1) empty: ! has_food color: rgb('white') rotate: my heading;
		}  
	}    
} 
experiment Complete type: gui {
	parameter name: 'Number:' var: ants_number init: 100 unit: 'ants' category: 'Environment and Population'; 
	parameter name: 'Grid dimension:' var: gridsize init: 100 unit: '(number of rows and columns)' category: 'Environment and Population'; 
	parameter name: 'Number of food depots:' var: number_of_food_places init: 5 min: 1 category: 'Environment and Population';
	
	// Experimentator
	int a <- 1000;
	
	init {
		write "Experimentator agent running " + self;  
	}
	
	//reflex to {
	//	write "Experimentator at cycle " + cycle;   
	//}
	
	
	

	
	output { 
		display Ants background: rgb ( 'white' ) refresh_every: 1{  
			image name: 'Background' file: '../images/soil.jpg' position: { 0.05 ,0.05 } size: { 0.9 , 0.9 };
			agents ant_grid2 transparency: 0.5 position: { 0.05 , 0.05 } size: { 0.9 , 0.9 } value: (ant_grid as list) where ( ( each . food > 0 ) or ( each . road > 0 ) or ( each . is_nest ) );
			species ant position: { 0.05 , 0.05 } size: { 0.9 , 0.9 } aspect: icon;
			text food value: 'Food foraged: ' + string ( ( (food_placed = 0 ? 0:  food_gathered /food_placed ) * 100 ) with_precision 2 ) + '%' position: { 0.05 , 0.03 }
			color: rgb ( 'black' ) size: { 1 , 0.02 };
			text agents value: 'Carrying ants: ' + string ( int ( ant as list count (each . has_food ) ) + int ( ant as list count ( each . state =    
			'followingRoad' ) ) ) position: { 0.5 , 0.03 } color: rgb ( 'black' ) size                
			: { 1 , 0.02 };  
		}  
	} 
}   

experiment Batch type: batch repeat: 2 keep_seed: true until: (food_gathered = food_placed ) or ( time > 400 ) {
	parameter name: 'Size of the grid:' var: gridsize init: 75 unit:'width and height';
	parameter name: 'Number:' var: ants_number init: 200 unit: 'ants';
	parameter name: 'Evaporation:' var: evaporation_rate among: [ 0.1 , 0.2 , 0.5 , 0.8 , 1.0 ] unit: 'rate every cycle (1.0 means 100%)';
	parameter name: 'Diffusion:' var: diffusion_rate min: 0.1 max: 1.0 unit: 'rate every cycle (1.0 means 100%)' step: 0.3;
	method exhaustive maximize: food_gathered;
}
experiment Genetic type: batch repeat: 2 keep_seed: true until: (food_gathered = food_placed ) or ( time > 400 ) {
	parameter name: 'Size of the grid:' var: gridsize init: 75 unit:
	'(width and height)';
	parameter name: 'Number:' var: ants_number init: 200 unit: 'ants';
	parameter name: 'Evaporation:' var: evaporation_rate among: [ 0.1 , 0.2 ,
	0.5 , 0.8 , 1.0 ] unit: 'rate every cycle (1.0 means 100%)';
	parameter name: 'Diffusion:' var: diffusion_rate min: 0.1 max: 1.0 unit:
	'rate every cycle (1.0 means 100%)' step: 0.3;
	method genetic maximize: food_gathered pop_dim: 5 crossover_prob: 0.7
	mutation_prob: 0.1 nb_prelim_gen: 1 max_gen: 20; 
}
experiment name: 'Show Quadtree' type: gui {
	output {
		monitor name: 'Food gathered' value: food_gathered;
		display QuadTree {
			quadtree qt;	
		}
		display Ants background: rgb ( 'white' ) refresh_every: 1 {
			grid ant_grid lines: rgb ( 'black' );
			species ant aspect: default;
		}
	}
}