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
    		set attribute <- rnd(nbClass);
    	} 
        aspect base {
            draw sphere(2) color: rgb('red');
        }
    }
}

output {
    
    display Model type:opengl ambient_light:100{
        species bug aspect: base;
    }
}

