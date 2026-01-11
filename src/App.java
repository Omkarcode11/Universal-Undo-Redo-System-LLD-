import core.history.InMemoryHistory;
import core.manager.CommandExecutor;
import core.manager.UndoRedoManager;
import core.utils.DeleteFileCommand;
import core.interfaces.Command;
import core.interfaces.Undoable;
import core.exceptions.UndoNotSupportedError;
import core.exceptions.RedoNotSupportedError;

import java.util.*;

public class App {
    public static void main(String[] args) {
        TestRunner runner = new TestRunner();

        runner.addTest("Test Basic Execute and Undo", () -> {
            InMemoryHistory undoHistory = new InMemoryHistory();
            InMemoryHistory redoHistory = new InMemoryHistory();
            CommandExecutor executor = new CommandExecutor();
            UndoRedoManager manager = new UndoRedoManager(undoHistory, redoHistory, executor);

            DeleteFileCommand.FILE_SYSTEM.put("test.txt", "content");
            DeleteFileCommand cmd = new DeleteFileCommand("test.txt");

            manager.execute(cmd);
            assert !DeleteFileCommand.FILE_SYSTEM.containsKey("test.txt") : "File should be deleted";
            assert !undoHistory.isEmpty() : "Undo history should not be empty";

            manager.undo();
            assert DeleteFileCommand.FILE_SYSTEM.containsKey("test.txt") : "File should be restored";
            assert "content".equals(DeleteFileCommand.FILE_SYSTEM.get("test.txt")) : "Content should match";
            assert undoHistory.isEmpty() : "Undo history should be empty";
            assert !redoHistory.isEmpty() : "Redo history should not be empty";
        });

        runner.addTest("Test Redo Functionality", () -> {
            InMemoryHistory undoHistory = new InMemoryHistory();
            InMemoryHistory redoHistory = new InMemoryHistory();
            CommandExecutor executor = new CommandExecutor();
            UndoRedoManager manager = new UndoRedoManager(undoHistory, redoHistory, executor);

            DeleteFileCommand.FILE_SYSTEM.put("test2.txt", "content2");
            DeleteFileCommand cmd = new DeleteFileCommand("test2.txt");

            manager.execute(cmd);
            manager.undo();
            manager.redo();

            assert !DeleteFileCommand.FILE_SYSTEM.containsKey("test2.txt") : "File should be deleted again after redo";
            assert !undoHistory.isEmpty() : "Undo history should have the command again";
            assert redoHistory.isEmpty() : "Redo history should be empty after redo";
        });

        runner.addTest("Test History Clearing on New Command", () -> {
            InMemoryHistory undoHistory = new InMemoryHistory();
            InMemoryHistory redoHistory = new InMemoryHistory();
            CommandExecutor executor = new CommandExecutor();
            UndoRedoManager manager = new UndoRedoManager(undoHistory, redoHistory, executor);

            DeleteFileCommand cmd1 = new DeleteFileCommand("f1.txt");
            DeleteFileCommand cmd2 = new DeleteFileCommand("f2.txt");

            manager.execute(cmd1);
            manager.undo();
            assert !redoHistory.isEmpty() : "Redo history should have cmd1";

            manager.execute(cmd2);
            assert redoHistory.isEmpty() : "Redo history should be cleared after new execute";
        });

        runner.addTest("Test Undo Not Supported Error", () -> {
            InMemoryHistory undoHistory = new InMemoryHistory();
            InMemoryHistory redoHistory = new InMemoryHistory();
            CommandExecutor executor = new CommandExecutor();
            UndoRedoManager manager = new UndoRedoManager(undoHistory, redoHistory, executor);

            try {
                manager.undo();
                throw new RuntimeException("Should have thrown UndoNotSupportedError");
            } catch (UndoNotSupportedError e) {
                // Expected
            }
        });

        runner.addTest("Test Redo Not Supported Error", () -> {
            InMemoryHistory undoHistory = new InMemoryHistory();
            InMemoryHistory redoHistory = new InMemoryHistory();
            CommandExecutor executor = new CommandExecutor();
            UndoRedoManager manager = new UndoRedoManager(undoHistory, redoHistory, executor);

            try {
                manager.redo();
                throw new RuntimeException("Should have thrown RedoNotSupportedError");
            } catch (RedoNotSupportedError e) {
                // Expected
            }
        });

        runner.addTest("Test Non-Undoable Command", () -> {
            InMemoryHistory undoHistory = new InMemoryHistory();
            InMemoryHistory redoHistory = new InMemoryHistory();
            CommandExecutor executor = new CommandExecutor();
            UndoRedoManager manager = new UndoRedoManager(undoHistory, redoHistory, executor);

            Command nonUndoable = new Command() {
                @Override
                public void execute() {
                    System.out.println("Executing non-undoable");
                }

                @Override
                public void undo() {
                }

                @Override
                public void redo() {
                }
            };

            manager.execute(nonUndoable);
            assert undoHistory.isEmpty() : "Non-undoable command should not be in history";
        });

        runner.run();
    }
}

class TestRunner {
    private final List<TestItem> tests = new ArrayList<>();

    public void addTest(String name, Runnable test) {
        tests.add(new TestItem(name, test));
    }

    public void run() {
        System.out.println("\n==========================================");
        System.out.println("       Running Undo-Redo System Tests       ");
        System.out.println("==========================================\n");

        int passed = 0;
        int failed = 0;

        for (TestItem item : tests) {
            System.out.print("Running: " + item.name + " ... ");
            try {
                item.test.run();
                System.out.println("PASSED");
                passed++;
            } catch (AssertionError e) {
                System.out.println("FAILED");
                System.out.println("   Reason: " + e.getMessage());
                failed++;
            } catch (Exception e) {
                System.out.println("ERROR");
                System.out.println("   Exception: " + e.toString());
                e.printStackTrace(System.out);
                failed++;
            }
        }

        System.out.println("\n------------------------------------------");
        System.out.println("Tests Finished!");
        System.out.println("Total:  " + tests.size());
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("------------------------------------------\n");

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static class TestItem {
        String name;
        Runnable test;

        TestItem(String name, Runnable test) {
            this.name = name;
            this.test = test;
        }
    }
}
