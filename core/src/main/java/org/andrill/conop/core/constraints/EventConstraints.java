package org.andrill.conop.core.constraints;

import java.util.Set;

import org.andrill.conop.core.AbstractConfigurable;
import org.andrill.conop.core.Event;
import org.andrill.conop.core.Run;
import org.andrill.conop.core.Solution;

import com.google.common.collect.Sets;

/**
 * Ensures all event constraints are satisfied.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class EventConstraints extends AbstractConfigurable implements Constraints {
	protected Set<Event> constrained = null;

	protected void initialize(final Run run) {
		constrained = Sets.newHashSet();

		for (Event e : run.getEvents()) {
			Event before = e.getBeforeConstraint();
			Event after = e.getAfterConstraint();
			if ((before != null) && ((before.getAfterConstraint() != e) || !constrained.contains(before))) {
				constrained.add(e);
			}
			if ((after != null) && ((after.getBeforeConstraint() != e) || !constrained.contains(after))) {
				constrained.add(e);
			}
		}
	}

	@Override
	public boolean isValid(final Solution solution) {
		if (constrained == null) {
			initialize(solution.getRun());
		}

		// check our constraints
		for (Event e : constrained) {
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
