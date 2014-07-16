package org.andrill.conop.core;

import java.math.BigDecimal;

import com.google.common.collect.ImmutableSet;

public interface Location {

	/**
	 * Gets the events in this location.
	 *
	 * @return the set of events.
	 */
	public abstract ImmutableSet<Event> getEvents();

	/**
	 * Gets the levels in this location.
	 *
	 * @return the set of levels.
	 */
	public abstract ImmutableSet<BigDecimal> getLevels();

	/**
	 * Gets the name of this location.
	 *
	 * @return the name.
	 */
	public abstract String getName();

	/**
	 * Gets the observation for the specified event.
	 *
	 * @param event
	 *            the event.
	 * @return the observation.
	 */
	public abstract Observation getObservation(Event event);

	/**
	 * Gets the observations.
	 *
	 * @return the observation
	 */
	public abstract ImmutableSet<Observation> getObservations();

}