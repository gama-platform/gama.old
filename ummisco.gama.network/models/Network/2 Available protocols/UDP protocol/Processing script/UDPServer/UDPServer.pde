import hypermedia.net.*;

int PORT = 9876;
String HOST_IP = "localhost"; 
UDP udp; 

void setup() {
  udp = new UDP( this, PORT );
  udp.log( true ); 
  udp.listen( true );
  noLoop();
  
}

//process events
void draw() {;}

// void receive( byte[] data ) { 			// <-- default handler
void receive( byte[] data, String ip, int port ) {	// <-- extended handler

  data = subset(data, 0, data.length-2);
  String message = new String( data );
  
  println( "receive: \""+message+"\" from "+ip+" on port "+port );
}
