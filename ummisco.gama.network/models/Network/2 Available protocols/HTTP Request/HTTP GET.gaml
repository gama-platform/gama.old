/**
* Name: HTTPGET
* Based on the internal empty template. 
* Author: benoitgaudou
* Tags: 
*/


model HTTPGET

global {
	int port <- 443;       // for HTPP : 80 http, for HTTPS : 443 
	string url <- "https://openlibrary.org";	
	
	init {
		create NetworkingAgent number: 1 {
		   do connect to: url protocol: "http" port: port raw: true;
		}		
	} 

}

species NetworkingAgent skills:[network] {
	
	reflex send when:  cycle = 0 {
		write "sending message ";		
		do send to: "/search/authors.json?q=j%20k%20rowling" contents: ["GET"];
	//	do send to: "/search/authors.json?q=j%20k%20rowling" contents: ["GET", ["Content-Type"::"application/json"]];		
	}

	reflex get_message {
		loop while:has_more_message()
		{
			//read a message
			message mess <- fetch_message();
			//display the message 
			write name + " fecth this message: " + mess.contents;	
			write sample(map(mess.contents)["CODE"]);
			write sample(map(mess.contents)["BODY"]);
			write sample(map(mess.contents)["HEADERS"]);			
		}
		
	}

	
	aspect default {
		draw circle(1) color: #red border: #black; 
	}
}

experiment Server_testdd type: gui {
	output {
		display d {
			species NetworkingAgent;	
		}
	}
}
