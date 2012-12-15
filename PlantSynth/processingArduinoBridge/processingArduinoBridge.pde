import oscP5.*;
import netP5.*;
import processing.serial.*;

//serial
Serial myPort;  // Create object from Serial class
int val;      // Data received from the serial port

//communications  
OscP5 oscP5;
NetAddress myRemoteLocation;
OscP5 oscP5Client;

void setup() {    
  //graphical init
  size(400,400);
  frameRate(25);
  
  oscP5 = new OscP5(this,12000);  
  myRemoteLocation = new NetAddress("127.0.0.1",12001);
  
  println(Serial.list());
   String portName = Serial.list()[4]; //be carefull here, this changes between machines (welcome to hardware!) in my machine is 4, its typically 0
   myPort = new Serial(this, portName, 9600);  
  println(Serial.list()); //if we need to see the COM ports
}

int storedVal = -1;

void draw() {
  background(0);  
  if ( myPort.available() > 0) { 
    int value = myPort.read();
      sendMessage(value);     
  }
}

void sendMessage(int value) {
  OscMessage myMessage = new OscMessage("/value");
  myMessage.add(value);  
  oscP5.send(myMessage,myRemoteLocation);
  println(value); 
}
