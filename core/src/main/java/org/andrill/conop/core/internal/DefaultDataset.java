package org.andrill.conop.core.internal;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.andrill.conop.core.Dataset;
import org.andrill.conop.core.Event;
import org.andrill.conop.core.Location;
import org.andrill.conop.core.Observation;
import org.andrill.conop.core.util.IdentityOptimizedMap;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Represents a CONOP dataset.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class DefaultDataset implements Dataset {
	protected final ImmutableSet<Event> events;
	protected final ImmutableSet<Location> locations;
	protected final Map<Event, Integer> ids = new IdentityOptimizedMap<Event, Integer>();

	/**
	 * Create a new dataset from the specified locations.
	 *
	 * @param list
	 *            the locations.
	 */
	public DefaultDataset(final List<Location> list) {
		locations = ImmutableSet.copyOf(canonicalize(list));

		// build our immutable set of events
		Builder<Event> b = ImmutableSet.builder();
		for (Location s : locations) {
			b.addAll(s.getEvents());
		}
		events = b.build();

		// assign ids to events
		int id = 0;
		for (Event e : events) {
			ids.put(e, id++);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andrill.conop.core.Dataset#getEvents()
	 */
	@Override
	public ImmutableSet<Event> getEvents() {
		return events;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andrill.conop.core.Dataset#getId(org.andrill.conop.core.Event)
	 */
	@Override
	public int getId(final Event e) {
		return ids.get(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andrill.conop.core.Dataset#getLocations()
	 */
	@Override
	public ImmutableSet<Location> getLocations() {
		return locations;
	}

	protected List<Location> canonicalize(List<Location> list) {
		Map<Event, Event> unique = Maps.newHashMap();
		List<Location> cloned = Lists.newArrayList();
		for (Location l : list) {
			List<Observation> observations = Lists.newArrayList();
			for (Observation o : l.getObservations()) {
				Event canonical = unique.get(o.getEvent());
				if (canonical == null) {
					canonical = o.getEvent();
					unique.put(canonical, canonical);
				}
				observations.add(new DefaultObservation(canonical, o.getLevel(), o.getWeightUp(), o.getWeightDown()));
			}
			cloned.add(new DefaultLocation(l.getName(), observations));
		}
		return cloned;
	}

	public boolean isCanonical() {
		// build our identity map
		Map<Event, Event> identity = new IdentityHashMap<>();
		for (Event e : events) {
			identity.put(e, e);
		}
		boolean canonical = true;

		for (Location l : locations) {
			for (Observation o : l.getObservations()) {
				canonical &= identity.containsKey(o.getEvent());
			}
		}
		return canonical;
	}
}
