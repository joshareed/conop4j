package org.andrill.conop4j;

import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Models coexistence relationships among events.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CoexistenceMatrix {
	public enum Coexistence {
		ABSENT, CONJUNCT, DISJUNCT, MIXED
	}

	protected final ImmutableMap<Event, Integer> events;
	protected final Coexistence[][] matrix;

	/**
	 * Creates a new coexistence matrix for the specified run.
	 * 
	 * @param run
	 *            the run.
	 */
	public CoexistenceMatrix(final Run run) {
		// index our events
		int i = 0;
		Builder<Event, Integer> eventBuilder = ImmutableMap.builder();
		for (Event e : run.getEvents()) {
			eventBuilder.put(e, i++);
		}
		events = eventBuilder.build();

		// create our matrix
		matrix = new Coexistence[events.size()][events.size()];

		// populate the matrix
		populateMatrix(run);
	}

	/**
	 * Create a new coexistence matrix for the specified section and list of all
	 * events.
	 * 
	 * @param section
	 *            the section.
	 * @param allEvents
	 *            the list of all events.
	 */
	public CoexistenceMatrix(final Section section, final List<Event> allEvents) {
		// index our events
		int i = 0;
		Builder<Event, Integer> eventBuilder = ImmutableMap.builder();
		for (Event e : allEvents) {
			eventBuilder.put(e, i++);
		}
		events = eventBuilder.build();

		// create our matrix
		matrix = new Coexistence[events.size()][events.size()];

		// populate the matrix
		populateMatrix(section);
	}

	/**
	 * Create a new coexistence matrix for the specified solution.
	 * 
	 * @param solution
	 *            the solution.
	 */
	public CoexistenceMatrix(final Solution solution) {
		// index our events
		int i = 0;
		Builder<Event, Integer> eventBuilder = ImmutableMap.builder();
		for (Event e : solution.getRun().getEvents()) {
			eventBuilder.put(e, i++);
		}
		events = eventBuilder.build();

		// create our matrix
		matrix = new Coexistence[events.size()][events.size()];

		// populate the matrix
		populateMatrix(solution);
	}

	private Coexistence computeCoexistence(final Event e1, final Event e2, final Section s) {
		Observation start1 = s.getObservation(e1.getAfterConstraint() == null ? e1 : e1.getAfterConstraint());
		Observation start2 = s.getObservation(e2.getAfterConstraint() == null ? e2 : e2.getAfterConstraint());
		Observation end1 = s.getObservation(e1.getBeforeConstraint() == null ? e1 : e1.getBeforeConstraint());
		Observation end2 = s.getObservation(e2.getBeforeConstraint() == null ? e2 : e2.getBeforeConstraint());
		if ((start1 == null) || (start2 == null) || (end1 == null) || (end2 == null)) {
			return Coexistence.ABSENT;
		} else if ((start1.getLevel().compareTo(end2.getLevel()) >= 0)
				&& (start2.getLevel().compareTo(end1.getLevel()) >= 0)) {
			return Coexistence.CONJUNCT;
		} else {
			return Coexistence.DISJUNCT;
		}
	}

	private Coexistence computeCoexistence(final Event e1, final Event e2, final Solution s) {
		int start1 = s.getPosition(e1.getAfterConstraint() == null ? e1 : e1.getAfterConstraint());
		int start2 = s.getPosition(e2.getAfterConstraint() == null ? e2 : e2.getAfterConstraint());
		int end1 = s.getPosition(e1.getBeforeConstraint() == null ? e1 : e1.getBeforeConstraint());
		int end2 = s.getPosition(e2.getBeforeConstraint() == null ? e2 : e2.getBeforeConstraint());
		if ((start1 == -1) || (start2 == -1) || (end1 == -1) || (end2 == -1)) {
			return Coexistence.ABSENT;
		} else if ((start1 < end2) && (start2 < end1)) {
			return Coexistence.CONJUNCT;
		} else {
			return Coexistence.DISJUNCT;
		}
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
		return matrix[events.get(e1)][events.get(e2)];
	}

	protected void populateMatrix(final Run run) {
		for (Entry<Event, Integer> e1 : events.entrySet()) {
			for (Entry<Event, Integer> e2 : events.entrySet()) {
				boolean first = true;
				for (Section s : run.getSections()) {
					Coexistence coex = computeCoexistence(e1.getKey(), e2.getKey(), s);
					if (first) {
						first = false;
						matrix[e1.getValue()][e2.getValue()] = coex;
					} else if ((coex != Coexistence.ABSENT) && (matrix[e1.getValue()][e2.getValue()] != coex)) {
						matrix[e1.getValue()][e2.getValue()] = Coexistence.MIXED;
					}
					if (matrix[e1.getValue()][e2.getValue()] == Coexistence.MIXED) {
						break;
					}
				}
			}
		}
	}

	protected void populateMatrix(final Section section) {
		for (Entry<Event, Integer> e1 : events.entrySet()) {
			for (Entry<Event, Integer> e2 : events.entrySet()) {
				matrix[e1.getValue()][e2.getValue()] = computeCoexistence(e1.getKey(), e2.getKey(), section);
			}
		}
	}

	protected void populateMatrix(final Solution solution) {
		for (Entry<Event, Integer> e1 : events.entrySet()) {
			for (Entry<Event, Integer> e2 : events.entrySet()) {
				matrix[e1.getValue()][e2.getValue()] = computeCoexistence(e1.getKey(), e2.getKey(), solution);
			}
		}
	}
}
