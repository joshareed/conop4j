package org.andrill.conop.core;

import java.math.BigDecimal;

public interface Observation {

	/**
	 * Gets the event.
	 *
	 * @return the event.
	 */
	public abstract Event getEvent();

	/**
	 * Gets the level.
	 *
	 * @return the level.
	 */
	public abstract BigDecimal getLevel();

	/**
	 * Gets the down-weight.
	 *
	 * @return the down-weight.
	 */
	public abstract double getWeightDown();

	/**
	 * Gets the up-weight.
	 *
	 * @return the up-weight.
	 */
	public abstract double getWeightUp();

}