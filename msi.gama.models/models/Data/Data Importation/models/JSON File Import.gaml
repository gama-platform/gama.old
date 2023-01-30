/**
* Name: JSON File Loading
* Author:  Arnaud Grignard
* Description: Initialize a grid from a JSON FIle. 
* Tags:  load_file, grid, json
*/

model json_loading   

global {
	file JsonFile <- json_file("../includes/cityIO.json");
    map<string, unknown> c <- JsonFile.contents;

	init { 
		list<map<string, int>> cells <- c["grid"];
        loop mm over: cells {                 
            cityMatrix cell <- cityMatrix grid_at {mm["x"],mm["y"]};
            cell.type <-int(mm["type"]);
        }
	}  
} 

grid cityMatrix width:16  height:16{
	rgb color <- #black;
	int type;
   	aspect base{	
    		draw shape color:rgb(type*30) border:#black ;
    }
}

experiment Display  type: gui {
	output {
		display cityMatrixView   type: 3d axes:false{	
			species cityMatrix aspect:base;			
		}
	}
}
