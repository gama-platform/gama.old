model model1   

global {
  int nb_cells <-100;	
  init { 
    create cells number: nb_cells { 
      location <- {rnd(100), rnd(100), rnd(100)};       
    } 
  }  
} 
  
species cells{                      
  aspect default {
    draw sphere(1) color:rgb('blue');   
  }
}

experiment Display  type: gui {
  output {
    display View1 type:opengl {
      species cells;
    }
  }
}