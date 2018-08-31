import hypermedia.net.*;

int PORT = 9877;
String HOST_IP = "localhost"; //IP Address of the PC in which this App is running
UDP udp; //Create UDP object for recieving

void setup(){
  udp= new UDP(this);  
  udp.log(true);
  udp.listen(true);
  noLoop();
}

//process events
void draw() {;}

void receive(byte[] data){
  println("Processing received an unexpected message");   
}

void mouseMoved() {
    String s = ""+(mouseX)+";"+(mouseY);

    println("sending " + s + " to " + HOST_IP + " port:" + PORT);
    udp.send(s,HOST_IP,PORT);
}
