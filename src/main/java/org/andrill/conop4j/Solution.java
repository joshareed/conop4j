package org.andrill.conop4j;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * A solution contains a set of events in a particular order.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class Solution {
	protected final ImmutableList<Event> events;
	protected final ImmutableMap<Event, Integer> positions;
	protected final Run run;
	protected double score = 0.0;

	/**
	 * Create a new Solution with the specified ordered list of events.
	 * 
	 * @param events
	 *            the list of events.
	 */
	public Solution(final Run run, final List<Event> events) {
		this.events = ImmutableList.copyOf(events);
		this.run = run;

		// index our events
		Builder<Event, Integer> b = ImmutableMap.builder();
		for (int i = 0; i < events.size(); i++) {
			b.put(events.get(i), i);
		}
		positions = b.build();
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
	 * Sets the score for this solution.
	 * 
	 * @param score
	 *            the score.
	 */
	public void setScore(final double score) {
		this.score = score;
	}
}
