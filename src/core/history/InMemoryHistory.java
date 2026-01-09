package core.history;

import core.interfaces.Command;
import java.util.Stack;

public class InMemoryHistory implements CommandHistory {

    private Stack<Command> stack = new Stack<>();

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
