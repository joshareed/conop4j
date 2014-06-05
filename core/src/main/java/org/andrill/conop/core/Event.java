package org.andrill.conop.core;

public interface Event {

	/**
	 * Gets the event this event must appear after in a valid solution.
	 * 
	 * @return the event or null if not constrained.
	 */
	public abstract Event getAfterConstraint();

	/**
	 * Gets the event this event must appear before in a valid solution.
	 * 
	 * @return the event or null if not constrained.
	 */
	public abstract Event getBeforeConstraint();

	/**
	 * Gets the name of this event.
	 * 
	 * @return the name.
	 */
	public abstract String getName();

}