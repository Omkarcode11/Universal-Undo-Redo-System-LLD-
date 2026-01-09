package core.exceptions;

public class UndoNotSupportedError extends RuntimeException {

    public UndoNotSupportedError() {
        super("Undo operation is not supported for this command");
    }

    public UndoNotSupportedError(String message) {
        super(message);
    }

}
