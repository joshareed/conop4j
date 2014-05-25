package org.andrill.conop.core;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Represents a section.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class Section {
	protected final Map<Event, Observation> events;
	protected final ImmutableMultimap<BigDecimal, Observation> levels;
	protected final String name;
	protected final ImmutableSet<Observation> observations;

	/**
	 * Create a new section.
	 * 
	 * @param name
	 *            the name.
	 * @param list
	 *            the list of observations.
	 */
	public Section(final String name, final List<Observation> list) {
		this.name = name;
		observations = ImmutableSet.copyOf(list);
		events = new IdentityHashMap<Event, Observation>();

		// index the observations by event and level
		ImmutableMultimap.Builder<BigDecimal, Observation> levelBuilder = ImmutableMultimap.builder();
		for (Observation o : list) {
			events.put(o.getEvent(), o);
			levelBuilder.put(o.getLevel(), o);
		}
		levels = levelBuilder.orderKeysBy(new Comparator<BigDecimal>() {
			public int compare(final BigDecimal bd1, final BigDecimal bd2) {
				return bd1.compareTo(bd2);
			}
		}).build();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Section other = (Section) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the events in this section.
	 * 
	 * @return the set of events.
	 */
	public ImmutableSet<Event> getEvents() {
		return ImmutableSet.copyOf(events.keySet());
	}

	/**
	 * Gets the levels in this section.
	 * 
	 * @return the set of levels.
	 */
	public ImmutableSet<BigDecimal> getLevels() {
		return levels.keySet();
	}

	/**
	 * Gets the name of this section.
	 * 
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the observation for the specified event.
	 * 
	 * @param event
	 *            the event.
	 * @return the observation.
	 */
	public Observation getObservation(final Event event) {
		return events.get(event);
	}

	/**
	 * Gets the observations.
	 * 
	 * @return the observation
	 */
	public ImmutableSet<Observation> getObservations() {
		return observations;
	}

	/**
	 * Get all observations at the specified level.
	 * 
	 * @param level
	 *            the level.
	 * @return the list of observations.
	 */
	public ImmutableSet<Observation> getObservations(final BigDecimal level) {
		return ImmutableSet.copyOf(levels.get(level));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return name;
	}
}
