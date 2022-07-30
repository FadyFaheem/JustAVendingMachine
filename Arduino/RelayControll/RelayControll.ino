int Relay = 27;                 // Digital pin D5

void setup() {
  Serial.begin(9600);
  pinMode(Relay, OUTPUT);       // declare Relay as output
}

void loop() {
  digitalWrite (Relay, HIGH);
  delay(500);
  digitalWrite (Relay, LOW);
  delay(500);
}
