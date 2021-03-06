package org.andrill.conop.core.penalties;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.andrill.conop.core.Dataset;
import org.andrill.conop.core.Event;
import org.andrill.conop.core.Location;
import org.andrill.conop.core.Observation;
import org.andrill.conop.core.Solution;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * An {@link Penalty} implementation that places events using cumulative
 * penalties in a matrix.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class MatrixPenalty extends AbstractParallelPenalty {

	private static class LocationMatrix implements Penalty {
		private static final Comparator<BigDecimal> REVERSE = new Comparator<BigDecimal>() {
			@Override
			public int compare(final BigDecimal o1, final BigDecimal o2) {
				return -1 * o1.compareTo(o2);
			}
		};
		protected final List<BigDecimal> levels;
		protected double[][] matrix;
		protected double[][] penalties;
		protected final Location location;
		protected Dataset dataset;

		/**
		 * Create a new LocationMatrix.
		 *
		 * @param location
		 *            the location.
		 */
		public LocationMatrix(final Location location, final Dataset dataset) {
			this.location = location;
			this.dataset = dataset;

			// get our sorted levels
			levels = Lists.newArrayList(location.getLevels());
			Collections.sort(levels, REVERSE);

			// initialize
			init(dataset);
		}

		private void init(final Dataset dataset) {
			int eventCount = dataset.getEvents().size();
			int levelCount = levels.size();

			// initialize our work matrix
			matrix = new double[eventCount][levels.size()];

			// initialize our penalty matrix
			penalties = new double[eventCount][levelCount];
			for (Event e : dataset.getEvents()) {
				int i = dataset.getId(e);
				Observation o = location.getObservation(e);
				for (int j = 0; j < levelCount; j++) {
					if (o == null) {
						penalties[i][j] = 0.0;
					} else {
						BigDecimal level = levels.get(j);
						double diff = level.subtract(o.getLevel()).doubleValue();
						if (diff > 0) {
							penalties[i][j] = diff * o.getWeightUp();
						} else {
							penalties[i][j] = -diff * o.getWeightDown();
						}
					}
				}
			}
		}

		/**
		 * Score the solution against this location.
		 *
		 * @param solution
		 *            the solution.
		 * @return the penalty.
		 */
		@Override
		public double score(final Solution solution) {
			int eventCount = solution.getEvents().size();
			int levelCount = levels.size();

			// initialize our score matrix with the penalties
			for (int i = 0; i < eventCount; i++) {
				Event e = solution.getEvent(i);
				System.arraycopy(penalties[dataset.getId(e)], 0, matrix[i], 0, levelCount);
			}

			// accumulate penalties
			for (int i = 1; i < eventCount; i++) {
				double min = matrix[i - 1][0];
				for (int j = 0; j < levelCount; j++) {
					min = Math.min(min, matrix[i - 1][j]);
					matrix[i][j] += min;
				}
			}

			// find minimum value in last column
			double best = Double.MAX_VALUE;
			for (int j = 0; j < levelCount; j++) {
				best = Math.min(best, matrix[eventCount - 1][j]);
			}

			return best;
		}

	}

	protected Map<Location, LocationMatrix> matrices = null;

	public MatrixPenalty() {
		super("Matrix Penalty");
	}

	@Override
	protected void initialize(final Dataset dataset) {
		matrices = Maps.newHashMap();
		for (Location location : dataset.getLocations()) {
			matrices.put(location, new LocationMatrix(location, dataset));
		}
	}

	@Override
	protected List<Future<Double>> internalScore(final Solution solution) {
		List<Future<Double>> results = Lists.newArrayList();
		for (Location location : context.getDataset().getLocations()) {
			results.add(execute(matrices.get(location), solution));
		}
		return results;
	}
}
