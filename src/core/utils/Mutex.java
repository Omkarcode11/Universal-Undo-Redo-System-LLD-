package core.utils;

import java.util.concurrent.locks.ReentrantLock;

public class Mutex {
    
    private final ReentrantLock lock = new ReentrantLock();

    public void lock(){
        lock.lock();
    }

    public void unlock(){
        lock.unlock();
    }
}
