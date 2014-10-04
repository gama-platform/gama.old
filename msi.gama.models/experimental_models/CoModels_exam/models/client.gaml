/**
 *  new
 *  Author: HUYNH
 *  Description: 
 */

model myClient 

global{
//	string ss<-get_URL_content("http://www.ietf.org/rfc/rfc4180.txt");
//	string ss<-get_URL_content("file:///D:/Test.java");
	init{
		create Client number:2{			
			port<-3000;
		}
//		create Client number:2{			
//			port<-3001;
//		}
	}
}

species Client skills:[moving, socket]{
	
	reflex c{  
//		write port;
		do listen_server;
		write msg;
		do send_to_server msg:""+self+" at cycle "+cycle;
	}
	reflex do_move{
		do wander;
	}
	aspect d{
		draw circle(1);
	}
}
experiment new type: gui {
	/** Insert here the definition of the input and output of the model */
	output {
		display "AA"{
			species Client aspect:d;
		}
	}
}
