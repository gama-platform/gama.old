model Bug

global {
	int nbClass <-5;
	int nbBugs <-50;
    init {
        create bug number: nbBugs{
        	attribute <- rnd(nbClass);
        	color <- hsb (attribute/nbClass,1.0,1.0);
        }
    }
} 


species bug skills: [moving] {
	 rgb color;
     int attribute;

	 reflex move{
	 	do move;
	 }
	 
	reflex update{
		attribute <- rnd(nbClass);
	} 
	//Display the bug in red
    aspect base {
        draw sphere(1) color: #red;
    }
    
    //Display the bug with a color that represent the value of the attribute
    aspect attribute{
      draw sphere(2) color: color;
      color <- hsb(attribute/nbClass,1.0,1.0);
    }
}




