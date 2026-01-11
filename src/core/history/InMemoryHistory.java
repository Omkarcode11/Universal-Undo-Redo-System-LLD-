package core.history;

import core.interfaces.BoundedHistory;
import core.interfaces.Command;
import java.util.Stack;

public class InMemoryHistory implements CommandHistory, BoundedHistory {

    private Stack<Command> stack = new Stack<>();

    public void pushWithLimit(Command command, int maxLimit) {
        while (stack.size() >= maxLimit) {
            stack.removeElementAt(0);
        }
        stack.push(command);
    }

    public void push(Command command) {
        stack.push(command);
    }

    public Command pop() {
        return stack.pop();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public void clear() {
        stack.clear();

    }
}
