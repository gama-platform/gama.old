/**
* Name: gam_ashapedthom_10K_comp
* Author: Arnaud Grignard and Tri Nguyen-Huu 
* Description: Forked from agrignard/r_adiohead_10kandOK forked from dataarts/radiohead) implemented on GAMA 1.8
* Tags: art
*/
model hail_to_the_gam_ashapedthom_10K_comp

global{
  bool m_ight_be_wrong <- true;
  bool pyramid <- false;
  float no_surprises <- 1.0;
  point the_bend <-{1,1,1}; 
  point everything_in_its_right_place <-{0,0,0};
  matrix<float,float> idioteque;
  init {
    idioteque <- matrix(csv_file("../includes/ok_computer.csv",""));
    everything_in_its_right_place<-{min(column_at (idioteque , 0)),min(column_at (idioteque , 1)),min(column_at (idioteque , 2))};
    shape<- box(max(column_at (idioteque , 0))-min(column_at (idioteque , 0)),max(column_at (idioteque , 1))-min(column_at (idioteque , 1)),max(column_at (idioteque , 2))-(min(column_at (idioteque , 2)))) 
    at_location {(max(column_at (idioteque , 0))-min(column_at (idioteque , 0)))/2,(max(column_at (idioteque , 1))-min(column_at (idioteque , 1)))/2,min(column_at (idioteque , 2))};
	loop i from: 1 to: idioteque.rows -1{
	  create inCloud{		
	    location<-{-everything_in_its_right_place.x+float(idioteque[0,i]),-everything_in_its_right_place.y+float(idioteque[1,i]),(float(idioteque[2,i]))-everything_in_its_right_place.z};	
		myxomatosis<-float(idioteque[3,i]);
      }	  
	}
  }
}

species inCloud skills:[moving]{
	float myxomatosis;
	aspect base{
		if(m_ight_be_wrong){
			if(pyramid){
			  draw pyramid(no_surprises*myxomatosis/100) color:rgb(myxomatosis*1.1,myxomatosis*1.6,200,50) rotate: cycle*myxomatosis/10::the_bend;
			}
		  draw square(no_surprises*myxomatosis/100) color:rgb(myxomatosis*1.1,myxomatosis*1.6,200,50) rotate: cycle*myxomatosis/10::the_bend;		
		}else{
		  draw square(no_surprises) color:rgb(myxomatosis*1.1,myxomatosis*1.6,200,50) rotate: cycle*myxomatosis/10::the_bend;		
		}  
	}
}

experiment OK_not_OK type:gui autorun:true{
	float minimum_cycle_duration <- 0.0225;
	output{
		display videotape type:opengl background:rgb(0,0,15)  draw_env:false synchronized:true fullscreen:true toolbar:false{
	    species inCloud aspect:base;
	    	event["p"] action: {pyramid<-!pyramid;};
			event["b"] action: {m_ight_be_wrong<-!m_ight_be_wrong;};
			event["x"] action: {the_bend<-{1,0,0};};
			event["y"] action: {the_bend<-{0,1,0};};
			event["z"] action: {the_bend<-{0,0,1};};
			event["t"] action: {the_bend<-{1,1,1};};
		}	
	}
}