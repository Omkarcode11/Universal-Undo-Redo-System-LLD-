# Universal Undo-Redo System (LLD)

A professional-grade, thread-safe, and highly extensible Undo-Redo system built using the **Command Design Pattern**. This architecture is designed to handle complex state management in applications (like text editors, file systems, or financial tools) while maintaining strict decoupling and memory efficiency.

---

## 🏛️ Deep Dive Architecture

### 1. Detailed Class Diagram
This diagram shows the static structure of the system, highlighting the interfaces, their implementations, and the relationships that enable decoupling.

```mermaid
classDiagram
    namespace Core_Interfaces {
        class Command {
            <<interface>>
            +execute()*
            +undo()*
            +redo()*
        }
        class Undoable {
            <<interface>> 
            Note: Marker for tracking
        }
        class BoundedHistory {
            <<interface>>
            +pushWithLimit(Command, int)*
        }
        class Serializable~T~ {
            <<interface>>
            +serializable() T*
        }
    }

    namespace History_Management {
        class CommandHistory {
            <<interface>>
            +push(Command)*
            +pop() Command*
            +isEmpty() boolean*
            +clear()*
        }
        class InMemoryHistory {
            -Stack~Command~ stack
            +push(Command)
            +pop() Command
            +pushWithLimit(Command, int)
        }
    }

    namespace Execution_Control {
        class UndoRedoManager {
            -CommandHistory undoHistory
            -CommandHistory redoHistory
            -CommandExecutor commandExecutor
            -Mutex mutex
            +execute(Command)
            +undo()
            +redo()
            +clearHistory()
        }
        class CommandExecutor {
            +execute(Command)
        }
        class Mutex {
            -ReentrantLock lock
            +lock()
            +unlock()
        }
    }

    UndoRedoManager "1" o-- "2" CommandHistory : manages
    UndoRedoManager "1" o-- "1" CommandExecutor : delegates
    UndoRedoManager "1" o-- "1" Mutex : protects
    InMemoryHistory ..|> CommandHistory
    InMemoryHistory ..|> BoundedHistory
    DeleteFileCommand --|> AbstractCommand
    DeleteFileCommand ..|> Undoable
    AbstractCommand ..|> Command
    SendEmailCommand --|> AbstractCommand
```

---

### 2. Operational Sequence Diagrams

#### A. Command Execution Workflow
This diagram illustrates what happens when a user triggers a new action. Notice how the system automatically clears the "Redu" stack and decides whether to track the command based on the `Undoable` interface.

```mermaid
sequenceDiagram
    participant U as Client/User
    participant M as UndoRedoManager
    participant X as CommandExecutor
    participant C as ConcreteCommand
    participant UH as UndoHistory
    participant RH as RedoHistory

    U->>M: execute(command)
    M->>M: mutex.lock()
    M->>X: execute(command)
    X->>C: execute()
    
    alt is instance of Undoable
        M->>UH: push(command)
        M->>RH: clear()
        Note over M,RH: Standard Undo logic: New action breaks the redo chain
    else is not Undoable
        Note over M: Command executes but won't be tracked
    end
    
    M->>M: mutex.unlock()
    M-->>U: Success
```

#### B. The Undo Process
Shows the step-by-step movement of a command from the Undo stack back to the Redo stack after reversing its effects.

```mermaid
sequenceDiagram
    participant U as Client/User
    participant M as UndoRedoManager
    participant UH as UndoHistory
    participant C as ConcreteCommand
    participant RH as RedoHistory

    U->>M: undo()
    M->>M: mutex.lock()
    
    M->>UH: pop()
    UH-->>M: command
    
    M->>C: undo()
    Note right of C: Command restores previous state
    
    M->>RH: push(command)
    
    M->>M: mutex.unlock()
    M-->>U: State Restored
```

---

## 🧩 Detailed Class & Strategy Explanation

### **The manager (`UndoRedoManager`)**
*   **Why**: Centralized control. Without it, the UI would have to manually manage stacks and thread locks.
*   **What**: It coordinates the two stacks (`undo` and `redo`) and ensures that if one action is undone, it's ready to be redone.
*   **How**: It uses a **Composition** strategy. It doesn't care how the history is stored (In-Memory, Database, etc.) or how the command works; it only interacts with the `Command` and `CommandHistory` interfaces.

### **The History Strategy (`InMemoryHistory`)**
*   **Why**: Memory management. Storing infinite history leads to `OutOfMemoryError`.
*   **What**: Implements a Stack-based storage with **History Limiting**.
*   **How**: When `pushWithLimit` is called, it checks if the stack size exceeds the limit. If it does, it removes the **oldest** element (index 0) before adding the new one. This is a circular-buffer-like behavior using a Stack.

### **The Command Abstraction (`AbstractCommand`)**
*   **Why**: Reduces boilerplate code.
*   **What**: Most commands treat `redo` exactly like their initial `execute`.
*   **How**: It provides a default `redo()` implementation that simply calls `execute()`, so concrete commands only need to define `execute` and `undo`.

### **Thread Safety (`Mutex`)**
*   **Why**: Race conditions. If two threads call `undo()` at the exact same millisecond, both might try to pop the same command.
*   **What**: A wrapper around `ReentrantLock`.
*   **How**: Every public method in `UndoRedoManager` is wrapped in a `lock()` / `finally { unlock() }` block. This ensures **atomic** operations.

---

## � Project Structure
```text
src/core/
├── exceptions/    # Custom errors (UndoNotSupported, etc.)
├── history/       # Implementations of Command storage
├── interfaces/    # The "Contract" (Command, Undoable, Bounded)
├── manager/       # The brain (UndoRedoManager, Executor)
└── utils/         # Concrete commands and helpers (Mutex, Limiter)
```

## 🚀 Testing & Validation
The system includes a custom test runner in `App.java` that simulates real-world usage:
1.  **Compile**: `javac -d bin -sourcepath src src/App.java`
2.  **Run**: `java -ea -cp bin App`

### Key Test Scenarios:
- **Redo Chain Break**: Validates that executing a new command after an undo clears the redo history.
- **Exception Safety**: Ensures the system doesn't crash if you try to undo/redo with an empty history.
- **Marker Interface Logic**: Validates that commands *not* implements `Undoable` are ignored by the history tracker.
