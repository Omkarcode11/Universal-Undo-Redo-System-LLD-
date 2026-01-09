package core.history;

import core.interfaces.Command;
import core.interfaces.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

public class PersistentHistory implements CommandHistory {

    public final Deque<Command> stack = new ArrayDeque<>();

    public void push(Command command) {
        stack.push(command);

        if (command instanceof Serializable) {
            persist((Serializable<?>) command);
        }
    }

    public void clear() {
        stack.clear();
        clearPersistence();
    }

    public Command pop() {
        return stack.pop();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public void persist(Serializable<?> command) {

    }

    public void clearPersistence() {

    }
}
