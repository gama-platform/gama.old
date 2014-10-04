/**
 *  new
 *  Author: HUYNH
 *  Description: 
 */
 
model myServer

global{   
//	string ss<-get_URL_content("http://www.ietf.org/rfc/rfc4180.txt"); 
//	string ss<-get_URL_content("file:///D:/Test.java");
	init{
//		write ss;
		create Server {			
			port<-3000;
		}
//		create Server {			
//			port<-3001;
//		}
	}
}

species Server skills:[moving, socket]{
	init{		
		do open_socket;
	}
	
	reflex s{
//		write port;
		do listen_client;
		write msg;
		do send_to_client msg:"server "+self;
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
			species Server aspect:d;
		}
	}
}
