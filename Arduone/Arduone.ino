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

// Methods

void setup() {
  Serial.begin(115200);
}

void sendPrintPacket(const String& s) {
  const char* s0 = s.c_str();
  
  Serial.write(DLE);
  Serial.write(STX);
  Serial.write(1); // Packet id
  
  for(int i = 0; s0[i] != '\0'; i++) {
    char c = *(s0 + i);
    if(c == DLE) // Duplicate the DLE
      Serial.write(DLE);
    Serial.write(c);
  }
  
  Serial.write(DLE);
  Serial.write(ETX);
}

void sendReadyToRead() {
  Serial.write(DLE);
  Serial.write(STX);
  Serial.write(0);
  Serial.write(DLE);
  Serial.write(ETX);
}

void handlePacket(const byte* frame, int size) {
  if(size < 1)
    return;
    
  byte packetId = frame[0];

  if(packetId == 0) {
    isOpen = true;
  } else if(isOpen) {
    if(packetId == 1) {
      isOpen = false;
    } else if(packetId == 3) {
      isReading = false;
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
        // TODO: Either raise an exception or log the error?
        isReadingFrame = false;
        frameLength = 0;
      }
   }
 }
}

void loop() {
  if(isReading)
    handleInput();
  else if(isOpen) {
    sendPrintPacket("Porcodio");

    isReading = true;
    sendReadyToRead();
  }
}
