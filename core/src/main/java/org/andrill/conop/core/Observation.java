package org.andrill.conop.core;

import java.math.BigDecimal;

/**
 * Represents an observation of an event in a section.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class Observation {
	protected final Event event;
	protected final BigDecimal level;
	protected final double weightDown;
	protected final double weightUp;

	/**
	 * Creates a new observation for the specified event.
	 * 
	 * @param event
	 *            the event.
	 * @param level
	 *            the level.
	 * @param weightUp
	 *            the up-weight.
	 * @param weightDown
	 *            the down-weight.
	 */
	public Observation(final Event event, final BigDecimal level, final double weightUp, final double weightDown) {
		this.event = event;
		this.level = level;
		this.weightUp = weightUp;
		this.weightDown = weightDown;
	}

	/**
	 * Gets the event.
	 * 
	 * @return the event.
	 */
	public Event getEvent() {
		return event;
	}

	/**
	 * Gets the level.
	 * 
	 * @return the level.
	 */
	public BigDecimal getLevel() {
		return level;
	}

	/**
	 * Gets the down-weight.
	 * 
	 * @return the down-weight.
	 */
	public double getWeightDown() {
		return weightDown;
	}

	/**
	 * Gets the up-weight.
	 * 
	 * @return the up-weight.
	 */
	public double getWeightUp() {
		return weightUp;
	}

	@Override
	public String toString() {
		return "Observation[event=" + event + ", level=" + level + ", weights=(" + weightUp + "," + weightDown + ")]";
	}
}
