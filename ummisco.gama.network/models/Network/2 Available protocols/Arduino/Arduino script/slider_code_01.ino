/*
Robotale Potentiometer Sample Sketch
*/

#include <SPI.h>        

#include <Ethernet.h>

#include <EthernetUdp.h>


const int potIn = A0;

int RawValue= 0;
float Voltage = 0;
float Resistance = 0;

void setup(){  
  pinMode(potIn, INPUT);
//  Serial.begin(74880);
  Serial.begin(9600);  
}

void loop(){  
  RawValue = analogRead(potIn); 
  Voltage = (RawValue * 5.0 )/ 1024.0; // scale the ADC

  Serial.println(Voltage,3); //3 digits after decimal point
  delay(500);   // 1/2 sec so your display doesnt't scroll too fast
}
