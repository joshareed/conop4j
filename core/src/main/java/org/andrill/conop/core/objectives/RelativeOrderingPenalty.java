package org.andrill.conop.core.objectives;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.andrill.conop.core.Event;
import org.andrill.conop.core.Observation;
import org.andrill.conop.core.Run;
import org.andrill.conop.core.Section;
import org.andrill.conop.core.Solution;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class RelativeOrderingPenalty extends AbstractParallelPenalty {
	public static class EventOrdering implements Penalty {
		protected Event event;
		protected int[][] penalties;

		public EventOrdering(final Event event, final Run run) {
			this.event = event;

			// initialize our penalties
			ImmutableSet<Event> events = run.getEvents();
			penalties = new int[events.size()][2];

			for (Event e : events) {
				int i = run.getId(e);
				penalties[i][0] = 0;
				penalties[i][1] = 0;

				for (Section s : run.getSections()) {
					Observation o1 = s.getObservation(event);
					Observation o2 = s.getObservation(e);

					if ((o1 != null) && (o2 != null)) {
						if (o1.getLevel().compareTo(o2.getLevel()) < 0) {
							penalties[i][0]++;
						} else if (o1.getLevel().compareTo(o2.getLevel()) > 0) {
							penalties[i][1]++;
						}
					}
				}
			}
		}

		@Override
		public double score(final Solution solution) {
			int score = 0;

			int me = solution.getRank(event);
			for (Event e : solution.getEvents()) {
				int them = solution.getRank(e);
				int id = solution.getRun().getId(e);
				if (me < them) {
					score += penalties[id][1];
				} else if (me > them) {
					score += penalties[id][0];
				}
			}

			return score;
		}
	}

	protected Map<Event, EventOrdering> orderings = null;

	public RelativeOrderingPenalty() {
		super("Relative Ordering");
	}

	@Override
	public void initialize(final Run run) {
		orderings = Maps.newHashMap();
		for (Event e : run.getEvents()) {
			orderings.put(e, new EventOrdering(e, run));
		}
	}

	@Override
	protected List<Future<Double>> internalScore(final Solution solution) {
		List<Future<Double>> results = Lists.newArrayList();
		for (Event e : solution.getEvents()) {
			results.add(execute(orderings.get(e), solution));
		}
		return results;
	}
}
