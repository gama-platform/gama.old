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
          int curColor <-0;
          loop geom over: allSimShape{
          	draw geom color:hsb(curColor/10,0.5,0.5);
          	curColor <- curColor+1;
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