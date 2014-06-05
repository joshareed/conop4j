package org.andrill.conop.core;

import java.math.BigDecimal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

/**
 * Models coexistence relationships among events.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class CoexistenceMatrix {
	public static final int NONE = 0;
	public static final int DISJUNCT = 1;
	public static final int DISJUNCT_BEFORE = (1 << 1);
	public static final int DISJUNCT_AFTER = (1 << 2);
	public static final int CONJUNCT = (1 << 3);
	public static final int CONJUNCT_BEFORE = (1 << 4);
	public static final int CONJUNCT_AFTER = (1 << 5);
	public static final int CONJUNCT_CONTAINS = (1 << 6);
	public static final int CONJUNCT_CONTAINED = (1 << 7);
	public static final int MASK = (1 << 8) - 1;

	protected static int computeCoexistence(final Number start1, final Number end1, final Number start2, final Number end2) {

		// handle one or the other not present
		if ((start1 == null) || (start2 == null) || (end1 == null) || (end2 == null)) {
			return MASK; // should this be 0?
		}

		// convert to doubles
		double s1 = start1.doubleValue();
		double e1 = end1.doubleValue();
		double s2 = start2.doubleValue();
		double e2 = end2.doubleValue();

		int result = 0;
		if ((s1 <= e2) && (s2 <= e1)) {
			result |= CONJUNCT;
			if ((s1 <= s2) && (e1 >= e2)) {
				result |= CONJUNCT_CONTAINS;
			} else if ((s1 >= s2) && (e1 <= e2)) {
				result |= CONJUNCT_CONTAINED;
			} else if ((s1 <= s2) && (e1 <= e2)) {
				result |= CONJUNCT_BEFORE;
			} else if ((s1 >= s2) && (e1 >= e2)) {
				result |= CONJUNCT_AFTER;
			}
		} else {
			result |= DISJUNCT;
			if (s1 < s2) {
				result |= DISJUNCT_BEFORE;
			} else {
				result |= DISJUNCT_AFTER;
			}
		}
		return result;
	}

	protected final int[][] matrix;
	protected final ImmutableMap<Event, Integer> ids;

	/**
	 * Creates a new coexistence matrix for the specified run.
	 *
	 * @param run
	 *            the run.
	 */
	public CoexistenceMatrix(final Run run) {
		ids = initIds(run);

		// create our matrix
		matrix = initMatrix(run.events.size());

		// populate the matrix
		populateMatrix(run);
	}

	/**
	 * Create a new coexistence matrix for the specified solution.
	 *
	 * @param solution
	 *            the solution.
	 */
	public CoexistenceMatrix(final Solution solution) {
		Run run = solution.getRun();
		ids = initIds(run);

		// create our matrix
		matrix = initMatrix(run.events.size());

		// populate the matrix
		populateMatrix(solution);
	}

	protected int computeCoexistence(final Event e1, final Event e2, final Section s) {
		BigDecimal start1 = unwrap(s.getObservation(e1.getAfterConstraint() == null ? e1 : e1.getAfterConstraint()));
		BigDecimal start2 = unwrap(s.getObservation(e2.getAfterConstraint() == null ? e2 : e2.getAfterConstraint()));
		BigDecimal end1 = unwrap(s.getObservation(e1.getBeforeConstraint() == null ? e1 : e1.getBeforeConstraint()));
		BigDecimal end2 = unwrap(s.getObservation(e2.getBeforeConstraint() == null ? e2 : e2.getBeforeConstraint()));
		return computeCoexistence(start1, end1, start2, end2);
	}

	protected int computeCoexistence(final Event e1, final Event e2, final Solution s) {
		int start1 = s.getPosition(e1.getAfterConstraint() == null ? e1 : e1.getAfterConstraint());
		int start2 = s.getPosition(e2.getAfterConstraint() == null ? e2 : e2.getAfterConstraint());
		int end1 = s.getPosition(e1.getBeforeConstraint() == null ? e1 : e1.getBeforeConstraint());
		int end2 = s.getPosition(e2.getBeforeConstraint() == null ? e2 : e2.getBeforeConstraint());
		return computeCoexistence(start1, end1, start2, end2);
	}

	/**
	 * Get the coexistence value for the specified events.
	 *
	 * @param e1
	 *            the first event.
	 * @param e2
	 *            the second event.
	 * @return the coexistence value.
	 */
	public int getCoexistence(final Event e1, final Event e2) {
		return matrix[ids.get(e1)][ids.get(e2)];
	}

	protected ImmutableMap<Event, Integer> initIds(final Run run) {
		// build our id map
		Builder<Event, Integer> builder = ImmutableMap.builder();
		for (Event e : run.getEvents()) {
			builder.put(e, run.getId(e));
		}
		return builder.build();
	}

	private int[][] initMatrix(final int size) {
		int[][] matrix = new int[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				matrix[i][j] = MASK;
			}
		}
		return matrix;
	}

	protected void populateMatrix(final Run run) {
		ImmutableSet<Event> events = run.getEvents();
		for (Event e1 : events) {
			for (Event e2 : events) {
				for (Section s : run.getSections()) {
					matrix[ids.get(e1)][ids.get(e2)] &= computeCoexistence(e1, e2, s);
				}
				// if still null then assume ABSENT
				if (matrix[ids.get(e1)][ids.get(e2)] == MASK) {
					matrix[ids.get(e1)][ids.get(e2)] = 0;
				}
			}
		}
	}

	protected void populateMatrix(final Solution solution) {
		ImmutableList<Event> events = solution.getEvents();
		for (Event e1 : events) {
			for (Event e2 : events) {
				matrix[ids.get(e1)][ids.get(e2)] = computeCoexistence(e1, e2, solution);
			}
		}
	}

	protected BigDecimal unwrap(final Observation o) {
		return o == null ? null : o.getLevel();
	}
}
