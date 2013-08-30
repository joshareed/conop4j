package org.andrill.conop.search.objectives;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

import org.andrill.conop.search.*;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class RelativeOrderingPenalty extends AbstractParallelObjective {
	public static class EventOrdering implements ObjectiveFunction {
		protected Event event;
		protected int[][] penalties;

		public EventOrdering(final Event event, final Run run) {
			this.event = event;

			// initialize our penalties
			ImmutableSet<Event> events = run.getEvents();
			penalties = new int[events.size()][2];

			for (Event e : events) {
				int i = e.getInternalId();
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
				if (me < them) {
					score += penalties[e.getInternalId()][1];
				} else if (me > them) {
					score += penalties[e.getInternalId()][0];
				}
			}

			return score;
		}
	}

	protected Map<Event, EventOrdering> orderings = Maps.newHashMap();

	public RelativeOrderingPenalty() {
		super("Relative Ordering");
	}

	@Override
	public void configure(final Properties properties, final Run run) {
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
