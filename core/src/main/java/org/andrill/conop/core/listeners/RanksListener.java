package org.andrill.conop.core.listeners;

import java.util.Arrays;

import org.andrill.conop.core.Event;
import org.andrill.conop.core.Dataset;
import org.andrill.conop.core.Solution;

public class RanksListener extends AbstractListener {
	protected double min = Double.MAX_VALUE;
	protected double[][] ranks;
	protected Dataset dataset;

	/**
	 * Gets the max rank for the specified event.
	 *
	 * @param e
	 *            the event.
	 * @return the max rank.
	 */
	public int getMaxRank(final Event e) {
		double[] array = ranks[dataset.getId(e)];
		if (array == null) {
			return -1;
		}

		int i = 0;
		while ((i < array.length) && (array[i] != min)) {
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
		double[] array = ranks[dataset.getId(e)];
		if (array == null) {
			return -1;
		}

		int i = array.length - 1;
		while ((i >= 0) && (array[i] != min)) {
			i--;
		}
		return array.length - i;
	}

	protected void initialize(final Dataset dataset) {
		this.dataset = dataset;

		// pre-populate our matrix
		int events = dataset.getEvents().size();
		ranks = new double[events][events];
		for (int i = 0; i < events; i++) {
			Arrays.fill(ranks[i], -1);
		}
	}

	@Override
	public void tried(final double temp, final Solution current, final Solution best) {
		if (dataset == null) {
			initialize(best.getDataset());
		}

		double score = best.getScore();
		if (score <= min) {
			for (Event e : best.getEvents()) {
				ranks[dataset.getId(e)][best.getPosition(e)] = score;
			}
		}
	}
}
