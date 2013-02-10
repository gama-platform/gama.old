model predator   

global {

	int width_and_height_of_environment parameter: 'Dimensions' min: 10 <- 20;
 	
	init { 		
		create myAgent number:300;  
	}  

} 
 
environment width: width_and_height_of_environment height: width_and_height_of_environment;
 
entities { 
	
	species myAgent skills: [moving] {
		int t_a<-rnd(100);
		reflex chay{
			set t_a<-rnd(100);
			do wander speed: 1;
			
			}
			aspect a{
//				draw circle(1); 
				draw circle(1) at_location (point({0,0,0}))  color:rgb("red");
				draw circle(0.5) at_location (point({0,0,5000}))  color:rgb("green");
//				draw circle(10) at: (point([{50,50,0}]));
			}
			aspect b{
				draw circle(2); 
			}
	}
}
experiment wet type: gui {
	output { 
		display Display refresh_every: 1  type:opengl {
			species myAgent aspect:a;	
		}
	}
}


experiment collision type:gui {
	output { 
		display Display refresh_every: 1   {
			species myAgent aspect:b;	
		}
	}
	
}