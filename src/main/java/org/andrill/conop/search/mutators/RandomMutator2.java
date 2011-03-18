package org.andrill.conop.search.mutators;

import java.util.List;
import java.util.Random;

import org.andrill.conop.search.Event;
import org.andrill.conop.search.Solution;

import com.google.common.collect.Lists;

public class RandomMutator2 extends AbstractMutator {
	protected Random random = new Random();

	@Override
	protected Solution internalMutate(final Solution solution) {
		List<Event> events = Lists.newArrayList(solution.getEvents());

		// pick a random event and move it to a new position randomly
		int cur = random.nextInt(events.size());
		int range = ((int) Math.log(temp) + 1) * 2;
		int diff = random.nextInt(2 * range) - range;

		// build a new solution
		Event e = events.remove(cur);
		events.add(Math.min(events.size(), Math.max(0, cur + diff)), e);
		return new Solution(solution.getRun(), events);
	}

	@Override
	public String toString() {
		return "Random2";
	}
}
