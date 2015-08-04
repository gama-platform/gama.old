model tuto

global skills:[graphic] {
	int nb_people <- 3;

    agent_group_follower peoplefollower;
    string sequentialPalette <- "Blues" among:["YlOrRd","Grays","PuBu","GnRdPu","BuPu","YlOrBr","Greens","BuGn","GnBu","PuRd","Purples","Blues","Oranges","PuBu","OrRd","Reds","YlGn","YlGnBu"];
    string divergingPalette <- "Spectral" among:["PRGn","PuOr","RdGy","Spectral","RdYlGn","RdBu","RdYlBu","PiYG","BrBG"];

    list<rgb> SequentialColors<-list<rgb>(brewer_palette(divergingPalette));
	
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

	species agentfollower parent:agent_group_follower skills:[graphic]
	{
		aspect base {
          draw shape color: #red;
          int curColor <-0;
          loop geom over: allSimShape{
          	draw geom color:SequentialColors[curColor] at:{location.x,location.y,curColor*10};
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
		display city_display type:opengl{
			species people aspect: base ;
			species agentfollower aspect:base transparency:0.1;
		}
	}
}