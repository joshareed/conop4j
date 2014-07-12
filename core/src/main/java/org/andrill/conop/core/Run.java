package org.andrill.conop.core;

import com.google.common.collect.ImmutableSet;

public interface Run {

	/**
	 * Gets all events in this run.
	 *
	 * @return the events.
	 */
	public abstract ImmutableSet<Event> getEvents();

	public abstract int getId(Event e);

	/**
	 * Gets all sections in this run.
	 *
	 * @return the sections.
	 */
	public abstract ImmutableSet<Section> getSections();

}