package core.history;

import core.interfaces.Command;

public interface  CommandHistory {
    void push(Command command);
    Command pop();
    boolean isEmpty();
    void clear();
}
