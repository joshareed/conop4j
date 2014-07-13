package org.andrill.conop.core.objectives;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.Future;

import org.andrill.conop.core.Event;
import org.andrill.conop.core.Observation;
import org.andrill.conop.core.Run;
import org.andrill.conop.core.Section;
import org.andrill.conop.core.Solution;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A parallel version of {@link PlacementPenalty}.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class PlacementPenalty extends AbstractParallelPenalty {
	public static class SectionPlacement implements Penalty {
		private static final IdentityHashMap<BigDecimal, Double> cache = new IdentityHashMap<BigDecimal, Double>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Double get(final Object key) {
				Double value = super.get(key);
				if (value == null) {
					BigDecimal k = (BigDecimal) key;
					value = k.doubleValue();
					put(k, value);
				}
				return value;
			};
		};
		private static final Comparator<BigDecimal> REVERSE = new Comparator<BigDecimal>() {
			@Override
			public int compare(final BigDecimal o1, final BigDecimal o2) {
				return -1 * o1.compareTo(o2);
			}
		};
		protected int head = 0;
		protected final Map<BigDecimal, Integer> levelMap;
		protected final List<BigDecimal> levels;
		protected final Map<BigDecimal, List<Event>> placed;
		protected final Section section;

		/**
		 * Create a new section placement for the specified section.
		 *
		 * @param section
		 *            the section.
		 */
		public SectionPlacement(final Section section) {
			this.section = section;

			// get our sorted levels
			levels = Lists.newArrayList(section.getLevels());
			Collections.sort(levels, REVERSE);

			// create our placement object
			placed = new TreeMap<BigDecimal, List<Event>>(REVERSE);
			levelMap = Maps.newTreeMap();
			int i = 0;
			for (BigDecimal l : levels) {
				placed.put(l, new ArrayList<Event>());
				levelMap.put(l, i++);
			}
			head = 0;
		}

		/**
		 * Gets the placement penalty for this section.
		 *
		 * @return the penalty.
		 */
		public double getPenalty() {
			double penalty = 0;
			for (Entry<BigDecimal, List<Event>> entry : placed.entrySet()) {
				for (Event e : entry.getValue()) {
					penalty += getPenalty(e, entry.getKey());
				}
			}
			return penalty;
		}

		protected double getPenalty(final Event event, final BigDecimal level) {
			Observation o = section.getObservation(event);
			if (o == null) {
				return 0;
			}

			double diff = cache.get(level) - cache.get(o.getLevel());
			if (diff < 0) {
				return Math.abs(diff) * o.getWeightDown();
			} else {
				return Math.abs(diff) * o.getWeightUp();
			}
		}

		/**
		 * Gets the placement level for the specified event.
		 *
		 * @param e
		 *            the event.
		 * @return the placement level.
		 */
		public BigDecimal getPlacement(final Event e) {
			for (Entry<BigDecimal, List<Event>> entry : placed.entrySet()) {
				if (entry.getValue().contains(e)) {
					return entry.getKey();
				}
			}
			return null;
		}

		protected double getShiftPenalty() {
			double current = 0.0;
			double shifted = 0.0;
			for (Event e : placed.get(levels.get(head))) {
				current += getPenalty(e, levels.get(head));
				shifted += getPenalty(e, levels.get(Math.max(0, head - 1)));
			}
			return Math.abs(current - shifted);
		}

		/**
		 * Place the specified event.
		 *
		 * @param e
		 *            the event.
		 */
		public void place(final Event e) {
			Observation o = section.getObservation(e);
			if (o == null) {
				placed.get(levels.get(head)).add(e);
			} else {
				// find the optimal position
				int optimal = levelMap.get(o.getLevel());

				// if it is below the current position, place it there
				if (optimal >= head) {
					placed.get(o.getLevel()).add(e);
					head = optimal;
				} else {
					// calculate the cost of placing it at the current head and
					// the
					// cost of shifting the head placements up one level
					double placePenalty = getPenalty(e, levels.get(head));
					double shiftPenalty = getShiftPenalty();
					while ((placePenalty > shiftPenalty) && (head > 0)) {
						// shift up
						List<Event> shifting = placed.get(levels.get(head));
						placed.get(levels.get(head - 1)).addAll(0, shifting);
						shifting.clear();
						head--;

						// re-calculate our penalties
						placePenalty = getPenalty(e, levels.get(head));
						shiftPenalty = getShiftPenalty();
					}
					placed.get(levels.get(head)).add(e);
				}
			}
		}

		/**
		 * Resets the placement.
		 */
		public void reset() {
			for (Entry<BigDecimal, List<Event>> e : placed.entrySet()) {
				e.getValue().clear();
			}
		}

		/**
		 * Scores the specified solution against this section.
		 *
		 * @param solution
		 *            the solution.
		 * @return the score.
		 */
		@Override
		public double score(final Solution solution) {
			reset();
			for (Event e : solution.getEvents()) {
				place(e);
			}
			return getPenalty();
		}
	}

	protected Map<Section, SectionPlacement> placements = null;

	public PlacementPenalty() {
		super("Placement");
	}

	@Override
	public void initialize(final Run run) {
		placements = Maps.newHashMap();
		for (final Section section : run.getSections()) {
			placements.put(section, new SectionPlacement(section));
		}
	}

	@Override
	protected List<Future<Double>> internalScore(final Solution solution) {
		List<Future<Double>> results = Lists.newArrayList();
		for (final Section section : solution.getRun().getSections()) {
			results.add(execute(placements.get(section), solution));
		}
		return results;
	}
}
