model tuto

global skills:[graphic] {
	int nb_people <- 30;

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
		  dbscane <-0.2;
		  do analyse_cluster species_to_analyse:people;
		  peoplefollower<-self;
		}		
	}
}

	species agentfollower parent:agent_group_follower skills:[graphic]
	{
		aspect base {
		  display_mode <-"global";
		  clustering_mode <-"none";
          draw shape color: #red;
		}
		
		aspect simglobal{
			display_mode <-"simglobal";
		    clustering_mode <-"none";
			draw shape color: #red;
            int curColor <-0;
            loop geom over: allSimShape{
          	  draw geom color:SequentialColors[curColor] at:{location.x,location.y,curColor*10};
          	  curColor <- curColor+1;
            } 
		}
		
		aspect simglobalflat{
			display_mode <-"simglobal";
		    clustering_mode <-"none";
			draw shape color: #red;
            int curColor <-0;
            loop geom over: allSimShape{
          	  draw geom color:SequentialColors[curColor] at:{location.x+curColor*world.shape.width,location.y,curColor*10};
          	  curColor <- curColor+1;
            } 
		}
		
		aspect cluster {
		  display_mode <-"global";
		  clustering_mode <-"dbscan";
          draw shape color: #red;
		}
		
		aspect clusterSimGlobal {
		  display_mode <-"simglobal";
		  clustering_mode <-"dbscan";
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
	

experiment expGlobalNone type: gui {
	output {
		display view1 type:opengl{
			species people aspect: base ;
			species agentfollower aspect:base transparency:0.1;
		}
	}
}

experiment expSimGlobalNone type: gui {
	output {
		display view type:opengl{
			species people aspect: base ;
			species agentfollower aspect:simglobal transparency:0.1;
		}
	}
}

experiment expSimGlobalNoneFlat type: gui {
	output {
		display view type:opengl{
			species people aspect: base ;
			species agentfollower aspect:simglobalflat transparency:0.1;
		}
	}
}

experiment expCluster type: gui {
	output {
		display view type:opengl{
			species people aspect: base ;
			species agentfollower aspect:cluster transparency:0.1;
		}
	}
}

experiment expClusterSimGlobal type: gui {
	output {
		display view type:opengl{
			species people aspect: base ;
			species agentfollower aspect:clusterSimGlobal transparency:0.1;
		}
	}
}