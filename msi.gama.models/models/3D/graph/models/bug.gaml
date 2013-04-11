model Bug

global {
	int nbClass <-5;
	int nbBugs <-50;
    init {
        create bug number: nbBugs{
        	set attribute <- rnd(nbClass);
        	set color <- color hsb_to_rgb ([attribute/nbClass,1.0,1.0]);
        	//set location <- {(attribute/nbClass)*100 + rnd(100/nbClass),(attribute/nbClass)*100+rnd(100/nbClass)}; 
        }
    }
} 

environment{
}

entities {
    species bug skills: [moving] {
    	 rgb color;
         int attribute;
    
    	 reflex move{
    	 	do move;
    	 }
    	 
    	reflex update{
    		//set attribute <- rnd(nbClass);
    	} 
        aspect base {
            draw sphere(2) color: rgb('red');
        }
        
        aspect attribute{
          draw sphere(2) color: color;
          set color <- color hsb_to_rgb ([attribute/nbClass,1.0,1.0]);
        }
    }
}

output {
     display bug_display type:opengl ambiant_light:0.2{
        species bug aspect: base;
        species bug aspect: attribute position: {125,0,0};
    }
}

