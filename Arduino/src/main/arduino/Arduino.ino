// Char stuffing

#define DLE ((char) 10)
#define STX ((char) 2)
#define ETX ((char) 3)

bool isEscaped = false;
bool isReadingFrame = false;

int frameLength = 0;
byte frame[10];

// Channel

bool isOpen = false;
bool isReading = true;

// Arduino Packets IDs

#define READY_TO_READ_APACKET ((byte) 0)
#define PRINT_APACKET ((byte) 1)
#define PRESS_BUTTON_APACKET ((byte) 2)

// Computer Packets IDs

#define START_COMM_CPACKET ((byte) 0)
#define END_COMM_CPACKET ((byte) 1)
#define STOP_READING_CPACKET ((byte) 3)
#define LED_CPACKET ((byte) 4)

// Pins

#define LED_PIN 8
#define BUTTON_PIN 2

bool pressed;

// Methods

void setup() {
  Serial.begin(115200);
  
  pinMode(LED_PIN, OUTPUT);
  pinMode(BUTTON_PIN, INPUT);
  
  attachInterrupt(digitalPinToInterrupt(BUTTON_PIN), onButtonPress, RISING);
}

void sendPrintPacket(const String& s) {
  const char* s0 = s.c_str();
  
  Serial.write(DLE);
  Serial.write(STX);
  Serial.write(PRINT_APACKET);
  
  for(int i = 0; s0[i] != '\0'; i++) {
    char c = *(s0 + i);
    if(c == DLE) // Duplicate the DLE
      Serial.write(DLE);
    Serial.write(c);
  }
  
  Serial.write(DLE);
  Serial.write(ETX);
}

void sendPressButtonPacket() {
  Serial.write(DLE);
  Serial.write(STX);
  Serial.write(PRESS_BUTTON_APACKET);
  Serial.write(DLE);
  Serial.write(ETX);
}

void sendReadyToRead() {
  Serial.write(DLE);
  Serial.write(STX);
  Serial.write(READY_TO_READ_APACKET);
  Serial.write(DLE);
  Serial.write(ETX);
}

void handlePacket(const byte* frame, int size) {
  if(size < 1)
    return;
    
  byte packetId = frame[0];

  if(packetId == START_COMM_CPACKET) {
    isOpen = true;
  } else if(isOpen) {
    if(packetId == END_COMM_CPACKET) {
      isOpen = false;
    } else if(packetId == STOP_READING_CPACKET) {
      isReading = false;
    } else if(packetId == LED_CPACKET) {
      if(size < 2) {
        sendPrintPacket("Invalid packet: LED_CPACKET missing state");
        return;
      }
      
      boolean state = frame[1];
      digitalWrite(LED_PIN, state ? HIGH : LOW);
    }
  }
}

void handleInput() {
  while(Serial.available()) {
    byte b = Serial.read();
    
    if(!isEscaped) {
      if(b == DLE)
        isEscaped = true;
      else if(isReadingFrame)
        frame[frameLength++] = b;
    } else /*if (isEscaped)*/ {
      isEscaped = false;
        
      if(b == STX && !isReadingFrame) {
        // Start of the packet
        isReadingFrame = true;
        frameLength = 0;
      } else if(b == DLE && isReadingFrame) {
        // It's escaped, so unwrap it
        frame[frameLength++] = b;
      } else if(b == ETX && isReadingFrame) {
        // End of the packet
        handlePacket(frame, frameLength);
        isReadingFrame = false;
        frameLength = 0;
      } else {
        sendPrintPacket("Framing exception, discarding packet");
        isReadingFrame = false;
        frameLength = 0;
      }
   }
 }
}

void onButtonPress() {
  pressed = true;
}

void loop() {
  if(isReading)
    handleInput();
  else if(isOpen) {
    if(pressed)
      sendPressButtonPacket();
    pressed = false;

    isReading = true;
    sendReadyToRead();
  }
}
