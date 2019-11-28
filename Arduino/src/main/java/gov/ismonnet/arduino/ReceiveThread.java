package gov.ismonnet.arduino;

import gov.ismonnet.shared.Commands;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ReceiveThread extends Thread {

    private static final Logger LOGGER = LogManager.getLogger(ReceiveThread.class);

    private final Supplier<Commands> data;
    private final Consumer<Commands> consumer;

    public ReceiveThread(Supplier<Commands> data, Consumer<Commands> consumer) {
        this.data = data;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        LOGGER.trace("RECEIVE THREAD STARTED");

        while(!isInterrupted())
            consumer.accept(data.get());

        LOGGER.trace("RECEIVE THREAD INTERRUPTED");
    }
}
