package org.andrill.conop.core;

import com.google.common.collect.ImmutableSet;

public interface Dataset {

	/**
	 * Gets all events in this dataset.
	 *
	 * @return the events.
	 */
	public abstract ImmutableSet<Event> getEvents();

	public abstract int getId(Event e);

	/**
	 * Gets all locations in this dataset.
	 *
	 * @return the locations.
	 */
	public abstract ImmutableSet<Location> getLocations();

}