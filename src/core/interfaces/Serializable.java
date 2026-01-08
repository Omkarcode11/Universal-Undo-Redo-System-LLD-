package core.interfaces;

/**
 * Indicates that a command can be serialized
 * for persistence (event sourcing, recovery).
 *
 * @param <T> Serialized form (JSON, Map, DTO, etc.)
 */

public interface  Serializable<T> {
    T serializable();
}
