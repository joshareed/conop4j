package org.andrill.conop.core;

import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Represents a CONOP run.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class Run {
	protected final ImmutableSet<Event> events;
	protected RanksMatrix ranks = null;
	protected final ImmutableSet<Section> sections;
	protected final ImmutableMap<Event, Integer> ids;
	protected Solution best = null;

	/**
	 * Create a new run from the specified sections.
	 *
	 * @param list
	 *            the sections.
	 */
	public Run(final List<Section> list) {
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

	/**
	 * Add a new solution to this run.
	 *
	 * @param solution
	 *            the scored solution.
	 */
	public void add(final Solution solution) {
		if ((best == null) || (solution.getScore() <= best.getScore())) {
			best = solution;
			getRanksMatrix().update(solution);
		}
	}

	/**
	 * Gets all events in this run.
	 *
	 * @return the events.
	 */
	public ImmutableSet<Event> getEvents() {
		return events;
	}

	public int getId(final Event e) {
		return ids.get(e);
	}

	/**
	 * Gets the ranks matrix for this run.
	 *
	 * @return the ranks matrix.
	 */
	public RanksMatrix getRanksMatrix() {
		if (ranks == null) {
			ranks = new RanksMatrix(this);
		}
		return ranks;
	}

	/**
	 * Gets all sections in this run.
	 *
	 * @return the sections.
	 */
	public ImmutableSet<Section> getSections() {
		return sections;
	}
}
