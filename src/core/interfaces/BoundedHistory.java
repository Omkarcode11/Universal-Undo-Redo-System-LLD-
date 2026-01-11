package core.interfaces;

public interface BoundedHistory {
    
    void pushWithLimit(Command command, int maxLimit);
}
