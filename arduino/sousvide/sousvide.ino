/**
    Bluetooth sousvide v1.0
    by benparvar@gmail.com
*/

#include <SoftwareSerial.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#include <PID_v1.h>
//#include <PID_AutoTune_v0.h>

#define THERMOMETER_PIN 2
#define HEATER_PIN 13
#define PID_OUTPUT 0

boolean DEBUG = false;

// Last
String lastSendData = "";

// STATUS
String STS_OFF = "0";
String STS_READY = "1";
String STS_COOK_IN_PROGRESS = "2";
String STS_COOK_FINISHED = "3";

// COMMAND
String HEADER = "PAN";
String SEPARATOR = ":";
String VERB = "V";
String NOUN = "N";
String STATUS = "S";

// VERB
String PAN_OFF = "000";
String PAN_ON = "001";
String PAN_TIMER = "002";
String PAN_TEMPERATURE = "003";
String PAN_TIMER_TARGET = "004";
String PAN_TEMPERATURE_TARGET = "005";
String PAN_CURRENT_TIMER = "006";
String PAN_CURRENT_TEMPERATURE = "007";
String PAN_READY = "008";
String PAN_COOK_IN_PROGRESS = "009";
String PAN_COOK_FINISHED = "010";
String PID_VALUE = "011";

// ERROR CODE
String INVALID_HEADER = "900";
String INVALID_VERB = "901";
String INVALID_NOUN = "902";
String INVALID_TIMER_TARGET = "903";
String INVALID_TEMPERATURE_TARGET = "904";
String INVALID_ALREADY_OFF = "905";
String INVALID_ALREADY_COOKING = "906";
String INVALID_ALREADY_FINISHED_COOKING = "907";
String INVALID_NO_PROGRAMMED = "908";

// PAN STATUS
String panStatus = STS_OFF;

double VALUE_OF_INVALID_TEMPERATURE = -127;

// PAN TIMER
long currentTimer = 0;
long targetTimer = 0;

// PAN TEMPERATURE
double currentTemperature = 0;
double targetTemperature = 0;

// TEMPERATURE LIMITS (C)
int MIN_TEMPERATURE = 3000;
int MAX_TEMPERATURE = 6000;

// TIMER (MILI SECONDS)
long MIN_TIMER = 0;
long MAX_TIMER = 86400; // A day

long previousMillis = 0;
long millisInterval = 1000;

//PID Stuff
double kp = 196, ki = 33, kd = 290, PIDOutput = PID_OUTPUT;
PID PIDCalculation(&currentTemperature, &PIDOutput, &targetTemperature, kp, ki, kd, DIRECT);

SoftwareSerial swSerial = SoftwareSerial(1, 0);
OneWire ds18b20(THERMOMETER_PIN);
DallasTemperature temperatureSensor(&ds18b20);

void setup() {
  pinMode(HEATER_PIN, OUTPUT);
  swSerial.begin(9600); // this sets the the module to run at the default bound rate
  initializePID();
}

/**
   Read the current temperature
*/
void readCurrentTemperature() {
  temperatureSensor.requestTemperatures();
  double tempe = temperatureSensor.getTempCByIndex(0);

  if (VALUE_OF_INVALID_TEMPERATURE != tempe) {
    currentTemperature = tempe * 100;

    //PAN CURRENT TEMPERATURE -> "PAN:S:007:0000:0000"
    String data = HEADER;
    data.concat(SEPARATOR);
    data.concat(STATUS);
    data.concat(SEPARATOR);
    data.concat(PAN_CURRENT_TEMPERATURE);
    data.concat(SEPARATOR);
    data.concat(currentTemperature);
    data.concat(SEPARATOR);
    data.concat(targetTemperature);
    sendData(data);
  }
}

void readCurrentTimer() {
  if (panStatus == STS_COOK_IN_PROGRESS) {
    if ( countTimer()) {
      currentTimer++;
    }

    if (currentTimer > targetTimer) {
      cookOff();
      panStatus == STS_COOK_FINISHED;
    }

    //PAN CURRENT TIMER -> "PAN:S:006:0000:0000"
    String data = HEADER;
    data.concat(SEPARATOR);
    data.concat(STATUS);
    data.concat(SEPARATOR);
    data.concat(PAN_CURRENT_TIMER);
    data.concat(SEPARATOR);
    data.concat(currentTimer);
    data.concat(SEPARATOR);
    data.concat(targetTimer);
    sendData(data);
  }
}

