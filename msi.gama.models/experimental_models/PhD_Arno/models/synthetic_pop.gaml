/**
 *  Author: Arnaud Grignard
 */

model autoorganizedmap

global {
  int grid_width <-10;
  int grid_height <-10;
  geometry focusOnShape <- rectangle (world.shape.width*2.5,world.shape.height) at_location {world.shape.width*2.5, world.shape.height/2};
  float delta <-0.1;
  init{
  	//create people from:"../includes/etudsNotesSGalea.csv" header:true with:[age::get("age"),weight::get("H1G1SE11 - Semestre 1")];
	create people number:1000{
		age <-float(rnd(255));
	    //genderValue <-flip(0.5) ? 1.0 : 0.0;
	    weight <-float(rnd(100));
	    shape <- circle(5*weight/100);
	}
  }
  
  reflex updatePop{
		/*ask people{
			age <-age+rnd(5);
			weight <-weight + (flip(0.8) ? rnd(5) : -rnd(5));
			if(age > 255){
				do die;
			}
		}*/
  }
}

species people {
	float age;
	float weight;
	virtualAgent myVirtualClosest;
	geometry shape update: circle(5*weight/100) ;
		
	reflex findClosest{
		myVirtualClosest <-virtualAgent with_min_of(distance(each));
		ask myVirtualClosest{
			list neighbours <- self neighbours_at 1;
			ask neighbours{
				age <- age + (myself.age - age)*delta;
			    weight <- weight + (myself.weight - weight)*delta;
			}
			
			age <- age + (myself.age - age)*delta;
			weight <- weight + (myself.weight - weight)*delta;
		}
	}
	float distance (virtualAgent curCell) {
  	  return (abs(curCell.age - age) + abs(curCell.weight - weight))/2;
    }
  
	aspect base{
		draw shape color:rgb(age,age,age);		
	}
	
	aspect onVirtualGrid{
		if(cycle>1){
		  draw (shape scaled_by 1) color:rgb(age,age,age) at:any_location_in(myVirtualClosest.shape);
		}		
	}

}


grid virtualAgent width:grid_width height:grid_height{
	float age <-float(rnd(255));
	float  weight <-float(rnd(100));
	
	aspect grid{
		draw square(world.shape.width/grid_width) color:rgb(age,age,age);
	}
	
	aspect age{
		draw square(world.shape.width/grid_width) color:rgb(age,age,age);
	}
	
	aspect weight{
		draw square(world.shape.width/grid_width) color:rgb(weight,weight,weight);
	}
	
	aspect base{
		draw circle(5*weight/100) color:rgb(age,age,age);		
	}
	aspect text{
	  //draw "" + age + "-" + genderValue + "-" + weight bitmap:false color:°black;	
	}
}

experiment Display type: gui {

	output {
		display View1  type:opengl focus:focusOnShape{
			species people aspect:base ;
			species virtualAgent aspect:base position:{world.shape.width*1.1,0,0};		 
			species people aspect:onVirtualGrid position:{world.shape.width*2*1.1,0,0};			
			species virtualAgent aspect:age position:{world.shape.width*3*1.1,0,0}; 
			species virtualAgent aspect:weight position:{world.shape.width*4*1.1,0,0};
			graphics "info"{
			draw "initial model" color:°black at:{0,world.shape.height*1.1,0}bitmap:false;
			draw "virtual" color:°black at:{world.shape.width*1.1,world.shape.height*1.1,0} bitmap:false;		 
			draw "clustered agent" color:°black at:{world.shape.width*2*1.1,world.shape.height*1.1,0} bitmap:false;			
			draw "Kohonen age" color:°black at:{world.shape.width*3*1.1,world.shape.height*1.1,0} bitmap:false; 
			draw "Kohonen weight" color:°black at:{world.shape.width*4*1.1,world.shape.height*1.1,0} bitmap:false;				
			}
		}
	}

}

