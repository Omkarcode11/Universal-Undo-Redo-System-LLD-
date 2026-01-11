package core.utils;

import core.history.CommandHistory;
import core.interfaces.BoundedHistory;
import core.interfaces.Command;
import core.interfaces.Undoable;

public class HistoryLimiter {

    private final int limit;

    public HistoryLimiter(int limit) {
        this.limit = limit;
    }

    public void push(CommandHistory history, Command command) {
        if (history instanceof BoundedHistory) {
            ((BoundedHistory) history).pushWithLimit(command, limit);
        } else {
            history.push(command);
        }
    }

}
