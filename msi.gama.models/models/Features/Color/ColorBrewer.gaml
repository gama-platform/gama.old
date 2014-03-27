/**
 *  ColorBrewer 
 *  Author: Arnaud Grignard
 */

model ColorBrewer


global skills:[graphic]{

int nb_class<-6 among:[1,3,4,5,6,7,8,9];
int agent_size <-10;
//geometry shape <- cube(nb_class) ;

string sequentialPalette <- "Blues" among:["YlOrRd","Grays","PuBu","GnRdPu","BuPu","YlOrBr","Greens","BuGn","GnBu","PuRd","Purples","Blues","Oranges","PuBu","OrRd","Reds","YlGn","YlGnBu"];
string divergingPalette <- "Spectral" among:["PRGn","PuOr","RdGy","Spectral","RdYlGn","RdBu","RdYlBu","PiYG","BrBG"];
string qualitativePalette <- "Paired" among:["Accents","Paired","Set3","Set2","Set1","Dark2","Pastel2","Pastel1"];

list<rgb> SequentialColors<-list<rgb>(brewer_palette(sequentialPalette));
list<rgb> DivergingColors<-list<rgb>(brewer_palette(divergingPalette));
list<rgb> QualitativeColors<-list<rgb>(brewer_palette(qualitativePalette));

init {
	loop i from:0 to:nb_class-1{
	  create cells{
		location <-{0.5 + i mod nb_class * agent_size, 0, 0};
		myClass <-i;
	  }	
	}
  }
}


species cells skills:[graphic]{

	rgb color;
	int myClass;	
	aspect sequential {
		rgb myColor<-self.brewer_color("sequential",nb_class,myClass);
		draw cube(agent_size) color:myColor border:myColor at:location;
	}
	
	aspect diverging {
		rgb myColor<-self.brewer_color("diverging",nb_class,myClass);
		draw cube(agent_size) color:myColor border:myColor at:location;
	}
	
	aspect qualitative {
		rgb myColor<-self.brewer_color("qualitative",nb_class,myClass);
		draw cube(agent_size) color:myColor border:myColor at:location;
	}	
}




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

experiment BrewerColoredAgent type: gui {
	parameter "Number of data classes" var:nb_class category:"Brewer";
	output {
		display View1 type:opengl draw_env:false{
			species cells aspect:sequential position:{0,world.shape.height/4};
			species cells aspect:diverging position:{0,2*world.shape.height/4};
			species cells aspect:qualitative position:{0,3*world.shape.height/4};
		}	
	}
}