void readCurrentStatus() {
  String status = "";

  if (panStatus == STS_OFF) {
    status = PAN_OFF;
  } else if (panStatus == STS_READY) {
    status = PAN_READY;
  } else if (panStatus == STS_COOK_IN_PROGRESS) {
    status = PAN_COOK_IN_PROGRESS;
  } else if (panStatus == STS_COOK_FINISHED) {
    status = PAN_COOK_FINISHED;
  }

  String data = HEADER;
  data.concat(SEPARATOR);
  data.concat(STATUS);
  data.concat(SEPARATOR);
  data.concat(status);
  sendData(data);
}

/**
   Reset the PAN
*/
void reset() {
  targetTemperature = 0;
  targetTimer = 0;
  currentTemperature = 0;
  currentTimer = 0;
  panStatus = STS_OFF;
}

/**
   Verify if the PAN is programmed
*/
void verifyProgram() {
  if ((targetTemperature > 0) && (targetTimer > 0)) {
    panStatus = STS_READY;
  }
}

/**
   Cook on
*/
void cookOn() {
  if (panStatus == STS_OFF) {
    sendError(INVALID_NO_PROGRAMMED);
  } else if (panStatus == STS_COOK_IN_PROGRESS) {
    sendError(INVALID_ALREADY_COOKING);
  } else if (panStatus == STS_COOK_FINISHED) {
    sendError(INVALID_ALREADY_FINISHED_COOKING);
  } else if (panStatus == STS_READY) {
    panStatus = STS_COOK_IN_PROGRESS;
    digitalWrite(HEATER_PIN, HIGH);
    //PAN ON -> "PAN:S:001"
    String data = HEADER;
    data.concat(SEPARATOR);
    data.concat(STATUS);
    data.concat(SEPARATOR);
    data.concat(PAN_ON);
    sendData(data);
  }
}

/**
   Cook off
*/
void cookOff() {
  if (panStatus == STS_OFF || panStatus == STS_READY) {
    sendError(INVALID_ALREADY_OFF);
  } else if (panStatus == STS_COOK_IN_PROGRESS || panStatus == STS_COOK_FINISHED) {
    digitalWrite(HEATER_PIN, LOW);
    reset();
    //PAN OFF -> "PAN:S:000"
    String data = HEADER;
    data.concat(SEPARATOR);
    data.concat(STATUS);
    data.concat(SEPARATOR);
    data.concat(PAN_OFF);
    sendData(data);
  }
}

/**
   Set the timer
*/
void setTimer(String timer) {
  sendDebug("timer", timer);

  float tim = timer.toFloat();

  if (tim < MIN_TIMER || tim > MAX_TIMER) {
    sendError(INVALID_TIMER_TARGET);
  } else {
    targetTimer = tim;
    verifyProgram();
    //PAN TIMER -> "PAN:S:004:00000"
    String data = HEADER;
    data.concat(SEPARATOR);
    data.concat(STATUS);
    data.concat(SEPARATOR);
    data.concat(PAN_TIMER_TARGET);
    data.concat(SEPARATOR);
    data.concat(timer);
    sendData(data);
  }
}

/**
   Set the temperature
*/
void setTemperature(String temperature) {
  sendDebug("temperature", temperature);

  float temper = temperature.toFloat();

  if (temper < MIN_TEMPERATURE || temper > MAX_TEMPERATURE) {
    sendError(INVALID_TEMPERATURE_TARGET);
  } else {
    targetTemperature = temper;
    verifyProgram();
    //PAN TEMPERATURE -> "PAN:S:005:00000"
    String data = HEADER;
    data.concat(SEPARATOR);
    data.concat(STATUS);
    data.concat(SEPARATOR);
    data.concat(PAN_TEMPERATURE_TARGET);
    data.concat(SEPARATOR);
    data.concat(temperature);
    sendData(data);
  }
}

/**
   Try to keep the temperature
*/
void controlTemperature() {
  if (panStatus == STS_COOK_IN_PROGRESS) {
    analogWrite(HEATER_PIN, PIDOutput);
  } else {
    digitalWrite(HEATER_PIN, LOW);
  }
}

