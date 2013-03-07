/**
 * Constraints are used to constraint string value attributes to a certain patterns, like
 * valid numbers (double or integer), booleans or mail address. They are used in
 * conjunction with 
 * {@link de.skuzzle.polly.sdk.UserManager#addAttribute(String, String, 
 * AttributeConstraint)}.
 * 
 * Additionally you can provide own constraints by implementing the interface 
 * {@link de.skuzzle.polly.sdk.constraints.AttributeConstraint}
 * 
 * @since 0.7
 */
package de.skuzzle.polly.sdk.constraints;