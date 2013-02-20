model Bug

global {
    init {
        create bug number: 100;
    }
} 

environment{
}

entities {
    species bug skills: [moving] {
    
    	 reflex move{
    	 	do move;
    	 }
        aspect base {
            draw sphere(1) color: rgb('red');
        }
    }
}

output {
     display bug_display type:opengl{
        species bug aspect: base;
    }
}

