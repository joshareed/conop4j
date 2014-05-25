package org.andrill.conop.core.mutators;

import java.util.List;
import java.util.Random;

import org.andrill.conop.core.Event;
import org.andrill.conop.core.Solution;
import org.andrill.conop.core.listeners.Listener;

import com.google.common.collect.Lists;

/**
 * Randomly move an event with no consideration to constraints. The possible
 * distance an event can move reduces as the temperature of the simulation
 * decreases.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class AnnealingMutator extends AbstractMutator implements Listener {
	private int delta = 1;
	protected Random random = new Random();
	private double temp = 1000.0;

	public AnnealingMutator() {
		super("Annealing");
	}

	@Override
	protected Solution internalMutate(final Solution solution) {
		List<Event> events = Lists.newArrayList(solution.getEvents());

		// pick a random event and move it to a new position
		int cur = random.nextInt(events.size());
		int pos = random.nextInt(delta) + 1;
		if (random.nextInt(2) == 1) {
			pos = Math.min(events.size() - 1, cur + pos);
		} else {
			pos = Math.max(0, cur - pos);
		}

		// build a new solution
		Event e = events.remove(cur);
		events.add(pos, e);
		return new Solution(solution.getRun(), events);
	}

	@Override
	public void tried(final double temp, final Solution current, final Solution best) {
		if (temp != this.temp) {
			this.temp = temp;
			delta = (int) Math.max(1, Math.ceil(Math.log(temp)));
		}
	}
}
