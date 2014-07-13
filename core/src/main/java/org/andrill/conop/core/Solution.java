package org.andrill.conop.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * A solution contains a set of events in a particular order.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class Solution {

	/**
	 * Creates an initial solution with events sorted by type.
	 *
	 * @param run
	 *            the run.
	 * @return the initial solution.
	 */
	public static Solution initial(final Run run) {
		final List<Event> events = Lists.newArrayList(run.getEvents());
		Collections.sort(events, new Comparator<Event>() {
			@Override
			public int compare(final Event o1, final Event o2) {
				return toInt(o1).compareTo(toInt(o2));
			}

			public Integer toInt(final Event e) {
				if ((e.getBeforeConstraint() != null) && (e.getAfterConstraint() == null)) {
					return Integer.valueOf(-1);
				} else if ((e.getAfterConstraint() != null) && (e.getBeforeConstraint() == null)) {
					return Integer.valueOf(1);
				} else {
					return Integer.valueOf(0);
				}
			}
		});
		return new Solution(run, events);
	}

	protected final ImmutableList<Event> events;
	protected final Map<Event, Integer> positions;
	protected final Run run;
	protected double score = 0.0;

	/**
	 * Create a new Solution with the specified ordered list of events.
	 *
	 * @param run
	 *            the run.
	 * @param events
	 *            the list of events.
	 */
	public Solution(final Run run, final List<Event> events) {
		this.events = ImmutableList.copyOf(events);
		this.run = run;

		// index our events
		positions = new IdentityHashMap<Event, Integer>();
		for (int i = 0; i < events.size(); i++) {
			positions.put(events.get(i), i);
		}
	}

	/**
	 * Gets the event at the specified position.
	 *
	 * @param position
	 *            the position.
	 * @return the event.
	 */
	public Event getEvent(final int position) {
		return events.get(position);
	}

	/**
	 * Gets the ordered, immutable list of events in this solution.
	 *
	 * @return the immutable list of events.
	 */
	public ImmutableList<Event> getEvents() {
		return events;
	}

	/**
	 * Gets the position of the event in this solution.
	 *
	 * @param event
	 *            the event.
	 * @return the position.
	 */
	public int getPosition(final Event event) {
		return positions.get(event);
	}

	/**
	 * Gets the rank for a specified event.
	 *
	 * @param e
	 *            the event.
	 * @return the rank.
	 */
	public int getRank(final Event e) {
		return events.size() - getPosition(e);
	}

	/**
	 * Gets the run this solution belongs to.
	 *
	 * @return the run.
	 */
	public Run getRun() {
		return run;
	}

	/**
	 * Gets the score for this solution.
	 *
	 * @return the score.
	 */
	public double getScore() {
		return score;
	}

	/**
	 * Generates a hash representation of this solution. This hash is only
	 * guaranteed to be unique for a single run.
	 *
	 * @return the hash.
	 */
	public String hash() {
		StringBuilder hash = new StringBuilder();
		for (Event e : events) {
			hash.append(e);
			hash.append(':');
		}
		return hash.toString();
	}

	/**
	 * Sets the score for this solution.
	 *
	 * @param score
	 *            the score.
	 */
	public void setScore(final double score) {
		this.score = score;
	}
}
