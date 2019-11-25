package gov.ismonnet.arduino;

import java.net.DatagramSocket;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ReceiveThread extends Thread {
    private Supplier<String> data;
    private Consumer<String> consumer;

    public ReceiveThread(Supplier<String> data, Consumer<String> consumer) {
        this.data = data;
        this.consumer = consumer;
    }

    @Override
    public void run(){
        while(!isInterrupted())
            consumer.accept(data.get());
    }

}
