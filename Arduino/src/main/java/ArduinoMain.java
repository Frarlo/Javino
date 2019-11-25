import gov.ismonnet.arduino.netty.ArduinoSerialManager;
import gov.ismonnet.arduino.serial.CliSerialPortSelector;
import gov.ismonnet.arduino.serial.SerialPortSelector;

import java.util.Scanner;

public class ArduinoMain {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final SerialPortSelector serialPortSelector = new CliSerialPortSelector(System.out, scanner);
        final ArduinoSerialManager serialManager = new ArduinoSerialManager(serialPortSelector.choosePort());

        serialManager.start();
    }
}
