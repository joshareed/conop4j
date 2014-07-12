package org.andrill.conop.core.internal;

import java.util.List;

import org.andrill.conop.core.Event;
import org.andrill.conop.core.Run;
import org.andrill.conop.core.Section;
import org.andrill.conop.core.Solution;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Represents a CONOP run.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class DefaultRun implements Run {
	protected final ImmutableSet<Event> events;
	protected final ImmutableSet<Section> sections;
	protected final ImmutableMap<Event, Integer> ids;
	protected Solution best = null;

	/**
	 * Create a new run from the specified sections.
	 *
	 * @param list
	 *            the sections.
	 */
	public DefaultRun(final List<Section> list) {
		sections = ImmutableSet.copyOf(list);

		// build our immutable set of events
		Builder<Event> b = ImmutableSet.builder();
		for (Section s : sections) {
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
	 * @see org.andrill.conop.core.Run#getEvents()
	 */
	@Override
	public ImmutableSet<Event> getEvents() {
		return events;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andrill.conop.core.Run#getId(org.andrill.conop.core.Event)
	 */
	@Override
	public int getId(final Event e) {
		return ids.get(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andrill.conop.core.Run#getSections()
	 */
	@Override
	public ImmutableSet<Section> getSections() {
		return sections;
	}
}
