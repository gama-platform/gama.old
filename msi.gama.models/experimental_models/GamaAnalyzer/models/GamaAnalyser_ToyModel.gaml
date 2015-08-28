model tuto

global skills:[graphic] {
	int nb_people <- 30;

    agentfollower peoplefollower;
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
		list graphvalues<-[] update:(self at_cycle ("multi_averagehistory","speed"));
		list graphdistrib<-[[0,0,0,0,0,0,0,0]] update:(self at_cycle ("multi_distribhistory","speed"));
		list graphdistriblegend<-["1","2","3"] update:(self distrib_legend ("speed"));
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
          	  draw geom color:colorList[curColor] at:{location.x,location.y,curColor*10};
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
          	draw geom color:colorList[curColor] at:{location.x,location.y,curColor*10};
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
		display chartserie {
			chart name:"speedhistory" type:series {
				datalist value: (peoplefollower.graphvalues) color:peoplefollower.colorList;		
			}
		}
		display chartdistrib refresh:cycle>2{
			chart name:"speedhistory"  type: histogram style: stack {
				datalist categoriesnames:peoplefollower.graphdistriblegend value: (peoplefollower.graphdistrib) style:stack color:peoplefollower.colorList;		
			}
		}
	}
}

experiment expSimGlobalNone type: gui {
	output {
		display view type:opengl{
			species people aspect: base ;
			species agentfollower aspect:simglobal transparency:0.1;
		}
		display chartserie {
			chart name:"speedhistory" type:series {
				datalist value: (peoplefollower.graphvalues) color:peoplefollower.colorList;
			}
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