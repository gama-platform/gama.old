model Tuto3D
//Model 1 of the 3D tutorial   

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
    draw sphere(1) color:#blue;   
  }
}

experiment Tuto3D  type: gui {
  parameter "Initial number of cells: " var: nb_cells min: 1 max: 1000 category: "Cells" ;	
  output {
    display View1 type:opengl {
      species cells;
    }
  }
}