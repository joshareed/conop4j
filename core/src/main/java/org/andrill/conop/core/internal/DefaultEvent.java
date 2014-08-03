package org.andrill.conop.core.internal;

import org.andrill.conop.core.Event;

/**
 * Represents an Event.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class DefaultEvent implements Event {

	protected final String name;

	/**
	 * Create a new event with no constraints.
	 *
	 * @param name
	 *            the name.
	 */
	public DefaultEvent(final String name) {
		this.name = name;
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
		Event other = (Event) obj;
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
	 * @see org.andrill.conop.core.Event#getName()
	 */
	@Override
	public String getName() {
		return name;
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
