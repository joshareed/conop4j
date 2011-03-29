package org.andrill.conop.search.mutators;

import java.util.List;
import java.util.Random;

import org.andrill.conop.search.Event;
import org.andrill.conop.search.Solution;

import com.google.common.collect.Lists;

public class RandomMutator2 extends AbstractMutator {
	protected List<Event> first;
	protected int ptr = -1;
	protected Random random = new Random();
	protected double temp;

	@Override
	protected Solution internalMutate(final Solution solution) {
		if (ptr == -1) {
			ptr = 0;
			first = solution.getEvents();
		}

		// get our events
		List<Event> events = Lists.newArrayList(solution.getEvents());

		// remove our event
		int i = events.indexOf(first.get(ptr));
		Event e = events.remove(i);

		// figure out our range
		int range = (int) Math.ceil(temp / 10) + 1;
		int diff = random.nextInt(2 * range) - range;

		// build a new solution
		events.add(Math.min(events.size(), Math.max(0, i + diff)), e);
		return new Solution(solution.getRun(), events);
	}

	@Override
	public String toString() {
		return "Random2";
	}

	@Override
	public void tried(final double temp, final Solution current, final Solution best) {
		super.tried(temp, current, best);
		this.temp = temp;
		ptr = (ptr + 1) % first.size();
	}
}
