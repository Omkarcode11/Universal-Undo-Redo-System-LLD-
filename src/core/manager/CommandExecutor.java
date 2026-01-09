package core.manager;

import core.interfaces.Command;

public class CommandExecutor {
    public void execute(Command command){
        command.execute();
    }
}
