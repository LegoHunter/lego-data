package net.bricklink.data.lego.ibatis.configuration;

public class LegoDataException extends RuntimeException {
    public LegoDataException() {
    }

    public LegoDataException(String message) {
        super(message);
    }

    public LegoDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public LegoDataException(Throwable cause) {
        super(cause);
    }

    public LegoDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
