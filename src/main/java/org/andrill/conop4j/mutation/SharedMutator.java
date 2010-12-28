package org.andrill.conop4j.mutation;

import java.util.List;
import java.util.Random;

import org.andrill.conop4j.CONOP.Listener;
import org.andrill.conop4j.Event;
import org.andrill.conop4j.Solution;

import com.google.common.collect.Lists;

public class SharedMutator implements MutationStrategy, Listener {
	protected Random random = new Random();
	protected Solution shared;

	@Override
	public Solution mutate(final Solution solution) {
		if ((shared != null) && (shared.getScore() < 0.75 * solution.getScore())) {
			return new Solution(shared.getRun(), shared.getEvents());
		} else {
			List<Event> events = Lists.newArrayList(solution.getEvents());

			// pick a random event and move it to a new position randomly
			int cur = random.nextInt(events.size());
			int pos = random.nextInt(events.size());
			while (pos == cur) {
				pos = random.nextInt(events.size());
			}

			// build a new solution
			Event e = events.remove(cur);
			events.add(pos, e);
			return new Solution(solution.getRun(), events);
		}
	}

	@Override
	public void tried(final double temp, final Solution current, final Solution best) {
		if ((shared == null) || (best.getScore() < shared.getScore())) {
			shared = best;
		}
	}
}
