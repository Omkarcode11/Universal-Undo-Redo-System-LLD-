package core.utils;

import core.interfaces.Command;

public abstract class AbstractCommand implements  Command {
    
    @Override
    public void redo() {
        execute();
    }
}
