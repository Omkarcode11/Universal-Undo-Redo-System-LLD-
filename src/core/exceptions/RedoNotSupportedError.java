package core.exceptions;

public class RedoNotSupportedError extends RuntimeException {

    public RedoNotSupportedError() {
        super("Redo operation is not supported for this command");
    }

    public RedoNotSupportedError(String message) {
        super(message);
    }

}
