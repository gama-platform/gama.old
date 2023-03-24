/**
* Name: gam_ashapedthom_10K_comp
* Author: Arnaud Grignard and Tri Nguyen-Huu 
* Description: Press "r","a","d","i","o","h","e","a","d"
* Forked from agrignard/r_adiohead_10kandOK forked from dataarts/radiohead - 2019
* Tags: art
*/
model hail_to_pablo_the_king_of_the_amnesiac_kid_in_a_computer_shaped_thom

global{
	
  matrix<float> idioteque <-  matrix<float>(csv_file("../includes/ok_computer.csv",""));
  float no_surprises <- 1.0;
  bool it_might_be_wrong <- true;
  bool pyramid_song <- false;
  point the_bend <-{1,1,1}; 
  point everything_in_its_right_place <-{0,0,0};
  geometry shape<- box(max(column_at (idioteque , 0))-min(column_at (idioteque , 0)),max(column_at (idioteque , 1))-min(column_at (idioteque , 1)),max(column_at (idioteque , 2))-(min(column_at (idioteque , 2)))) 
    at_location {(max(column_at (idioteque , 0))-min(column_at (idioteque , 0)))/2,(max(column_at (idioteque , 1))-min(column_at (idioteque , 1)))/2,min(column_at (idioteque , 2))};
  
 
  init {
    everything_in_its_right_place<-{min(column_at (idioteque , 0)),min(column_at (idioteque , 1)),min(column_at (idioteque , 2))};
	loop i from: 1 to: idioteque.rows -1{
	  create paranoid_android with:[the_numbers::idioteque[3,i]]{		
	    location<-{-everything_in_its_right_place.x+idioteque[0,i],-everything_in_its_right_place.y+idioteque[1,i],(idioteque[2,i])-everything_in_its_right_place.z};	
      }	  
	}
  }
}

species paranoid_android skills:[moving]{
	float the_numbers;
	aspect house_of_cards{
		if(it_might_be_wrong){
			if(pyramid_song){
			  draw pyramid(no_surprises*the_numbers/100) color:rgb(the_numbers*1.1,the_numbers*1.6,200,50) rotate: cycle*the_numbers/10::the_bend;
			}else{
			  draw square(no_surprises*the_numbers/100) color:rgb(the_numbers*1.1,the_numbers*1.6,200,50) rotate: cycle*the_numbers/10::the_bend;	
			}
		  		
		}else{
		  draw square(no_surprises) color:rgb(the_numbers*1.1,the_numbers*1.6,200,50) rotate: cycle*the_numbers/10::the_bend;		
		}  
	}
}

experiment OK_Computer type:gui autorun:true{
	output synchronized:true{
		display videotape type:3d background:rgb(0,0,15) axes:false fullscreen:true toolbar:false{
	    species paranoid_android aspect:house_of_cards;
	    	event "r"  {pyramid_song<-!pyramid_song;}
	    	event "a"  {the_bend<-{1,0,0};}
			event "d"  {the_bend<-{1,0,0};}
			event "i"  {it_might_be_wrong<-!it_might_be_wrong;}
			event "o"  {the_bend<-{0,1,0};}
			event "h"  {the_bend<-{0,0,1};}
			event "e"  {the_bend<-{1,1,1};}
			event "a"  {the_bend<-{1,1,1};}
			event "d"  {the_bend<-{1,1,1};}
		}	
	}
}