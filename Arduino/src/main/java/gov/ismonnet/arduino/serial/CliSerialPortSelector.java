package gov.ismonnet.arduino.serial;

import purejavacomm.CommPortIdentifier;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Scanner;

public class CliSerialPortSelector implements SerialPortSelector {

    private final PrintStream out;
    private final Scanner scanner;

    public CliSerialPortSelector(PrintStream out, Scanner scanner) {
        this.out = out;
        this.scanner = scanner;
    }

    @Override
    public String choosePort() {
        final String[] ports = Collections.list(CommPortIdentifier.getPortIdentifiers()).stream()
                .map(CommPortIdentifier::getName)
                .toArray(String[]::new);

        out.println("Ports: " + String.join(", ", ports));
        out.print("Choose a valid port to use: ");

        final String portIn = scanner.next();
        out.println("Selected: " + portIn);
        return portIn;
    }
}
