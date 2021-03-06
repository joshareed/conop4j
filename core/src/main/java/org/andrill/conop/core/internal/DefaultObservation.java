package org.andrill.conop.core.internal;

import java.math.BigDecimal;

import org.andrill.conop.core.Event;
import org.andrill.conop.core.Observation;

/**
 * Represents an observation of an event in a location.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class DefaultObservation implements Observation {
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
	public DefaultObservation(final Event event, final BigDecimal level, final double weightUp, final double weightDown) {
		this.event = event;
		this.level = level;
		this.weightUp = weightUp;
		this.weightDown = weightDown;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andrill.conop.core.Observation#getEvent()
	 */
	@Override
	public Event getEvent() {
		return event;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andrill.conop.core.Observation#getLevel()
	 */
	@Override
	public BigDecimal getLevel() {
		return level;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andrill.conop.core.Observation#getWeightDown()
	 */
	@Override
	public double getWeightDown() {
		return weightDown;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andrill.conop.core.Observation#getWeightUp()
	 */
	@Override
	public double getWeightUp() {
		return weightUp;
	}

	@Override
	public String toString() {
		return "Observation[event=" + event + ", level=" + level + ", weights=(" + weightUp + "," + weightDown + ")]";
	}
}
