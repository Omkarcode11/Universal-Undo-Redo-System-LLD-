package core.manager;

import core.exceptions.RedoNotSupportedError;
import core.exceptions.UndoNotSupportedError;
import core.history.CommandHistory;
import core.interfaces.Command;
import core.interfaces.Undoable;
import core.utils.Mutex;

public class UndoRedoManager {
    public final CommandHistory undoHistory;
    public final CommandHistory redoHistory;
    public final CommandExecutor commandExecutor;
    public final Mutex mutex;

    public UndoRedoManager(CommandHistory undoHistory, CommandHistory redoHistory, CommandExecutor commandExecutor) {
        this.undoHistory = undoHistory;
        this.commandExecutor = commandExecutor;
        this.redoHistory = redoHistory;
        this.mutex = new Mutex();
    }

    public void execute(Command command) {
        mutex.lock();
        try {
            commandExecutor.execute(command);

            if (command instanceof Undoable) {
                undoHistory.push(command);
                redoHistory.clear();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            mutex.unlock();
        }
    }

    public void undo() {
        mutex.lock();
        try {
            if (undoHistory.isEmpty()) {
                throw new UndoNotSupportedError("No command to undo");
            }

            Command command = undoHistory.pop();

            if (!(command instanceof Undoable)) {
                throw new UndoNotSupportedError("Command is not undoable");
            }

            command.undo();
            redoHistory.push(command);
        } catch (Exception e) {
            throw e;
        } finally {
            mutex.unlock();
        }
    }

    public void redo() {
        mutex.lock();
        try {
            if (redoHistory.isEmpty()) {
                throw new RedoNotSupportedError("No command to redo");
            }

            Command command = redoHistory.pop();

            command.redo();
            undoHistory.push(command);
        } catch (Exception e) {
            throw e;
        } finally {
            mutex.unlock();
        }
    }

    public void clearHistory() {
        mutex.lock();
        try {
            undoHistory.clear();
            redoHistory.clear();
        } catch (Exception e) {
            throw e;
        } finally {
            mutex.unlock();
        }
    }
}
