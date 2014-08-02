package org.andrill.conop.core;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A solution contains a set of events in a particular order.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class Solution {

	/**
	 * Creates an initial solution.
	 *
	 * @param dataset
	 *            the dataset.
	 * @return the initial solution.
	 */
	public static Solution initial(final Dataset dataset) {
		final List<Event> events = Lists.newArrayList(dataset.getEvents());
		return new Solution(dataset, events);
	}

	protected final ImmutableList<Event> events;
	protected final Map<Event, Integer> positions;
	protected final Dataset dataset;
	protected double score = -1.0;

	/**
	 * Create a new Solution with the specified ordered list of events.
	 *
	 * @param dataset
	 *            the dataset.
	 * @param events
	 *            the list of events.
	 */
	public Solution(final Dataset dataset, final List<Event> events) {
		this.events = ImmutableList.copyOf(events);
		this.dataset = dataset;

		// index our events
		positions = Maps.newHashMap();
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
	 * Gets the dataset this solution belongs to.
	 *
	 * @return the dataset.
	 */
	public Dataset getDataset() {
		return dataset;
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
		if (this.score >= 0.0) {
			throw new RuntimeException("Solution may only be scored once");
		}
		this.score = score;
	}
}
