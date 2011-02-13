package org.andrill.conop.search.constraints;

import org.andrill.conop.search.AbstractConfigurable;
import org.andrill.conop.search.Event;
import org.andrill.conop.search.Solution;

/**
 * Ensures all event constraints are satisfied.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class EventChecker extends AbstractConfigurable implements
		ConstraintChecker {

	@Override
	public boolean isValid(final Solution solution) {
		for (Event e : solution.getEvents()) {
			int position = solution.getPosition(e);
			Event before = e.getBeforeConstraint();
			if ((before != null) && (position > solution.getPosition(before))) {
				return false;
			}
			Event after = e.getAfterConstraint();
			if ((after != null) && (position < solution.getPosition(after))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "Event";
	}
}
