package gov.ismonnet.shared.netty.charstuffing;

public class CharStuffingException extends RuntimeException {

    public CharStuffingException() {
    }

    public CharStuffingException(String message) {
        super(message);
    }

    public CharStuffingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CharStuffingException(Throwable cause) {
        super(cause);
    }

    public CharStuffingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
