model tuto

global {
	int nb_people <- 3;

    agent_group_follower peoplefollower;
	
	init {

		create people number: nb_people {
			speed <-rnd(100);

		}  
		create agentfollower 
		{
		  do analyse_cluster species_to_analyse:people;
		  peoplefollower<-self;
		}		
	}
}

	species agentfollower parent:agent_group_follower
	{
		aspect base {
          draw shape color: #red;
          //write "allSimPoly" + allSimPoly;
          
          loop i from:0 to:length(allSimPoly)-1{
          	write "allSimPoly[i]" + allSimPoly[i];
          	loop j from:0 to:length(list(allSimPoly[i]))-1{
          		write "allSimPoly[i][j]" + list(allSimPoly[i])[j];
          	}
          	}
		  }
        
	}

	species people skills: [moving]{
		rgb color <- rgb('yellow') ;

		reflex move {
         do wander;
		}
		aspect base {
			draw circle(1) color: color;
		}
	} 
	

experiment exp type: gui {
	output {
		display city_display{
			species people aspect: base ;
			species agentfollower aspect:base transparency:0.1;
		}
	}
}