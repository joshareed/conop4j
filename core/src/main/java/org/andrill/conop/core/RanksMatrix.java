package org.andrill.conop.core;

import java.util.Arrays;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Tracks the min and max rank for events.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class RanksMatrix {
	protected double best = Double.MAX_VALUE;
	protected final double[][] ranks;
	protected final ImmutableMap<Event, Integer> ids;

	public RanksMatrix(final Run run) {
		// pre-populate our list
		int events = run.getEvents().size();
		ranks = new double[events][events];
		for (int i = 0; i < events; i++) {
			Arrays.fill(ranks[i], -1);
		}

		// build our id map
		Builder<Event, Integer> builder = ImmutableMap.builder();
		for (Event e : run.getEvents()) {
			builder.put(e, run.getId(e));
		}
		ids = builder.build();
	}

	/**
	 * Gets the max rank for the specified event.
	 *
	 * @param e
	 *            the event.
	 * @return the max rank.
	 */
	public int getMaxRank(final Event e) {
		double[] array = ranks[ids.get(e)];
		if (array == null) {
			return -1;
		}

		int i = 0;
		while ((i < array.length) && (array[i] != best)) {
			i++;
		}
		return array.length - i;
	}

	/**
	 * Gets the min rank for the specified event.
	 *
	 * @param e
	 *            the event.
	 * @return the min rank.
	 */
	public int getMinRank(final Event e) {
		double[] array = ranks[ids.get(e)];
		if (array == null) {
			return -1;
		}

		int i = array.length - 1;
		while ((i >= 0) && (array[i] != best)) {
			i--;
		}
		return array.length - i;
	}

	/**
	 * Update the ranks matrix with the specified scored solution.
	 *
	 * @param solution
	 *            the solution.
	 */
	public void update(final Solution solution) {
		double score = solution.getScore();
		if (score < best) {
			for (Event e : solution.getEvents()) {
				ranks[ids.get(e)][solution.getPosition(e)] = score;
			}
		}
	}
}
