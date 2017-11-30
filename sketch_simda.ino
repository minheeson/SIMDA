#include <SoftwareSerial.h>
#define pinRX 2          //rx pin 번호
#define pinTX 3          //tx pin 번호 
SoftwareSerial BTSerial(pinRX, pinTX);

byte buffer[1024];
int bufferPosition;
const int analogInPin = A0;
int outputValue = 0;
 
void setup() 
{
  Serial.begin(9600);
  Serial.println("Hello!");
  
  BTSerial.begin(9600);
}
  
void loop()
{
  //ppg sensor 
  int sensorValue = analogRead(analogInPin);
  Serial.println(sensorValue);
  BTSerial.write(sensorValue/10);
  delay(100);

  
 }
