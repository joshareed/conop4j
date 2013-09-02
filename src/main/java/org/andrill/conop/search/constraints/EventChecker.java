package org.andrill.conop.search.constraints;

import java.util.Set;

import org.andrill.conop.search.AbstractConfigurable;
import org.andrill.conop.search.Event;
import org.andrill.conop.search.Simulation;
import org.andrill.conop.search.Solution;

import com.google.common.collect.Sets;

/**
 * Ensures all event constraints are satisfied.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class EventChecker extends AbstractConfigurable implements ConstraintChecker {
	protected Set<Event> constrained = Sets.newHashSet();

	@Override
	public void configure(final Simulation simulation) {
		super.configure(simulation);

		// find minimum number of events to fully check all constraints
		for (Event e : simulation.getRun().getEvents()) {
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
