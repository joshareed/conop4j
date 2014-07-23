package org.andrill.conop.core.internal;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.andrill.conop.core.Event;
import org.andrill.conop.core.Location;
import org.andrill.conop.core.Observation;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * Represents a location.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class DefaultLocation implements Location {
	protected final Map<Event, Observation> events;
	protected final ImmutableMultimap<BigDecimal, Observation> levels;
	protected final String name;
	protected final ImmutableSet<Observation> observations;

	/**
	 * Create a new location.
	 *
	 * @param name
	 *            the name.
	 * @param list
	 *            the list of observations.
	 */
	public DefaultLocation(final String name, final List<Observation> list) {
		this.name = name;
		observations = ImmutableSet.copyOf(list);
		events = Maps.newHashMap();

		// index the observations by event and level
		ImmutableMultimap.Builder<BigDecimal, Observation> levelBuilder = ImmutableMultimap.builder();
		for (Observation o : list) {
			events.put(o.getEvent(), o);
			levelBuilder.put(o.getLevel(), o);
		}
		levels = levelBuilder.orderKeysBy(new Comparator<BigDecimal>() {
			@Override
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
		Location other = (Location) obj;
		if (name == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!name.equals(other.getName())) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.andrill.conop.core.Location#getEvents()
	 */
	@Override
	public ImmutableSet<Event> getEvents() {
		return ImmutableSet.copyOf(events.keySet());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.andrill.conop.core.Location#getLevels()
	 */
	@Override
	public ImmutableSet<BigDecimal> getLevels() {
		return levels.keySet();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.andrill.conop.core.Location#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.andrill.conop.core.Location#getObservation(org.andrill.conop.core.
	 * Event)
	 */
	@Override
	public Observation getObservation(final Event event) {
		return events.get(event);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.andrill.conop.core.Location#getObservations()
	 */
	@Override
	public ImmutableSet<Observation> getObservations() {
		return observations;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return name;
	}
}
