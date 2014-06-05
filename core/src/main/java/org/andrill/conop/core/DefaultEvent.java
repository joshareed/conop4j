package org.andrill.conop.core;

/**
 * Represents an Event.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class DefaultEvent implements Event {

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
	public static DefaultEvent createPaired(final String name1, final String name2) {
		DefaultEvent first = new DefaultEvent(name1);
		DefaultEvent second = new DefaultEvent(name2);
		first.before = second;
		second.after = first;
		return first;
	}

	protected DefaultEvent after;
	protected DefaultEvent before;
	protected final String name;

	/**
	 * Create a new event with no constraints.
	 *
	 * @param name
	 *            the name.
	 */
	public DefaultEvent(final String name) {
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
	public DefaultEvent(final String name, final DefaultEvent before, final DefaultEvent after) {
		this.name = name;
		this.before = before;
		this.after = after;
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
		DefaultEvent other = (DefaultEvent) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.andrill.conop.core.Event#getAfterConstraint()
	 */
	@Override
	public DefaultEvent getAfterConstraint() {
		return after;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.andrill.conop.core.Event#getBeforeConstraint()
	 */
	@Override
	public DefaultEvent getBeforeConstraint() {
		return before;
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
