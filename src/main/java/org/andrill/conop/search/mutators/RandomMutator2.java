package org.andrill.conop.search.mutators;

import java.util.List;
import java.util.Random;

import org.andrill.conop.search.Event;
import org.andrill.conop.search.Solution;

import com.google.common.collect.Lists;

public class RandomMutator2 extends AbstractMutator {
	protected List<Event> first;
	protected boolean increment = true;
	protected int ptr = 0;
	protected Random random = new Random();
	protected double score = Double.MAX_VALUE;
	protected boolean tried = false;

	@Override
	protected Solution internalMutate(final Solution solution) {
		if (first == null) {
			first = solution.getEvents();
		}

		// get our events
		List<Event> events = Lists.newArrayList(solution.getEvents());

		// remove our event
		int i = events.indexOf(first.get(ptr));
		Event e = events.remove(i);
		if (increment) {
			i++;
		} else {
			i--;
		}
		if ((i < 0) || (i > events.size() - 1) || !tried) {
			ptr = (ptr + 1) % first.size();
			increment = random.nextBoolean();
		}
		events.add(Math.max(0, Math.min(events.size(), i)), e);

		tried = false;
		return new Solution(solution.getRun(), events);
	}

	@Override
	public String toString() {
		return "Random2";
	}

	@Override
	public void tried(final double temp, final Solution current, final Solution best) {
		super.tried(temp, current, best);
		if (current.getScore() >= score) {
			ptr = (ptr + 1) % first.size();
			increment = random.nextBoolean();
		}
		score = current.getScore();
		tried = true;
	}
}
