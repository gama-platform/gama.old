/**
 *  Author: Arnaud Grignard
 */

model autoorganizedmap

global {
	
    float delta <-0.1;
    file dataset <- csv_file ("../includes/etudsNotesSGalea.csv",";");
  	/*int deltaGender <-0;
  	int deltaWeight <-0;*/
  
  init{
  	write "dataset: " + dataset;
  	create people from:dataset;// header:true with:[age::get("age"),weight::get("H1G1SE11 - Semestre 1")];
	/*create people number:100{
		age <-rnd(255);
	    //genderValue <-flip(0.5) ? 1.0 : 0.0;
	    weight <-rnd(100);
	}*/
  }
  
  /*action findClosestPeople(){
  	int deltaAge <-0;
  	int deltaGender <-0;
  	int deltaWeight <-0;
  	
  	ask grid whih_min_of(distance(each,))
  	
  }*/
  
  float distance (virtualAgent curCell, people curP) {
 	
  	float d <- (abs(curCell.age - curP.age) + abs(curCell.weight - curP.weight))/2 ;
  	return d;

  }
  
  action movetoClosest{
  	
  }
  
  action updateVirtualAgent{
  	
  }
}

species people {
	float age;
	//float genderValue;
	float weight;
	
	reflex findClosest{
		ask virtualAgent with_min_of(world.distance(each,self)){
			list neighbours <- self neighbours_at 1;
			ask neighbours{
				age <- age + (myself.age - age)*delta;
			    //genderValue <- genderValue + (myself.genderValue - genderValue)*delta;
			    weight <- weight + (myself.weight - weight)*delta;
			}
			
			age <- age + (myself.age - age)*delta;
			//genderValue <- genderValue + (myself.genderValue - genderValue)*delta;
			weight <- weight + (myself.weight - weight)*delta;
		}
	}
	aspect base{
		draw circle(5*weight/100) color:rgb(age,age,age);		
	}
	aspect text{
	  //draw "----->" + age + "-" + genderValue + "-" + weight bitmap:false color:°black;	
	}
}


grid virtualAgent width:10 height:10{
	float age <-rnd(255);
	//float genderValue <-flip(0.5) ? 1.0 : 0.0;
	float  weight <-rnd(100);
	
	aspect base{
		draw circle(5*weight/100) color:rgb(age,age,age);		
	}
	aspect text{
	  //draw "" + age + "-" + genderValue + "-" + weight bitmap:false color:°black;	
	}
}

experiment Display type: gui {

	output {
		display View1  type:opengl{
			species virtualAgent aspect:base; 
			//species virtualAgent aspect:text; 
			species people aspect:base position:{world.shape.width*1.1,0,0};
			//species people aspect:text position:{world.shape.width*1.1,0,0};
		}
	}

}

