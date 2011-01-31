package org.andrill.conop4j.objectives;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.andrill.conop4j.Event;
import org.andrill.conop4j.Observation;
import org.andrill.conop4j.Section;
import org.andrill.conop4j.Solution;

import com.google.common.collect.Lists;

/**
 * Scores a solution for a particular section using the matrix method.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class SectionMatrix {
	private static final Comparator<BigDecimal> REVERSE = new Comparator<BigDecimal>() {
		@Override
		public int compare(final BigDecimal o1, final BigDecimal o2) {
			return -1 * o1.compareTo(o2);
		}
	};
	protected final List<BigDecimal> levels;
	protected double[][] matrix;
	protected double[][] penalties;
	protected final Section section;

	/**
	 * Create a new SectionMatrix.
	 * 
	 * @param section
	 *            the section.
	 */
	public SectionMatrix(final Section section) {
		this.section = section;

		// get our sorted levels
		levels = Lists.newArrayList(section.getLevels());
		Collections.sort(levels, REVERSE);
	}

	/**
	 * Score the solution against this section.
	 * 
	 * @param solution
	 *            the solution.
	 * @return the penalty.
	 */
	public double score(final Solution solution) {
		int eventCount = solution.getEvents().size();
		int levelCount = levels.size();

		// populate our penalties matrix the first time
		if (penalties == null) {
			penalties = new double[eventCount][levelCount];
			for (Event e : solution.getEvents()) {
				int i = e.getInternalId();
				Observation o = section.getObservation(e);
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
		if (matrix == null) {
			matrix = new double[eventCount][levels.size()];
		}

		// initialize our score matrix with the penalties
		for (int i = 0; i < eventCount; i++) {
			Event e = solution.getEvent(i);
			System.arraycopy(penalties[e.getInternalId()], 0, matrix[i], 0, levelCount);
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
