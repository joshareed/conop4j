package org.andrill.conop.core.listeners;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

import org.andrill.conop.core.Dataset;
import org.andrill.conop.core.Event;
import org.andrill.conop.core.Solution;

public class PositionsMatrix {
	protected double[][] positions;
	protected Dataset dataset;
	protected double best = Double.MAX_VALUE;
	protected ReentrantLock lock = new ReentrantLock();

	public PositionsMatrix(Dataset dataset) {
		this.dataset = dataset;

		// initialize the arrays
		int size = dataset.getEvents().size();
		positions = new double[size][size];

		for (int i = 0; i < size; i++) {
			Arrays.fill(positions[i], -1);
		}
	}

	public void update(Solution solution) {
		double score = solution.getScore();
		if (score <= best) {
			try {
				lock.lock();

				if (score <= best) {
					best = score;
					for (Event e : solution.getEvents()) {
						positions[dataset.getId(e)][solution.getPosition(e)] = score;
					}
				}

			} finally {
				lock.unlock();
			}
		}
	}

	public int[] getRange(Event e, double score) {
		int[] range = new int[] { -1, -1 };

		double[] row = positions[dataset.getId(e)];
		for (int i = 0; i < row.length; i++) {
			double d = row[i];
			if (d <= score) {
				if (range[0] == -1) {
					range[0] = i;
				}
				range[1] = i;
			}
		}

		return range;
	}

	public int[] getRange(Event e) {
		return getRange(e, best);
	}
}
