String command;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  if (Serial.available()) {
    command = Serial.readStringUntil('\n');
    command.trim();
    Serial.println(command);
    pinMode(command.toInt(), OUTPUT);
    digitalWrite(command.toInt(), HIGH);
    delay(2000);
    digitalWrite(command.toInt(), LOW);
  }
}
