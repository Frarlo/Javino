package gov.ismonnet.server;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ServerReceiveThread extends Thread {

    private Supplier<String> data;
    private Consumer<String> consumer;

    public ServerReceiveThread(Supplier<String> data, Consumer<String> consumer) {
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
