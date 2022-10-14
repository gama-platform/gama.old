/**
* Name: Basic model
* Author: Arnaud Grignard
* Description: First part of the tutorial : Tuto3D
* Tags:
*/

model Tuto3D

global {
  int nb_cells <- 100;	
  init { 
    create cell number: nb_cells { 
      location <- {rnd(100), rnd(100), rnd(100)};       
    } 
  }  
} 
  
species cell {                      
  aspect default {
    draw sphere(1) color: #blue;   
  }
}

experiment Tuto3D  type: gui {
  parameter "Initial number of cells: " var: nb_cells min: 1 max: 1000 category: "Cells" ;	
  output {
    display View1 type: 3d {
      species cell;
    }
  }
}