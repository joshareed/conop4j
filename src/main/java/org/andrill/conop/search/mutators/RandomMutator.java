package org.andrill.conop.search.mutators;

import java.util.List;
import java.util.Random;

import org.andrill.conop.search.AbstractConfigurable;
import org.andrill.conop.search.Event;
import org.andrill.conop.search.Solution;

import com.google.common.collect.Lists;

/**
 * Randomly moves an event with no consideration to constraints.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class RandomMutator extends AbstractConfigurable implements
		MutationStrategy {
	protected Random random = new Random();

	@Override
	public Solution mutate(final Solution solution) {
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

	@Override
	public String toString() {
		return "Random";
	}
}
