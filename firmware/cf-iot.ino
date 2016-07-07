
double volts = 0.0;
double temperature = 0.0;

int voltsPin = A1;
int tempPin = A0;

int ledPin = D0;
int motorPin = D4;
int pushButton = D2; // Push button is connected to D2

boolean blinking = false;

unsigned long PUB_INTERVAL = 2000UL;
unsigned long lastTime = 0UL;

void setup() {
    Spark.variable("temperature", &temperature, DOUBLE);
    Spark.variable("volts", &volts, DOUBLE);
    
    pinMode(ledPin, OUTPUT);
    pinMode(pushButton, INPUT_PULLUP);
    
    Spark.function("led", ledSwitcher);
    Spark.function("status", ledStatus);
    
}

void loop() {
    volts = readVolts(voltsPin);
    temperature = calcTemp(readVolts(tempPin));
    
    publishTemp();
    
    if (temperature > 40) {
        blinking = true;
        Particle.publish("above40", String(temperature));
        
        digitalWrite(motorPin, HIGH);
    }
    
    if (temperature < 35) {
        digitalWrite(motorPin, LOW);
    }
    
    
   if (blinking) {
        digitalWrite(ledPin, HIGH);
        delay(200);
        digitalWrite(ledPin, LOW);
        delay(200);
    }
    
    int pushButtonState; 

    pushButtonState = digitalRead(pushButton);

    if(pushButtonState == LOW)
    { // If we push down on the push button
        blinking = true;
        Spark.publish("slot", "play");

    }
    else
    {
        blinking = false;
    }    
    
}

/////////////////////////////////
// HELPER FUNCTIONS
/////////////////////////////////

// http://www.analog.com/static/imported-files/data_sheets/TMP35_36_37.pdf
double calcTemp(double voltage) {
    return (voltage - 0.5) * 100;
}

double readVolts(int pin) {
    //return analogRead(pin) * 3.3 / 4096.0;
    return 3.3 * ((double)analogRead(pin) / 4095.0);
}

// Improve
void publishTemp() {
    unsigned long now = millis();
    
    //Every 15 seconds publish uptime
    if (now - lastTime > PUB_INTERVAL) {
        lastTime = now;
        Spark.publish("temperature", String(temperature));
    }    
}


/////////////////////////////////
// PUBLIC FUNCTIONS
/////////////////////////////////

int ledSwitcher(String command) {
    if (command.equalsIgnoreCase("on")) {
        blinking = true;
        return 1;
    }
    else if (command.equalsIgnoreCase("off")) {
        blinking = false;
        return 1;
    }
    return -1;
}

int ledStatus(String command) {
    return blinking;
}

