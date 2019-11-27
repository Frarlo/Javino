package gov.ismonnet.shared;

public enum Commands {
    PRESS_BUTTON("B_PRESS"),
    TURN_ON_LED("LED_ON"),
    TURN_OFF_LED("LED_OFF");

    private final String toSend;

    Commands(String toSend) {
        this.toSend = toSend;
    }

    public String getToSend() {
        return toSend;
    }
}
