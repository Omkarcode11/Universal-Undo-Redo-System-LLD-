package core.manager;

import core.exceptions.RedoNotSupportedError;
import core.exceptions.UndoNotSupportedError;
import core.history.CommandHistory;
import core.interfaces.Command;
import core.interfaces.Undoable;

public class UndoRedoManager {
    public final CommandHistory undoHistory;
    public final CommandHistory redoHistory;
    public final CommandExecutor commandExecutor;

    public UndoRedoManager(CommandHistory undoHistory, CommandHistory redoHistory, CommandExecutor commandExecutor){
        this.undoHistory = undoHistory;
        this.commandExecutor = commandExecutor;
        this.redoHistory = redoHistory;
    }

    public void execute(Command command){
        commandExecutor.execute(command);

        if(command instanceof Undoable){
            undoHistory.push(command);
            redoHistory.clear();
        }
    }


    public void undo(){
        if(undoHistory.isEmpty()){
            throw new UndoNotSupportedError("No command to undo");
        }

        Command command = undoHistory.pop();

        if(!(command instanceof Undoable)){
            throw new UndoNotSupportedError("Command is not undoable");
        }

        command.undo();
        redoHistory.push(command);
    }

    public void redo(){
        if(redoHistory.isEmpty()){
            throw new RedoNotSupportedError("No command to redo");
        }

        Command command = redoHistory.pop();

        command.redo();
        undoHistory.push(command);
    }

    public void clearHistory(){
        undoHistory.clear();
        redoHistory.clear();
    }
}
