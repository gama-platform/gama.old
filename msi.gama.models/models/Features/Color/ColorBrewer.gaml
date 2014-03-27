/**
 *  ColorBrewer 
 *  Author: Arnaud Grignard
 */

model ColorBrewer


global skills:[graphic]{

int nb_class<-6 among:[1,3,4,5,6,7,8,9];

geometry shape <- cube(nb_class) ;

//Sequentail
list<rgb> Blues<-list<rgb>(brewer_palette("Blues"));
list<rgb> YlGnBu<-list<rgb>(brewer_palette("YlGnBu"));
list<rgb> YlOrRd<-list<rgb>(brewer_palette("YlOrRd"));

//Diverging
list<rgb> BrBG<-list<rgb>(brewer_palette("BrBG"));
list<rgb> RdBu<-list<rgb>(brewer_palette("RdBu"));
list<rgb> RdYlBu<-list<rgb>(brewer_palette("RdYlBu"));


//Qualitative
list<rgb> Paired<-list<rgb>(brewer_palette("Paired"));
list<rgb> Set1<-list<rgb>(brewer_palette("Set1"));
list<rgb> Set3<-list<rgb>(brewer_palette("Set3"));



init {
	loop i from:0 to:nb_class-1{
	  create cells{
		location <-{0.5 + i mod nb_class, 0, 0};
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
		draw cube(1) color:myColor border:myColor at:location;
	}
	
	aspect diverging {
		rgb myColor<-self.brewer_color("diverging",nb_class,myClass);
		draw cube(1) color:myColor border:myColor at:location;
	}
	
	aspect qualitative {
		rgb myColor<-self.brewer_color("qualitative",nb_class,myClass);
		draw cube(1) color:myColor border:myColor at:location;
	}	
}


experiment BrewerColoredCells type: gui {
	parameter "Number of data classes" var:nb_class category:"Brewer";
	output {
		display View1 type:opengl draw_env:false{
			species cells aspect:sequential position:{0,world.shape.height/4};
			species cells aspect:diverging position:{0,2*world.shape.height/4};
			species cells aspect:qualitative position:{0,3*world.shape.height/4};
		}	
	}
}

experiment BrewerPalette type: gui {
	parameter "Number of data classes" var:nb_class category:"Brewer";
	output {
		display View1 type:opengl draw_env:false{
			graphics "brewer"{
				//Sequentia
				loop i from:0 to:length(Blues)-1{
					draw square(1) color:Blues[i] at: {0.5 + i, 0, 0};
				}
				loop i from:0 to:length(YlGnBu)-1{
					draw square(1) color:YlGnBu[i] at: {0.5 + i, 1, 0};
				}		
				loop i from:0 to:length(YlOrRd)-1{
					draw square(1) color:YlOrRd[i] at: {0.5 + i, 2, 0};
				}
				//Diverging
				loop i from:0 to:length(BrBG)-1{
					draw square(1) color:BrBG[i] at: {0.5 + i, 4, 0};
				}
				loop i from:0 to:length(RdBu)-1{
					draw square(1) color:RdBu[i] at: {0.5 + i, 5, 0};
				}		
				loop i from:0 to:length(RdYlBu)-1{
					draw square(1) color:RdYlBu[i] at: {0.5 + i, 6, 0};
				}
				//Qualitative
				loop i from:0 to:length(Paired)-1{
					draw square(1) color:Paired[i] at: {0.5 + i, 8, 0};
				}
				loop i from:0 to:length(Set1)-1{
					draw square(1) color:Set1[i] at: {0.5 + i, 9, 0};
				}		
				loop i from:0 to:length(Set3)-1{
					draw square(1) color:Set3[i] at: {0.5 + i, 10, 0};
				}
		    }
		}	
	}
}