/***
* Name: Mirrorsimple
* Author: documentation GAMA
* Description: This model simply illustrates mirror species used to provides 
*   a different display of the agents
* Tags: mirror, display
***/

model Mirrorsimple

global {
	
	int neigh_distance <- 10;
	
  	init{
    	create A number:100;    
  	}
}

species A skills:[moving] {
    reflex update{
        do wander;
    }
    aspect base{
        draw circle(1) color: #white border: #black;
    }
}
species B mirrors: A {
    point location <- target.location update: {target.location.x,target.location.y,target.location.z+5};    
   	list<B> neigh <- [] update: B at_distance neigh_distance;
   	
    aspect base {
        draw sphere(2) color: #blue;
        loop n over: neigh {
        	draw line(location, n.location) color: #black;
        }
    }
}

experiment mirroExp type: gui {
    output {
        display superposedView type: 3d{ 
          species A aspect: base;
          species B aspect: base transparency:0.5;
        }
    }
}
