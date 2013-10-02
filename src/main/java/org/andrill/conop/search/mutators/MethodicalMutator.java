package org.andrill.conop.search.mutators;

import java.util.List;
import java.util.Map;

import org.andrill.conop.search.Event;
import org.andrill.conop.search.Simulation;
import org.andrill.conop.search.Solution;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MethodicalMutator extends AbstractMutator {
	protected Map<Integer, Event> map = Maps.newHashMap();
	protected int current = 0;
	protected int cycle = 0;

	public MethodicalMutator() {
		super("Methodical");
	}

	@Override
	public void configure(final Simulation simulation) {
		for (Event e : simulation.getRun().getEvents()) {
			map.put(e.getInternalId(), e);
		}
	}

	@Override
	protected Solution internalMutate(final Solution solution) {
		// remove the event
		Event target = map.get(current);
		List<Event> events = Lists.newArrayList(solution.getEvents());
		int position = events.indexOf(target);
		events.remove(target);

		// increment event position
		position = (position + 1) % map.size();
		cycle = (cycle + 1) % map.size();
		if (cycle == 0) {
			current = (current + 1) % map.size();
		}

		// add the event back in
		events.add(position, target);

		return new Solution(solution.getRun(), events);
	}

}
