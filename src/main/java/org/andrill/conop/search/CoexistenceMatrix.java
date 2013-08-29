package org.andrill.conop.search;

import java.math.BigDecimal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Models coexistence relationships among events.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CoexistenceMatrix {
	/**
	 * The Coexistence level.
	 */
	public enum Coexistence {
		/**
		 * One or both of the events are absent.
		 */
		ABSENT,

		/**
		 * The events overlap.
		 */
		CONJUNCT,

		/**
		 * The events do not overlap.
		 */
		DISJUNCT,

		/**
		 * The events overlap in some sections and do not overlap in others.
		 */
		MIXED;

		public Coexistence and(final Coexistence that) {
			if ((this == that) || (that == ABSENT)) {
				return this;
			} else {
				return MIXED;
			}
		}
	}

	protected static Coexistence computeCoexistence(final Number start1, final Number end1, final Number start2,
			final Number end2) {
		if ((start1 == null) || (start2 == null) || (end1 == null) || (end2 == null)) {
			return Coexistence.ABSENT;
		} else if ((start1.doubleValue() <= end2.doubleValue())
				&& (start2.doubleValue() <= end1.doubleValue())) {
			return Coexistence.CONJUNCT;
		} else {
			return Coexistence.DISJUNCT;
		}
	}

	protected final Coexistence[][] matrix;

	/**
	 * Creates a new coexistence matrix for the specified run.
	 * 
	 * @param run
	 *            the run.
	 */
	public CoexistenceMatrix(final Run run) {
		// create our matrix
		int events = run.events.size();
		matrix = new Coexistence[events][events];

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
		// create our matrix
		int events = solution.events.size();
		matrix = new Coexistence[events][events];

		// populate the matrix
		populateMatrix(solution);
	}

	protected Coexistence computeCoexistence(final Event e1, final Event e2, final Section s) {
		BigDecimal start1 = unwrap(s.getObservation(e1.getAfterConstraint() == null ? e1 : e1.getAfterConstraint()));
		BigDecimal start2 = unwrap(s.getObservation(e2.getAfterConstraint() == null ? e2 : e2.getAfterConstraint()));
		BigDecimal end1 = unwrap(s.getObservation(e1.getBeforeConstraint() == null ? e1 : e1.getBeforeConstraint()));
		BigDecimal end2 = unwrap(s.getObservation(e2.getBeforeConstraint() == null ? e2 : e2.getBeforeConstraint()));
		return computeCoexistence(start1, end1, start2, end2);
	}

	protected Coexistence computeCoexistence(final Event e1, final Event e2, final Solution s) {
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
	public Coexistence getCoexistence(final Event e1, final Event e2) {
		return matrix[e1.getInternalId()][e2.getInternalId()];
	}

	protected void populateMatrix(final Run run) {
		ImmutableSet<Event> events = run.getEvents();
		for (Event e1 : events) {
			for (Event e2 : events) {
				for (Section s : run.getSections()) {
					Coexistence computed = computeCoexistence(e1, e2, s);
					Coexistence existing = matrix[e1.getInternalId()][e2.getInternalId()];
					if (existing == null) {
						// only set if not absent
						if (computed != Coexistence.ABSENT) {
							matrix[e1.getInternalId()][e2.getInternalId()] = computed;
						}
					} else {
						matrix[e1.getInternalId()][e2.getInternalId()] = existing.and(computed);
					}
				}
				// if still null then assume ABSENT
				if (matrix[e1.getInternalId()][e2.getInternalId()] == null) {
					matrix[e1.getInternalId()][e2.getInternalId()] = Coexistence.ABSENT;
				}
			}
		}
	}

	protected void populateMatrix(final Solution solution) {
		ImmutableList<Event> events = solution.getEvents();
		for (Event e1 : events) {
			for (Event e2 : events) {
				matrix[e1.getInternalId()][e2.getInternalId()] = computeCoexistence(e1, e2, solution);
			}
		}
	}

	protected BigDecimal unwrap(final Observation o) {
		return o == null ? null : o.getLevel();
	}
}
