/**
 * 
 * Bluetooth sousvide v1.0
 * by benparvar@gmail.com
 * 
 */
 
#include <SoftwareSerial.h>
int LED = 13;

boolean DEBUG = false;

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

// PAN TIMER
int currentTimer = 0;
int targetTimer = 0;

// PAN TEMPERATURE
int currentTemperature = 0;
int targetTemperature = 0;

// TEMPERATURE LIMITS (C)
float MIN_TEMPERATURE = 30.00;
float MAX_TEMPERATURE = 80.00;

// TIMER (MILI SECONDS)
float MIN_TIMER = 0.00;
float MAX_TIMER = 100000.00;

SoftwareSerial swSerial = SoftwareSerial(1, 0);

void setup() {
  pinMode(LED, OUTPUT);
  swSerial.begin(9600); // this sets the the module to run at the default bound rate
}

void reset() {
  targetTemperature = 0;
  targetTimer = 0;
  currentTemperature = 0;
  currentTimer = 0;
  panStatus = STS_OFF;
}

void verifyProgram() {
  if ((targetTemperature > 0) && (targetTimer > 0)) {
    panStatus = STS_READY;
  }
}

void cookOn() {
  if (panStatus == STS_OFF) {
    sendError(INVALID_NO_PROGRAMMED);
  } else if (panStatus == STS_COOK_IN_PROGRESS) {
    sendError(INVALID_ALREADY_COOKING);
  } else if (panStatus == STS_COOK_FINISHED) {
    sendError(INVALID_ALREADY_FINISHED_COOKING);
  } else if (panStatus == STS_READY) {
    panStatus = STS_COOK_IN_PROGRESS;
    digitalWrite(LED, HIGH);
    //PAN ON -> "PAN:S:001"
    String data = HEADER;
    data.concat(SEPARATOR);
    data.concat(STATUS);
    data.concat(SEPARATOR);
    data.concat(PAN_ON);
    sendData(data);
  }
}

void cookOff() {
  if (panStatus == STS_OFF || panStatus == STS_READY) {
    sendError(INVALID_ALREADY_OFF);
  } else if (panStatus == STS_COOK_IN_PROGRESS || panStatus == STS_COOK_FINISHED) {
    digitalWrite(LED, LOW);
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

void setTemperature(String temperature) {
  sendDebug("temperature", temperature);

  float temper = temperature.toFloat() / 100;

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

void sendError(String error) {
  String data = HEADER;
  data.concat(SEPARATOR);
  data.concat(STATUS);
  data.concat(SEPARATOR);
  data.concat(error);
  sendData(data);
}

void sendData(String data) {
  swSerial.print(data);
}

void sendDebug(String tag, String data) {
  if (DEBUG) {
    swSerial.print("\n*************\n");
    swSerial.print(tag);
    swSerial.print('\n');
    swSerial.print(data);
    swSerial.print("\n*************\n");
  }
}

void loop() {
  receiveData();
}
