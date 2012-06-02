/**
 *  modelTC
 *  Author: marilleau
 *  Description: 
 */

model modelTC
global
{
	const shape_file_TCLines type: string init: '../includes/Reseau_TC.shp' category: 'GIS' ;
	const shape_file_TCBusStop type: string init: '../includes/sommet_TC.shp'  category: 'GIS' ;
	var transportation_graph type: graph;
	init{
		create species: transportationLines from: shape_file_TCLines with: [length::read('FT_MINUTES')]{
			
		} 
		let weights_map type: map value: (list (transportationLines)) as_map [each:: each.length];
		set transportation_graph value: as_edge_graph(list(transportationLines))  with_weights weights_map with_optimizer_type "Djikstra";
		
	
		create species: busStop from: shape_file_TCBusStop {
			
		}
		
		let firstStop <-one_of(busStop);
			
		create species: bus number: 1
		{
			set stops <- list(firstStop);
			set stops <- collate([stops, list(one_of(busStop))]);
			set location value: /*any_location_in*/ (firstStop.shape).location;
			set the_target <- location;
		}
		create species: people number: 50
		{
			set the_target <- firstStop.shape.location;
			set leader <- nil;
		}
	
	}
	
}	
entities
{
	species transportationLines {
		var length type: float init: 0;		
		var color type: rgb init: rgb('blue') ;
		aspect base {
			draw shape: geometry color: color ;
		}
	}
	
		
	species busStop
	{
		var color type: rgb init: rgb('red') ;
		
		aspect base {
			draw shape: circle  color: color size: 100 ;
		}
		
	}	
	
	species people skills: [moving]
	{
		var color type: rgb init: rgb('yellow') ;
		var the_target  type: point init: nil ;
		var leader type: bus init: nil;
		var isWaitingBus type: bool init: false;
		
		int heading <- rnd(359) ;  //update: leader.heading;
		reflex move_bus  when:  leader != nil{ 
			set location <- leader.location;
		} 
		

		reflex move when: the_target!=location and leader = nil and isWaitingBus = false
		 { 
			let path_followed type: path <- self goto  [target::the_target]; // on::transportation_graph];
		}
		
		reflex take_bus when: the_target=location
		{
			set isWaitingBus <- true;
		}
		
		aspect base {
			draw shape: circle  color: color size: 100 ;
		}
	}
	
	species bus skills: [moving]
	{
		var color type: rgb init: rgb('green') ;
		var stops type: list of: busStop init: nil;
		var the_target  type: point init: nil ;
		var objectiveId type: int init: 0;
		
		
		reflex changeTarget when: location=the_target
		{
			let parent <-self;
			//delivery
			let deliver type: list of: people <- (people as list) ; // where ((each.leader) = parent) ;
			ask deliver
			{
				set leader <- nil;
			}
			
			
			//pick up
			let candidates type: list of: people <- (people as list)  where  (each.location  overlaps( parent.shape) and each.isWaitingBus = true);
			//((each).location = parent.location) ;
			
			ask candidates {
					
				set leader <- parent;
				set isWaitingBus <- false;	
			}
			
			set objectiveId <- (objectiveId + 1 ) mod length(stops);
			set the_target <- (stops at(objectiveId)).shape.location;
		}
		
		
		reflex moveToDestination when: location!=the_target
		{
			let path_followed type: path <- self  goto  [target::the_target, on::transportation_graph, speed::1, return_path:: true];
			let segments type: list of: geometry <- path_followed.segments;
			loop line over: segments {
				let dist type: float <- line.perimeter;
				let ag type: transportationLines <- path_followed agent_from_geometry line; 
			}		
			
		}
		
		
		aspect base {
			draw shape: square color: color size: 200;
		}
		
	}
	
}
environment  bounds: shape_file_TCLines; 

output {
display city_display refresh_every: 10 {
		species transportationLines aspect: base ;
		species busStop aspect: base;
		species bus aspect:base;
		species people aspect:base;
	}
}
/* Insert your model definition here */

