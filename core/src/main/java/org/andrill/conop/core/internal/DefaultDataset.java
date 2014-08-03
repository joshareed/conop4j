package org.andrill.conop.core.internal;

import java.util.List;

import org.andrill.conop.core.Event;
import org.andrill.conop.core.Location;
import org.andrill.conop.core.Dataset;
import org.andrill.conop.core.Solution;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Represents a CONOP dataset.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class DefaultDataset implements Dataset {
	protected final ImmutableSet<Event> events;
	protected final ImmutableSet<Location> locations;
	protected final ImmutableMap<Event, Integer> ids;
	protected Solution best = null;

	/**
	 * Create a new dataset from the specified locations.
	 *
	 * @param list
	 *            the locations.
	 */
	public DefaultDataset(final List<Location> list) {
		locations = ImmutableSet.copyOf(list);

		// build our immutable set of events
		Builder<Event> b = ImmutableSet.builder();
		for (Location s : locations) {
			b.addAll(s.getEvents());
		}
		events = b.build();

		// assign ids to events
		ImmutableMap.Builder<Event, Integer> i = ImmutableMap.builder();
		int id = 0;
		for (Event e : events) {
			i.put(e, id++);
		}
		ids = i.build();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.andrill.conop.core.Dataset#getEvents()
	 */
	@Override
	public ImmutableSet<Event> getEvents() {
		return events;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.andrill.conop.core.Dataset#getId(org.andrill.conop.core.Event)
	 */
	@Override
	public int getId(final Event e) {
		return ids.get(e);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.andrill.conop.core.Dataset#getLocations()
	 */
	@Override
	public ImmutableSet<Location> getLocations() {
		return locations;
	}
}
