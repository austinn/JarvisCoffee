#include <MeetAndroid.h>

MeetAndroid meetAndroid;
int relay0 = 2; //start at 2 for tx/rx pins

void setup() {
  // put your setup code here, to run once:
  meetAndroid.registerFunction(coffee, 'c');
  meetAndroid.registerFunction(sendStateToAndroid, 's');

  pinMode(relay0, OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  meetAndroid.receive();
}

void coffee(byte flag, byte numOfValues) {
  if(meetAndroid.getInt() == 1) {
    digitalWrite(relay0, HIGH);
  } else {
    digitalWrite(relay0, LOW);
  }
}

void sendStateToAndroid(byte flag, byte numOfValues) {
  if(meetAndroid.getInt() == 1) {
    meetAndroid.send(digitalRead(relay0));
  }
}