// INI PID
void initializePID() {
  PIDCalculation.SetTunings(kp, ki, kd);
  PIDCalculation.SetMode(AUTOMATIC);
}

void calculatePID() {
  double delta = (targetTemperature - currentTemperature) / 100;

  //  swSerial.print("tar : ");
  //  swSerial.println(targetTemperature / 100);
  //  swSerial.print("cur : ");
  //  swSerial.println(currentTemperature / 100);
  //  swSerial.print("del : ");
  //  swSerial.println(delta);

  // Limit the resistor block using PWM since the heating is precise
  if (delta > 8) {
    PIDCalculation.SetOutputLimits(0, 255);
  } else if (delta > 5) {
    PIDCalculation.SetOutputLimits(0, 235);
  } else if (delta > 3) {
    PIDCalculation.SetOutputLimits(0, 215);
  } else if (delta > 2) {
    PIDCalculation.SetOutputLimits(0, 195);
  } else if (delta > 1) {
    PIDCalculation.SetOutputLimits(0, 175);
  } else if (delta > 0) {
    PIDCalculation.SetOutputLimits(0, 155);
  } else if (delta > -2) {
    PIDCalculation.SetOutputLimits(0, 255);
  }

  // Compute the PID calculation
  PIDCalculation.Compute();

  if (panStatus == STS_COOK_IN_PROGRESS && DEBUG) {
    String data = HEADER;
    data.concat(SEPARATOR);
    data.concat(STATUS);
    data.concat(SEPARATOR);
    data.concat(PID_VALUE);
    data.concat(SEPARATOR);
    data.concat(PIDOutput);
    data.concat(SEPARATOR);
    data.concat(delta);
    sendData(data);
  }
}
// END PID

/**
   Receive data from external devices
*/
void receiveData() {
  String inData;
  if (swSerial.available()) {
    inData = swSerial.readString();

    // Header
    if (inData.substring(0, 3) == HEADER) {
      // Verb
      inData = inData.substring(4, inData.length());
      sendDebug("Verb", inData);
      if (inData.substring(0, 1) == VERB) {
        // Verb Data
        inData = inData.substring(2, inData.length());
        sendDebug("Verb Data", inData);

        // off
        if (inData.substring(0, 3) == PAN_OFF) {
          cookOff();
        }

        // on
        if (inData.substring(0, 3) == PAN_ON) {
          cookOn();
        }

        // timer
        if (inData.substring(0, 3) == PAN_TIMER) {
          inData = inData.substring(4, inData.length());
          sendDebug("Noun Data", inData);
          setTimer(inData);
        }

        // temperature
        if (inData.substring(0, 3) == PAN_TEMPERATURE) {
          inData = inData.substring(4, inData.length());
          sendDebug("Noun Data", inData);
          setTemperature(inData);
        }

      } else {
        sendDebug("Verb invalid", inData);
        sendError(INVALID_VERB);
      }
    } else {
      sendError(INVALID_HEADER);
    }
  }
}

/**
   Send error to output
*/
void sendError(String error) {
  String data = HEADER;
  data.concat(SEPARATOR);
  data.concat(STATUS);
  data.concat(SEPARATOR);
  data.concat(error);
  sendData(data);
}

/*
   Send data to output
*/
void sendData(String data) {
  if (lastSendData != data) {
    swSerial.println(data);
    lastSendData = data;
  }
}

/*
   Send debug to output
*/
void sendDebug(String tag, String data) {
  if (DEBUG) {
    swSerial.print("\n*************\n");
    swSerial.print(tag);
    swSerial.print('\n');
    swSerial.print(data);
    swSerial.print("\n*************\n");
  }
}

/**
   Count timer 1000 miliseconds
*/
boolean countTimer() {
  unsigned long currentMillis = millis();
  boolean count = false;

  if (currentMillis - previousMillis > millisInterval) {
    previousMillis = currentMillis;
    count = true;
  }

  return count;
}

/**
   The loop
*/
void loop() {
  receiveData();
  readCurrentTemperature();
  calculatePID();
  readCurrentTimer();
  readCurrentStatus();
  controlTemperature();
}
