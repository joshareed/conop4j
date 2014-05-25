package org.andrill.conop.core;

/**
 * Represents an Event.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class Event {
	private static int ID = 0;

	/**
	 * Create a paired event.
	 * 
	 * @param name1
	 *            the first event.
	 * @param name2
	 *            the second event.
	 * @return the first event which has the second event set as its before
	 *         constraint.
	 */
	public static Event createPaired(final String name1, final String name2) {
		Event first = new Event(name1);
		Event second = new Event(name2);
		first.before = second;
		second.after = first;
		return first;
	}

	protected Event after;
	protected Event before;
	protected final int id;
	protected final String name;

	/**
	 * Create a new event with no constraints.
	 * 
	 * @param name
	 *            the name.
	 */
	public Event(final String name) {
		this(name, null, null);
	}

	/**
	 * Create a new event with the specified constraints.
	 * 
	 * @param name
	 *            the name.
	 * @param before
	 *            the before constraint.
	 * @param after
	 *            the after constraint.
	 */
	public Event(final String name, final Event before, final Event after) {
		this.name = name;
		this.before = before;
		this.after = after;
		id = ID++;
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
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the event this event must appear after in a valid solution.
	 * 
	 * @return the event or null if not constrained.
	 */
	public Event getAfterConstraint() {
		return after;
	}

	/**
	 * Gets the event this event must appear before in a valid solution.
	 * 
	 * @return the event or null if not constrained.
	 */
	public Event getBeforeConstraint() {
		return before;
	}

	/**
	 * Gets the internal id of this event.
	 * 
	 * @return the internal id.
	 */
	public int getInternalId() {
		return id;
	}

	/**
	 * Gets the name of this event.
	 * 
	 * @return the name.
	 */
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
