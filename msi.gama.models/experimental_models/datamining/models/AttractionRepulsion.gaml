/**
 *  AttractionRepulsion
 *  Author: Javier Gil-Quijano
 *  Description: Particles attraction repulsion for datamining; See 
 * "Gil-Quijano & Piron, 2007, Formation automatique de groupes d’agents sociaux par techniques d’apprentissage non supervisé. In Actes de EGC’07 – Atelier Fouille de Données et Algorithmes Biomimétiques"
 */

model AttractionRepulsion
global {
	int width_of_environment parameter: 'Dimensions' init:200 ; 
	int height_of_environment parameter: 'Dimensions' init:200  ; 
	int number_agents parameter: 'Agents' init:200  ; 
	int max_visibility parameter: 'max visibility' init:50 ; 
	int min_visibility parameter: 'min visibility' init:10 ; 
	float G parameter: 'G' init:10 ; 
	
	list colors <- [rgb ([255,0,0]), rgb([0,255,0]), rgb([0,0,255])];
	list shapes <- [rectangle({3,3}), circle(3), triangle(3)];
	float mean_visibility <- 0;
	float mean_neighborns <- 0;
//	list shapes_names <- ["rectangle", "circle", "triangle"];
//	list colors_names <- ["red", "green", "blue"];
	
	init{
		create data number: number_agents;
	}
	reflex test{
		
		set mean_visibility <- mean (list (data) collect (each.visibility) );
		set mean_neighborns <- mean (list (data) collect (length (each neighbours_at each.visibility)) );
	}
} 

environment width: width_of_environment height: height_of_environment torus: true; 

entities {
	species data skills: [physical] {  
		rgb color;
		geometry shape;
		list _data;
		int visibility <- min_visibility;
		bool goingUp <-true;
		point displacement <- {0, 0};
		point velocity <- {0,0};
		float mass <- 1.0;
		init
		{
			let color_index type: int <- rnd(length(colors) - 1);
			let shape_index type:int <- rnd(length(shapes) - 1);
			set shape <- shapes at shape_index;
			set color <- colors at color_index;
			set _data <- [color_index, shape_index];
			set location <- {rnd(width_of_environment),rnd(height_of_environment)};
		}
 		
		reflex go {
			let neighs type: list of : data <- self neighbours_at visibility;
 			set displacement <- {0, 0};
 			if(length(neighs) = 0){
				set goingUp <- true;
 			}
 			else{
				loop n over : neighs{
					let pt type: point <- self computeForce [ag1::self, ag2::n];				
					set displacement <- {displacement.x + pt.x, displacement.y + pt.y}; 
				}
//				set velocity <- velocity + displacement;
				set location <- self compute_new_location [_displacement::displacement, current_location::location];
//				set location <- location + velocity;
 				
 			}
		}
		reflex update_visibility{
			if(goingUp){
				if(visibility >= max_visibility){
					set goingUp <- false;
				}
				else{
					set visibility <- visibility +1;
				}
			}
			else{
				if(visibility <= min_visibility){
					set goingUp <- true;
				}
				else{
					set visibility <- visibility -1;
					
				}
			}
			
		}
		action compute_new_location{
			arg _displacement type:point;
			arg current_location type:point;
			let temp_Location type : point <- current_location + _displacement;
			let x <- (temp_Location.x > width_of_environment)? (temp_Location.x - width_of_environment):(temp_Location.x);
			set x <- (temp_Location.x < 0)? ( width_of_environment + temp_Location.x):(temp_Location.x);
			let y <- (temp_Location.y > height_of_environment)? (temp_Location.y - height_of_environment):(temp_Location.y);
			set y <- (temp_Location.y < 0)? ( height_of_environment + temp_Location.y):(temp_Location.y);
			return {x, y};			
		}
		action computeForce{
			arg ag1 type: data;
			arg ag2 type: data;
			
			let _data_distance type:float <- self distance [arg1::ag1._data, arg2::ag2._data];
			let _distance type:float <- ag1 distance_to ag2;
			let divisor <- _distance * _distance;
			set divisor <- (divisor < 0.0001)?0.0001:divisor;
			let _force type:float <- G * ag1.mass * ag2.mass * (_data_distance - _distance)/(divisor);
			let _unitaryVector type:point <- self unitaryVectorBetweenPositions[arg1::ag1.location, arg2::ag2.location];
			return {_unitaryVector.x * _force , _unitaryVector.y * _force};
		}
		action normOfVector{
			arg arg1 type: point;
			let result type:float <- sqrt( float (arg1.x * arg1.x) + float (arg1.y * arg1.y )  );
			return (result = 0)?1.0:result;
		}
		action oppositeVector{
			arg arg1 type: point;
			return {-1 * arg1.x, -1 * arg1.y};
		}
		action unitaryVectorBetweenPositions{
			arg arg1 type: point;
			arg arg2 type: point;
			
			let result type:point <- {arg2.x - arg1.x, arg2.y - arg1.y};
			let norm type:float <- self normOfVector [arg1::result];
			return {result.x / norm, result.y/norm};
		}
		action distance{
			arg arg1 type:list;
			arg arg2 type:list;
			
			let _distance type:float <- 0.0;
			loop i from:0 to :length(arg1)-1{
//				let d type:float <- float (arg1 at i) - float (arg2 at i);
//				set d <- d * d;
				let d <- (arg1 at i = arg2 at i)?0:1;
				set _distance <- _distance + d;
			}
			return sqrt(_distance);
		}		

		aspect default {
			draw shape: geometry color: color;
		}
		
	}

	


}

output {
	display Circle refresh_every: 1 {
		species data;
	}
	monitor average_visibility value: mean_visibility;
	monitor average_neighbours value: mean_neighborns;
	
}/* Insert your model definition here */

