package core.utils;

import java.util.*;

import core.interfaces.Undoable;

public class DeleteFileCommand extends AbstractCommand implements Undoable {

    public static final Map<String, String> FILE_SYSTEM = new HashMap<>();

    private final String filePath;

    private String backupContent;

    public DeleteFileCommand(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void execute() {
        backupContent = FILE_SYSTEM.remove(filePath);

    }

    @Override
    public void undo() {
        if (backupContent != null) {
            FILE_SYSTEM.put(filePath, backupContent);
        }

    }

    public void createFile(String filePath, String content){
        FILE_SYSTEM.put(filePath, content);
    }

    public static String readFile(String filePath){
        if(FILE_SYSTEM.containsKey(filePath)){
            return FILE_SYSTEM.get(filePath);
        }
        return null;
    }

}
