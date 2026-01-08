package core.interfaces;

public interface Command {
    void execute();
    void undo();
    void redo();
}
