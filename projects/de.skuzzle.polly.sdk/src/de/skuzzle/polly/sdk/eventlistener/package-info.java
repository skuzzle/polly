/**
 * <p>This package provides swing-style listener classes to react on several polly 
 * events.</p>
 * 
 * <p>For listeners with many abstract methods exists abstract adapter classes, which
 * provide an empty implementation of the interface methods.</p>
 * 
 * <p>Please mind that each event may be raised on a new/different thread. Therefore
 * you need to ensure thread-safety of your code.</p>
 * 
 * <p>Please note that you should remove your listeners, for example upon shutdown of your
 * plugin, so the garbage collector can remove them properly from memory.</p>
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
package de.skuzzle.polly.sdk.eventlistener;