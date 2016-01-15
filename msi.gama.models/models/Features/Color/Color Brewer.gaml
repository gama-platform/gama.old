/**
 *  ColorBrewer 
 *  Author: Arnaud Grignard
 * Description: show how to use the color brewer: this feature allow to directly build Palettes of colors adapted to different needs
 */

model ColorBrewer


global skills:[graphic]{

//number of colors
int nb_class<-6 among:[1,3,4,5,6,7,8,9];

int agent_size <-10;

//the current sequential palette from the list of all available sequential Palettes
string sequentialPalette <- "Blues" among:["YlOrRd","Grays","PuBu","GnRdPu","BuPu","YlOrBr","Greens","BuGn","GnBu","PuRd","Purples","Blues","Oranges","PuBu","OrRd","Reds","YlGn","YlGnBu"];


//the current diverging palette from the list of all available diverging Palettes
string divergingPalette <- "Spectral" among:["PRGn","PuOr","RdGy","Spectral","RdYlGn","RdBu","RdYlBu","PiYG","BrBG"];


//the current qualitative palette from the list of all available qualitative Palettes
string qualitativePalette <- "Paired" among:["Accents","Paired","Set3","Set2","Set1","Dark2","Pastel2","Pastel1"];

//build the lists of colors from the palettes
list<rgb> SequentialColors<-list<rgb>(brewer_palette(sequentialPalette));
list<rgb> DivergingColors<-list<rgb>(brewer_palette(divergingPalette));
list<rgb> QualitativeColors<-list<rgb>(brewer_palette(qualitativePalette));


init {
	//define the location and the class index for each agent
	loop i from:0 to:nb_class-1{
	  create cell{
		location <-{agent_size/2 + i mod nb_class * agent_size, 0, 0};
		myClass <-i;
	  }	
	}
  }
}

//the graphics skill allows the agents to use some specific actions, in particular the brewer_color action
species cell skills:[graphic]{

	rgb color;
	int myClass;	
	
	//aspect while using sequential color
	aspect sequential {
		rgb myColor<-brewer_color("sequential",nb_class,myClass);
		draw square(agent_size) color:myColor at:location;
	}
	
	//aspect while using sequential color
	aspect diverging {
		rgb myColor<-self.brewer_color("diverging",nb_class,myClass);
		draw square(agent_size) color:myColor at:location;
	}
	
	//aspect while using qualitative color
	aspect qualitative {
		rgb myColor<-self.brewer_color("qualitative",nb_class,myClass);
		draw square(agent_size) color:myColor at:location;
	}	
}



//in this experiment, we do not use the cell agents, but we directlty draw the different palettes of colors
experiment BrewerPalette type: gui {
	parameter "Sequential Palettes" var:sequentialPalette category:"Brewer";
	parameter "Diverging Palettes" var:divergingPalette category:"Brewer";
	parameter "Qualitatives Palettes" var:qualitativePalette category:"Brewer";
	output {
		display View1 type:opengl draw_env:false{
			graphics "brewer"{
				//Sequential
				draw "Sequential" at:{-world.shape.width*0.2,0} color:°black bitmap:false;
				loop i from:0 to:length(SequentialColors)-1{
					draw square(agent_size) color:SequentialColors[i] at: {agent_size*(0.5 + i), 0, 0};
				}
				//Diverging
				loop i from:0 to:length(DivergingColors)-1{
					draw "Diverging" at:{-world.shape.width*0.2,1*agent_size} color:°black bitmap:false;
					draw square(agent_size) color:DivergingColors[i] at: {agent_size*(0.5 + i), 1*agent_size, 0};
				}
				//Qualitative		
				loop i from:0 to:length(QualitativeColors)-1{
					draw "Qualitative" at:{-world.shape.width*0.2,2*agent_size} color:°black bitmap:false;
					draw square(agent_size) color:QualitativeColors[i] at: {agent_size*(0.5 + i), 2*agent_size, 0};
				}
		    }
		}	
	}
}

//in this experiment, we display the cell agents with the  different aspects
experiment BrewerColoredAgent type: gui {
	parameter "Number of data classes" var:nb_class category:"Brewer";
	output {
		display View1 {
			species cell aspect:sequential position:{0,world.shape.height/6};
			species cell aspect:diverging position:{0,3*world.shape.height/6};
			species cell aspect:qualitative position:{0,5*world.shape.height/6};
		}	
	}
}