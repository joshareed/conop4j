package org.andrill.conop.core.mutators.internal;

import java.util.List;
import java.util.Map;

import org.andrill.conop.core.Event;
import org.andrill.conop.core.Dataset;
import org.andrill.conop.core.Solution;
import org.andrill.conop.core.mutators.AbstractMutator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MethodicalMutator extends AbstractMutator {
	protected Map<Integer, Event> cache;
	protected int current = 0;
	protected int cycle = 0;

	public MethodicalMutator() {
		super("Methodical");
	}

	protected void initCache(final Dataset dataset) {
		cache = Maps.newHashMap();
		for (Event e : dataset.getEvents()) {
			cache.put(dataset.getId(e), e);
		}
	}

	@Override
	protected Solution internalMutate(final Solution solution) {
		if (cache == null) {
			initCache(solution.getDataset());
		}

		// remove the event
		Event target = cache.get(current);
		List<Event> events = Lists.newArrayList(solution.getEvents());
		int position = events.indexOf(target);
		events.remove(target);

		// increment event position
		position = (position + 1) % cache.size();
		cycle = (cycle + 1) % cache.size();
		if (cycle == 0) {
			current = (current + 1) % cache.size();
		}

		// add the event back in
		events.add(position, target);

		return new Solution(solution.getDataset(), events);
	}

}
