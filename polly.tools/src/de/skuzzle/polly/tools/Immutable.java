package de.skuzzle.polly.tools;

/**
 * Tagging interface for immutable data types. Those types internal state must not be
 * modifiable by any operation. Instead, operations that would modify the state, must
 * return a new instance of the type.
 * 
 * @author Simon
 */
public interface Immutable {}
