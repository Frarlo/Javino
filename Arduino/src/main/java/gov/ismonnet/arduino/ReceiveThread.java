package gov.ismonnet.arduino;

import gov.ismonnet.shared.Commands;

import java.net.DatagramSocket;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ReceiveThread extends Thread {
    private Supplier<Commands> data;
    private Consumer<Commands> consumer;

    public ReceiveThread(Supplier<Commands> data, Consumer<Commands> consumer) {
        this.data = data;
        this.consumer = consumer;
    }

    @Override
    public void run(){

        System.out.println("RECEIVE THREAD STARTED");

        while(!isInterrupted())
            consumer.accept(data.get());

        System.out.println("RECEIVE THREAD INTERRUPTED");
    }

}
